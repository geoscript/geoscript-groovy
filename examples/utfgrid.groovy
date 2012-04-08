import geoscript.layer.*
import geoscript.geom.*
import geoscript.feature.*
import geoscript.filter.Filter
import org.json.*

class Request {
    int width
    int height
    Bounds bounds
    Request(int width, int height, Bounds bounds) {
        this.width = width
        this.height = height
        this.bounds = bounds
    }
}

class CoordTransform {
    Request request
    double offsetX
    double offsetY
    double sx
    double sy
    CoordTransform(Request request, double offsetX = 0.0, double offsetY = 0.0) {
        this.request = request
        this.offsetX = offsetX
        this.offsetY = offsetY
        this.sx = ((double)request.width) / request.bounds.width
        this.sy = ((double)request.height) / request.bounds.height
    }
    List forward(double x, double y) {
        double x0 = (x - request.bounds.minX) * sx - offsetX
        double y0 = (request.bounds.maxY - y) * sy - offsetY
        [x0,y0]
    }
    List backward(double x, double y) {
        double x0 = request.bounds.minX + (x + offsetX) / sx
        double y0 = request.bounds.maxY - (y + offsetY) / sy
        [x0,y0]
    }
}


class Grid {
    
    List rows = []
    Map featureCache = [:]
    int resolution

    Grid(int resolution = 4) {
        this.resolution = resolution
    }
    
    int width() {
        rows.size()
    }

    int height() {
        rows.size()
    }

    int escapeCodePoints(int codepoint) {
        int cp = codepoint
        if (codepoint == 34) {
            cp += 1
        } else if (codepoint == 92) {
            cp += 1
        }
        cp 
    }

    int decodeId(char codepoint) {
        int code = codepoint as int
        if (code >= 93) {
            code -= 1
        }
        if (code >= 35) {
            code -=1
        }
        code -= 32
        code
    }

    def encode() {
        Map keys = [:]
        List keyOrder = []
        Map data = [:]
        List utfRows = []
        int codepoint = 32
        (0..<height()).each{y ->
            String rowUtf = ""
            List row = rows[y]
            (0..<width()).each{x ->
                String featureId = row[x]
                if (keys.containsKey(featureId)) {
                    rowUtf += Character.toChars(keys[featureId])
                } else {
                    codepoint = escapeCodePoints(codepoint)
                    keys[featureId] = codepoint
                    keyOrder.add(featureId)
                    if (featureCache.containsKey(featureId)) {
                        data[featureId] = featureCache[featureId]
                    }
                    rowUtf += Character.toChars(codepoint)
                    codepoint += 1
                }
            }
            utfRows.add(rowUtf)
        }
        Map utf = [:]
        utf['grid'] = utfRows
        utf['keys'] = keyOrder.collect{key -> String.valueOf(key)}
        utf['data'] = data
        return utf
    }

}

class Renderer {
    
    private Grid grid

    private CoordTransform ctrans

    private Request request

    Renderer(Grid grid, CoordTransform ctrans) {
        this.grid = grid
        this.ctrans = ctrans
        this.request = ctrans.request
    }

    void apply(Layer layer, List fieldNames = []) {
        Schema schema = layer.schema
        List fields = []
        schema.fields.each{fld ->
            if (fieldNames.contains(fld.name)) {
                fields.add(fld)
            }
        }
        if (fields.size() == 0) {
            throw new Exception("No valid fields, field names = ${fieldNames}")
        }
        
        (0..<request.height).step(grid.resolution, { y ->
            def row = []
            (0..<request.width).step(grid.resolution, { x ->
               def (minx, maxy) = ctrans.backward(x,y)
               def (maxx, miny) = ctrans.backward(x+1, y+1)
               String wkt = "POLYGON ((${minx} ${miny}, ${minx} ${maxy}, ${maxx} ${maxy}, ${maxx} ${miny}, ${minx} ${miny}))"
               Geometry g = Geometry.fromWKT(wkt)
               boolean found = false
               layer.getCursor(Filter.intersects(g)).each{f ->
                    Geometry geom = f.geom
                    if (geom.intersects(g)) {
                        String featureId = f.id.split("\\.")[1]
                        row.add(featureId)
                        Map attr = [:]
                        fields.each{fld ->
                            attr[fld.name] = f.get(fld.name)
                        }
                        grid.featureCache[featureId] = attr
                        found = true
                    }
               }
               if (!found) {
                    row.add("")
               }

            })
            grid.rows.add(row)
        })
    }
}

def shp = new Shapefile("ne_110m_admin_0_countries.shp")
def box = new Bounds(-140, 0, -50, 90)
def tile = new Request(256, 256, box)
def ctrans = new CoordTransform(tile)
def grid = new Grid()
def renderer = new Renderer(grid, ctrans)
renderer.apply(shp, ["NAME_FORMA", "POP_EST"])
def utfgrid = grid.encode()
def json = new JSONObject(utfgrid)
println json
