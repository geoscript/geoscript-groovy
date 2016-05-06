package geoscript.layer.io

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.layer.Layer
import geoscript.proj.Projection
import no.ecc.vectortile.VectorTileDecoder
import no.ecc.vectortile.VectorTileEncoder
import no.ecc.vectortile.VectorTileDecoder.Feature as VTFeature

/**
 * A MapBox Vector Tile Reader and Writer
 */
class Pbf {
    
    /**
     * Read a List of Layers
     * @param options The optional named parameters
     * <ul>
     *    <li>proj = The Projection (defaults to EPSG3857)</li>
     *    <li>size = The tile size (defaults to 256)</li>
     * </ul>
     * @param byte The array of bytes
     * @param b The Bounds
     */
    static List<Layer> read(Map options = [:], byte[] bytes, Bounds b) {
        Projection proj = options.get("proj", new Projection("EPSG:3857"))
        int tileSize = options.get("tileSize", 256)
        VectorTileDecoder decoder = new VectorTileDecoder()
        VectorTileDecoder.FeatureIterable fit = decoder.decode(bytes)
        Map<String, Layer> layers = [:]
        fit.iterator().each { VectorTileDecoder.Feature f ->
            if (!layers.containsKey(f.layerName)) {
                List<Field> fields = []
                fields.add(new Field("geom", f.geometry.geometryType, proj))
                f.attributes.each {String key, Object value ->
                    fields.add(new Field(key, value ? value.class.name : "String"))
                }
                // Create a Schema and Layer
                Schema schema = new Schema(f.layerName, fields)
                layers.put(f.layerName, new Layer(f.layerName, schema))
            }
            Layer layer = layers[f.layerName]
            Schema schema = layer.schema
            Map attributes = [:]
            attributes.put(schema.geom.name, fromPixel(Geometry.wrap(f.geometry), b, tileSize))
            attributes.putAll(f.attributes)
            layer.add(attributes)
        }
        layers.values() as List
    }
    
    /**
     * Write a List of Layers 
     * @param options The optional named parameters
     * <ul>
     *   <li>tileSize = The tile size (defaults to 256)</li>
     * </ul>
     * @param layers The List of Layers
     * @param b The Bounds
     * @return An array of bytes
     */
    static byte[] write(Map options = [:], List<Layer> layers, Bounds b) {
        int tileSize = options.get("tileSize", 256)
        Map<String,List> subFields = options.get("subFields")
        VectorTileEncoder encoder = new VectorTileEncoder()
        layers.each { Layer layer ->
            Bounds projectedBounds = b.reproject(layer.proj)
            Geometry boundsGeom = projectedBounds.geometry
            layer.eachFeature(Filter.intersects(boundsGeom), { Feature f ->
                Geometry geom = f.geom.intersection(boundsGeom)
                Geometry pixelGeom = toPixel(geom, projectedBounds, tileSize)
                Map attributes = [:]
                layer.schema.fields.each { Field fld ->
                    if (!fld.isGeometry()) {
                        if (subFields == null
                                || subFields.isEmpty()
                                || !subFields.containsKey(layer.name)
                                || subFields[layer.name].contains(fld)
                                || subFields[layer.name].contains(fld.name)) {
                            attributes.put(fld.name, f.get(fld))
                        }
                    }
                }
                encoder.addFeature(layer.name, attributes, pixelGeom.g)
            })
        }
        encoder.encode()
    }
   
    /**
     * Convert a Geometry from pixel coordinates to geographic coordinates
     * @param g The Geometry in pixel coordinates
     * @param b The Bounds in geographic coordinates
     * @param tileSize The tile size
     * @return A Geometry in geographic coordinates
     */
    private static Geometry fromPixel(Geometry g, Bounds b, int size) {
        // Clone the Geometry in pixel coordinates
        GeometryFactory factory = new GeometryFactory()
        Geometry gpx = Geometry.wrap(factory.createGeometry(g.g))
        // Convert a Coordinate from pixel to geographic
        gpx.coordinates.each { Coordinate c ->
            double px = c.x / size
            double py = c.y / size
            c.x = b.minX + (b.width * px)
            c.y = b.minY + (b.height * py)
        }
        gpx
    }

    /**
     * Convert a Geometry from geographic coordinates to pixel coordinates
     * @param g The Geometry in geographic coordinates
     * @param b The Bounds in geographic coordinates
     * @param tileSize The tile size
     * @return A Geometry in pixel coordinates
     */
    private static Geometry toPixel(Geometry g, Bounds b, int size) {
        // Clone the Geometry in pixel coordinates
        GeometryFactory factory = new GeometryFactory()
        Geometry gpx = Geometry.wrap(factory.createGeometry(g.g))
        // Convert a Coordinate from geographic to pixel
        gpx.coordinates.each { Coordinate c ->
            double px = (c.x - b.minX) / b.width
            double py = (c.y - b.minY) / b.height
            c.x = size * px
            c.y = size * py
        }
        gpx
    }

}
