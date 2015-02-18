import geoscript.feature.*
import geoscript.geom.LineString
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.workspace.*
import groovy.json.JsonSlurper

interface Router {
    Layer route(Point start, Point end)
}


class MapQuestRouter implements Router {

    private String key

    MapQuestRouter(String key) {
        this.key = key
    }

    Layer route(Point start, Point end) {

        // Call the web service and parse the result
        URL url = new URL("http://open.mapquestapi.com/directions/v2/route?key=${key}&from={latLng:{lat:${start.y},lng:${start.x}}}&to={latLng:{lat:${end.y},lng:${end.x}}}&shapeFormat=raw&generalize=0")
        println url
        String response = url.text
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(response)

        // Shapes
        List indexes = result.route.shape.maneuverIndexes.collect { it as int}
        List points = result.route.shape.shapePoints.collate(2).collect{ List xy ->
            new Point(xy[1], xy[0])
        }
        List<LineString> lines = (0..<indexes.size() - 1).collect { int i ->
            int from = indexes[i]
            int to = indexes[i + 1] + 1
            new LineString(points.subList(from, to))
        }

        // Create Layer
        Schema schema = new Schema("route", [
                new Field("geom","LineString","EPSG:4326"),
                new Field("narrative","String"),
                new Field("distance","String")
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)

        // Populate the Layer with route instructions
        layer.withWriter { writer ->
            result.route.legs.each { leg ->
                leg.maneuvers.eachWithIndex { maneuver, int m ->
                    LineString lineString = lines[m - 1]
                    println lineString
                    Feature feature = writer.newFeature
                    feature.set([
                        geom : lineString,
                        narrative: maneuver.narrative,
                        distance: maneuver.distance
                    ])
                    writer.add(feature)
                }
            }
        }

        layer
    }

}

Router router = new MapQuestRouter("Fmjtd%7Cluu821utng%2C80%3Do5-94b50r")
Layer layer = router.route(new Point(-122.384515, 47.575863), new Point(-122.4433517, 47.2532732))
layer.eachFeature { Feature f ->
    println f
}