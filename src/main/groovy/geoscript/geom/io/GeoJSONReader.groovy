package geoscript.geom.io

import org.json.*
import geoscript.geom.*

/**
 * Read a Geometry from a GeoJSON String.
 * <p><code>GeoJSONReader reader = new GeoJSONReader()</code></p>
 * <p><code>Point point = reader.read("""{ "type": "Point", "coordinates": [111.0, -47.0] }""")</code></p>
 * <p><code>POINT (111, -47)</code></p>
 * @author Jared Erickson
 */
class GeoJSONReader implements Reader {

    /**
     * Read a Geometry from a GeoJSON String
     * @param str The GeoJSON String
     * @return A Geometry
     */
    Geometry read(String str) {
        JSONObject jsonObject = new JSONObject(str)
        String type = jsonObject.getString("type")

        if (type.equalsIgnoreCase("Point")) {
            return getPoint(jsonObject.getJSONArray("coordinates"))
        }
        else if (type.equalsIgnoreCase("LineString")) {
            return new LineString(getPoints(jsonObject.getJSONArray("coordinates")))
        }
        else if (type.equalsIgnoreCase("Polygon")) {
            JSONArray array = jsonObject.getJSONArray("coordinates")
            int num = array.length()
            LinearRing shell = new LinearRing(getPoints(array.getJSONArray(0)))
            def holes = []
            if (num > 1) {
                holes = (1..num - 1).collect{i->
                    new LinearRing(getPoints(array.getJSONArray(i)))
                }
            }
            return new Polygon(shell, holes)
        }
        else if (type.equalsIgnoreCase("MultiPoint")) {
            return new MultiPoint(getPoints(jsonObject.getJSONArray("coordinates")))
        }
        else if (type.equalsIgnoreCase("MultiLineString")) {
            JSONArray array = jsonObject.getJSONArray("coordinates")
            int num = array.length()
            return new MultiLineString((0..num-1).collect{i->new LineString(getPoints(array.getJSONArray(i)))})
        }
        else if (type.equalsIgnoreCase("MultiPolygon")) {
            JSONArray array = jsonObject.getJSONArray("coordinates")
            int num = array.length()
            return new MultiPolygon((0..num-1).collect{i->
                    JSONArray array2 = array.getJSONArray(i)
                    LinearRing shell = new LinearRing(getPoints(array2.getJSONArray(0)))
                    def holes = []
                    int num2 = array2.length()
                    if (num2 > 1) {
                        holes = (1..num2 - 1).collect{k->
                            new LinearRing(getPoints(array2.getJSONArray(k)))
                        }
                    }
                    new Polygon(shell, holes)
            })
        }
        else if (type.equalsIgnoreCase("GeometryCollection")) {
            def geometries = []
            JSONArray array = jsonObject.getJSONArray("geometries")
            (0..array.length()-1).collect{i->
                geometries.add(read(array.getJSONObject(i).toString()))
            }
            return new GeometryCollection(geometries)
        }
    }

    /**
     * Get a List of Points from a JSONArray
     * @param jsonArray The JSONArray
     * @return A List of Points
     */
    private List<Point> getPoints(JSONArray jsonArray) {
        List<Point> points = []
        (0..jsonArray.length()-1).each{i->
            points.add(getPoint(jsonArray.getJSONArray(i)))
        }
        points
    }

    /**
     * Get a Point from a JSONArray
     * @param jsonArray The JSONArray
     * @return A Point
     */
    private Point getPoint(JSONArray jsonArray) {
        new Point(jsonArray.getDouble(0), jsonArray.getDouble(1))
    }

}

