import geoscript.geom.*
import geoscript.feature.*
import geoscript.layer.Layer
import geoscript.workspace.*
import groovy.json.JsonSlurper

interface Geocoder {
    Layer geocode(String value)
}

interface ReverseGeocoder {
    Layer reverseGeocode(Point pt)
}

class NominatimGeocoder implements Geocoder {
    
    Layer geocode(String value) {
        URL url = new URL("http://nominatim.openstreetmap.org/search?format=json&q=${value}")
        String response = url.text
        JsonSlurper jsonSlurper = new JsonSlurper()
        def results = jsonSlurper.parseText(response)
        Schema schema = new Schema("geocode", [
            new Field("geom","Point","EPSG:4326"),
            new Field("value","String"),
            new Field("class","String"),
            new Field("type","String")
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)
        layer.withWriter { writer ->
            results.each { result ->
                Feature feature = writer.newFeature
                feature.set([
                    geom: new Point(result.lon as double, result.lat as double),
                    value: result.display_name,
                    class: result.class,
                    type: result.type
                ])
                writer.add(feature)
            }
        }
        layer
    }

}

class NominatimReverseGeocoder implements ReverseGeocoder {

    Layer reverseGeocode(Point pt) {
        URL url = new URL("http://nominatim.openstreetmap.org/reverse?format=json&lon=${pt.x}&lat=${pt.y}&zoom=18")
        String response = url.text
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(response)
        Schema schema = new Schema("geocode", [
                new Field("geom","Point","EPSG:4326"),
                new Field("value","String"),
                new Field("housenumber","String"),
                new Field("road","String"),
                new Field("suburb","String"),
                new Field("city","String"),
                new Field("county","String"),
                new Field("state","String"),
                new Field("postcode","String"),
                new Field("country","String"),
                new Field("countrycode","String")
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)
        layer.withWriter { writer ->
            Feature feature = writer.newFeature
            feature.set([
                    geom: new Point(result.lon as double, result.lat as double),
                    value: result.display_name,
                    housenumber: result.address.house_number,
                    road: result.address.road,
                    suburb: result.address.suburb,
                    city: result.address.city,
                    county: result.address.county,
                    state: result.address.state,
                    postcode: result.address.postcode,
                    country: result.address.country,
                    countrycode: result.address.country_code
            ])
            writer.add(feature)
        }
        layer
    }

}

Geocoder geocoder = new NominatimGeocoder()
Layer result = geocoder.geocode("Seattle, WA")
println "Geocoder found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}

ReverseGeocoder reverseGeocoder = new NominatimReverseGeocoder()
result = reverseGeocoder.reverseGeocode(new Point(-122.384515,47.575863))
println "ReverseGeocoder Found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}

