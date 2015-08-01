import geoscript.geom.*
import geoscript.feature.*
import geoscript.layer.Layer
import geoscript.layer.io.GeoJSONReader
import geoscript.workspace.*
import groovy.json.JsonSlurper

interface Geocoder {
    Layer geocode(String value)
}

interface ReverseGeocoder {
    Layer reverseGeocode(Point pt)
}

class NominatimGeocoder implements Geocoder {

    private final String baseUrl

    NominatimGeocoder() {
        this("http://nominatim.openstreetmap.org/search")
    }

    NominatimGeocoder(String baseUrl) {
        this.baseUrl = baseUrl
    }

    Layer geocode(Map options = [:], String value) {
        // Optional params
        List countryCodes = options.get("countrycodes")
        Bounds bounds = options.get("bounds")
        boolean bounded = options.get("bounded", false)
        int limit = options.get("limit", -1)
        // Build the URL
        String urlStr = "${baseUrl}?format=json&q=${value}"
        if (countryCodes) {
            urlStr += "&countrycodes=${countryCodes.join(',')}"
        }
        if (bounds) {
            urlStr += "&viewbox=${bounds.minX},${bounds.maxY},${bounds.maxX},${bounds.minY}"
        }
        if (bounded) {
            urlStr += "&bounded=1"
        }
        if (limit > -1) {
            urlStr += "&limit=${limit}"
        }
        URL url = new URL(urlStr)
        // Make the request and parse the results
        String response = url.text
        JsonSlurper jsonSlurper = new JsonSlurper()
        def results = jsonSlurper.parseText(response)
        // Create the Layer
        Schema schema = new Schema("geocode", [
            new Field("geom","Point","EPSG:4326"),
            new Field("value","String"),
            new Field("class","String"),
            new Field("type","String")
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)
        // Add the results
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

class MapQuestGeocoder implements Geocoder {

    private final String key

    MapQuestGeocoder(String key) {
        this.key = key
    }

    Layer geocode(Map options = [:], String value) {
        int maxresults = options.get("maxResults", -1)
        boolean thumbMaps = options.get("thumbMaps", false)
        Bounds bounds = options.get("bounds")
        String urlStr = "http://open.mapquestapi.com/geocoding/v1/address?key=${key}&location=${value.replaceAll(' ', '%20')}&maxResults=${maxresults}&thumbMaps=${thumbMaps ? 'true' : 'false'}"
        if (bounds) {
            urlStr += "&boundingBox=${bounds.maxY},${bounds.minX},${bounds.minY},${bounds.maxX}"
        }
        URL url = new URL(urlStr)
        // Make the request and parse the results
        String response = url.text
        JsonSlurper jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parseText(response)
        // Create the Layer
        Schema schema = new Schema("geocode", [
                new Field("geom","Point","EPSG:4326"),
                new Field("value","String")
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)
        // Add the results
        layer.withWriter { writer ->
            json.results.each { result ->
                result.locations.each { location ->
                    Feature feature = writer.newFeature
                    feature.set([
                            geom : new Point(location.latLng.lng as double, location.latLng.lat as double),
                            value: "${location.street} ${location.adminArea5}, ${location.adminArea3} ${location.postalCode}"
                    ])
                    writer.add(feature)
                }
            }
        }
        layer
    }

}

class PeliasGeocoder implements Geocoder {

    private final String baseUrl

    PeliasGeocoder() {
        this("http://pelias.mapzen.com")
    }

    PeliasGeocoder(String baseUrl) {
        this.baseUrl = baseUrl
    }

    Layer geocode(Map options = [:], String value) {
        // Optional params
        Point pt = options.get("point")
        Integer zoom = options.get("zoom")
        Integer size = options.get("size",10)
        Bounds bounds = options.get("bounds")
        List layers = options.get("layers")
        boolean details = options.get("details", true)
        // Build the URL
        String urlStr = "${baseUrl}/search?input=${java.net.URLEncoder.encode(value)}"
        if (pt) {
            urlStr += "&lon=${pt.x}&lat=${pt.y}"
        }
        if (zoom) {
            urlStr += "&zoom=${zoom}"
        }
        if (size) {
            urlStr += "&size=${size}"
        }
        if (layers) {
            urlStr += "&layers=${layers.join(',')}"
        }
        if (bounds) {
            urlStr += "&bbox=${bounds.minX},${bounds.maxY},${bounds.maxX},${bounds.minY}"
        }
        if (details) {
            urlStr += "&details=${details}"
        }
        URL url = new URL(urlStr)
        // Make the request and parse the results
        String response = url.text
        GeoJSONReader reader = new GeoJSONReader()
        reader.read(response)
    }

}

class NominatimReverseGeocoder implements ReverseGeocoder {

    private final String baseUrl

    NominatimReverseGeocoder() {
        this("http://nominatim.openstreetmap.org/reverse")
    }

    NominatimReverseGeocoder(String baseUrl) {
        this.baseUrl = baseUrl
    }

    Layer reverseGeocode(Map options = [:], Point pt) {
        int zoom = options.get("zoom", 18)
        URL url = new URL("${baseUrl}?format=json&lon=${pt.x}&lat=${pt.y}&zoom=${zoom}")
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

class MapQuestReverseGeocoder implements ReverseGeocoder {

    private final String key

    MapQuestReverseGeocoder(String key) {
        this.key = key
    }

    Layer reverseGeocode(Map options = [:], Point pt) {
        URL url = new URL("http://open.mapquestapi.com/geocoding/v1/reverse?key=${key}&location=${pt.y},${pt.x}")
        String response = url.text
        JsonSlurper jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parseText(response)
        Schema schema = new Schema("geocode", [
                new Field("geom","Point","EPSG:4326"),
                new Field("value","String")
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)
        layer.withWriter { writer ->
            json.results.each { result ->
                result.locations.each { location ->
                    Feature feature = writer.newFeature
                    feature.set([
                            geom: new Point(location.latLng.lng as double, location.latLng.lat as double),
                            value: "${location.street} ${location.adminArea5} ${location.adminArea3} ${location.postalCode}"
                    ])
                    writer.add(feature)
                }
            }
        }
        layer
    }

}

class PeliasReverseGeocoder implements ReverseGeocoder {

    private final String baseUrl

    PeliasReverseGeocoder() {
        this("http://pelias.mapzen.com")
    }

    PeliasReverseGeocoder(String baseUrl) {
        this.baseUrl = baseUrl
    }

    Layer reverseGeocode(Map options = [:], Point pt) {
        // Optional params
        Integer zoom = options.get("zoom")
        List layers = options.get("layers")
        boolean details = options.get("details", true)
        // Build the URL
        String urlStr = "${baseUrl}/reverse?lon=${pt.x}&lat=${pt.y}"
        if (zoom) {
            urlStr += "&zoom=${zoom}"
        }
        if (layers) {
            urlStr += "&layers=${layers.join(',')}"
        }
        if (details) {
            urlStr += "&details=${details}"
        }
        URL url = new URL(urlStr)
        // Make the request and parse the results
        String response = url.text
        GeoJSONReader reader = new GeoJSONReader()
        reader.read(response)
    }

}

Geocoder geocoder = new NominatimGeocoder()
Layer result = geocoder.geocode("Seattle, WA")
println "Nominatim Geocoder found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}
println ""

geocoder = new MapQuestGeocoder("Fmjtd%7Cluu821utng%2C80%3Do5-94b50r")
result = geocoder.geocode("950 Fawcett Tacoma WA")
println "MapQuest Geocoder found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}
println ""

geocoder = new PeliasGeocoder()
result = geocoder.geocode("950 South Fawcett Ave Tacoma WA")
println "Pelias Geocoder found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}
println ""

ReverseGeocoder reverseGeocoder = new NominatimReverseGeocoder()
result = reverseGeocoder.reverseGeocode(new Point(-122.384515,47.575863))
println "Nominatim ReverseGeocoder Found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}
println ""

reverseGeocoder = new MapQuestReverseGeocoder("Fmjtd%7Cluu821utng%2C80%3Do5-94b50r")
result = reverseGeocoder.reverseGeocode(new Point(-122.384515,47.575863))
println "MapQuest ReverseGeocoder Found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}
println ""

reverseGeocoder = new PeliasReverseGeocoder()
result = reverseGeocoder.reverseGeocode(new Point(-122.384515,47.575863))
println "Pelias ReverseGeocoder Found ${result.count} results:"
result.eachFeature { Feature f ->
    println f
}
println ""

