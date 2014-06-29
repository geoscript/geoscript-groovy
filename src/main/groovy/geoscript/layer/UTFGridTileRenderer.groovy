package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.index.STRtree
import geoscript.index.SpatialIndex
import geoscript.proj.Projection
import groovy.json.JsonOutput

/**
 * A UTFGrid TileRenderer
 * @author Jared Erickson
 */
class UTFGridTileRenderer implements TileRenderer {

    /**
     * The UTFGrid TileLayer
     */
    private UTFGrid utfGrid

    /**
     * The Layer
     */
    private Layer layer

    /**
     * The List of Fields
     */
    private List fields

    /**
     * The resolution
     */
    private int resolution

    /**
     * The tile size
     */
    private List size

    /**
     * The ID Field
     */
    private Field idField

    /**
     * Whether to pretty print the JSON or not
     */
    private boolean prettyPrint

    /**
     * Whether to use in memory spatial index or not
     */
    private boolean useSpatialIndex

    /**
     * Create a new UTFGrid
     * @param options The optional named parameters
     * <ul>
     *    <li>size = The tile size. Defaults to [256,256]</li>
     *    <li>resolution = The grid cell size.  Defaults to 4</li>
     *    <li>prettyPrint = Whether to pretty print the JSON or not.  Defaults to true</li>
     *    <li>useSpatialIndex = Whether to use an in memory spatial index or not.  Defaults to true</li>
     *    <li>idField = The ID Field.  If not used, the Feature ID will be used.  Defaults to null</li>
     * </ul>
     * @param dir The directory
     * @param layer The Layer
     * @param fields A List of Fields
     */
    UTFGridTileRenderer(java.util.Map options = [:], UTFGrid utfGrid, Layer layer, List fields) {
        this.utfGrid = utfGrid
        this.layer = layer
        this.fields = fields
        this.size = options.get("size", [256,256])
        this.resolution = options.get("resolution", 4)
        this.prettyPrint = options.get("prettyPrint", true)
        this.useSpatialIndex = options.get("useSpatialIndex", true)
        this.idField = options.get("idField")
    }

    @Override
    byte[] render(Bounds b) {
        renderToString(b).bytes
    }

    /**
     * Render a UTF tile
     * @param bounds The Bounds
     * @return The UTF tile
     */
    String renderToString(Bounds bounds) {

        // Get the tile width and height
        int width = size[0]
        int height = size[1]

        double dx = (width as double) / bounds.width
        double dy = (height as double) / bounds.height

        // Put all of the Features in the input Layer in a spatial index
        SpatialIndex index = null
        if (useSpatialIndex) {
            index = new STRtree()
            layer.getCursor(filter: Filter.intersects(layer.schema.geom.name, bounds.geometry)).each { Feature f ->
                index.insert(f.bounds, f)
            }
        }

        // Get the ids and the field values for each cell
        List rows = []
        Map idValues = [:]
        (0..<height).step(resolution, { y ->
            def row = []
            (0..<width).step(resolution, { x ->
                Bounds b =  calculateBounds(x, y, bounds, dx, dy)
                Geometry g = b.geometry
                Feature f = null
                if (useSpatialIndex) {
                    f = index.query(b.reproject(layer.proj)).find { Feature feature ->
                        feature.geom.intersects(Projection.transform(g, utfGrid.proj, layer.proj))
                    }
                } else {
                    f = layer.first(filter: Filter.contains(layer.schema.geom.name, Projection.transform(g.centroid, utfGrid.proj, layer.proj)))
                }
                if (f) {
                    String id = idField ? f.get(idField) : f.id.split("\\.")[1]
                    row.add(id)
                    Map values = [:]
                    fields.each { fld ->
                        values[fld.name] = f.get(fld.name)
                    }
                    idValues[id] = values
                } else {
                    row.add("")
                }
            })
            rows.add(row)
        })

        List keys = []
        Map keyMap = [:]
        Map data = [:]
        List grid = []

        int codepoint = 32
        (0..<rows.size()).each { y ->
            String encodedRow = ""
            List row = rows[y]
            (0..<row.size()).each { x ->
                String id = row[x]
                if (keyMap.containsKey(id)) {
                    encodedRow += Character.toChars(keyMap[id])
                } else {
                    codepoint = encode(codepoint)
                    keyMap[id] = codepoint
                    keys.add(id)
                    if (idValues.containsKey(id)) {
                        data[id] = idValues[id]
                    }
                    encodedRow += Character.toChars(codepoint)
                    codepoint += 1
                }
            }
            grid.add(encodedRow)
        }
        Map utf = [:]
        utf['grid'] = grid
        utf['keys'] = keys.collect { key -> String.valueOf(key) }
        utf['data'] = data

        def builder = new groovy.json.JsonBuilder(utf)
        String json = builder.toString()
        if (prettyPrint) {
            json = JsonOutput.prettyPrint(json)
        }
        json
    }

    private Bounds calculateBounds(int x, int y, Bounds bounds, double dx, double dy) {
        new Bounds(
                bounds.minX + x / dx,
                bounds.maxY - (y + 1) / dy,
                bounds.minX + (x + 1) / dx,
                bounds.maxY - y / dy,
                bounds.proj
        )
    }

    private int encode(int codepoint) {
        int code = codepoint as int
        if (codepoint == 34) {
            code += 1
        } else if (codepoint == 92) {
            code += 1
        }
        code
    }
}
