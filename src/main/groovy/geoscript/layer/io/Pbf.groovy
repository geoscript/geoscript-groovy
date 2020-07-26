package geoscript.layer.io

import no.ecc.vectortile.VectorTileDecoder
import no.ecc.vectortile.VectorTileEncoder
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.operation.transform.ProjectiveTransform
import org.geotools.renderer.lite.RendererUtilities
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry as JtsGeometry
import org.locationtech.jts.geom.GeometryFactory
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.workspace.Memory
import org.geotools.renderer.crs.ProjectionHandler
import org.geotools.renderer.crs.ProjectionHandlerFinder
import org.opengis.referencing.operation.MathTransform

import java.awt.Rectangle

/**
 * A MapBox Vector Tile Reader and Writer
 */
class Pbf {

    /**
     * Read a List of Layers
     * @param options The optional named parameters
     * <ul>
     *    <li>proj = The Projection (defaults to EPSG:3857)</li>
     *    <li>size = The tile size (defaults to 256)</li>
     * </ul>
     * @param byte The array of bytes
     * @param b The Bounds
     */
    static List<Layer> read(Map options = [:], byte[] bytes, Bounds b) {

        ProjectionHandler projectionHandler = ProjectionHandlerFinder.getHandler(b.env, b.proj.crs, true)
        Projection proj = options.get("proj", new Projection("EPSG:3857"))
        int tileSize = options.get("tileSize", 256)
        int extent = options.get("extent", 4096)

        Map<String, Layer> layers = [:]
        VectorTileDecoder vectorTileDecoder = new VectorTileDecoder()
        VectorTileDecoder.FeatureIterable  features = vectorTileDecoder.decode(bytes)
        features.iterator().each { VectorTileDecoder.Feature vectorTileFeature ->
            String layerName = vectorTileFeature.layerName
            Geometry geometry = fromPixel(Geometry.wrap(vectorTileFeature.geometry), b, tileSize, extent)
            if (!geometry.isEmpty()) {
                if (projectionHandler) {
                    JtsGeometry processedGeom = projectionHandler.preProcess(geometry.g)
                    if (processedGeom) {
                        geometry = Geometry.wrap(processedGeom)
                    }
                }
                Map<String, Object> attributes = [geometry: geometry]
                attributes.putAll(vectorTileFeature.attributes)
                if (!layers.containsKey(layerName)) {
                    Feature feature = new Feature(attributes, "1")
                    Schema schema = new Schema(layerName, feature.schema.fields).reproject(proj)
                    layers[layerName] = new Memory().create(schema)
                }
                Layer layer = layers[layerName]
                Feature feature = layer.schema.feature(attributes)
                layer.add(feature)
            }
        }

        layers.values().toList()
    }

    /**
     * Convert a Geometry from pixel coordinates to geographic coordinates
     * @param g The Geometry in pixel coordinates
     * @param b The Bounds in geographic coordinates
     * @param tileSize The tile size
     * @return A Geometry in geographic coordinates
     */
    private static Geometry fromPixel(Geometry g, Bounds b, int size, int extent) {
        // Clone the Geometry in pixel coordinates
        GeometryFactory factory = new GeometryFactory()
        Geometry gpx = Geometry.wrap(factory.createGeometry(g.g))
        // Convert a Coordinate from pixel to geographic
        gpx.coordinates.each { Coordinate c ->
            c.y = extent - c.y
            double px = c.x / extent
            double py = c.y / extent
            c.x = b.minX + (b.width * px)
            c.y = b.minY + (b.height * py)
        }
        gpx
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
        Map<String, List> subFields = options.get("subFields")

        Envelope clipEnvelope = new Envelope(b.env)
        final double bufferWidth = b.env.width * 0.1f
        final double bufferHeight = b.env.height * 0.1f
        clipEnvelope.expandBy(bufferWidth, bufferHeight)

        VectorTileEncoder encoder = new VectorTileEncoder()
        layers.each { Layer layer ->
            Bounds projectedBounds = b.reproject(layer.proj)
            projectedBounds.expandBy(projectedBounds.width * 0.1f)
            Geometry boundsGeom = projectedBounds.geometry
            boolean isPoint = layer.schema.geom.typ.equalsIgnoreCase("point")
            // Intersects
            List<JtsGeometry> geometries = []
            layer.eachFeature(Filter.intersects(layer.schema.geom.name, boundsGeom), { Feature f ->
                // Clip
                Geometry geom = isPoint ? f.geom : f.geom.intersection(boundsGeom)
                if (!geom.empty) {
                    // Process
                    ProjectionHandler projectionHandler = ProjectionHandlerFinder.getHandler(b.env, layer.proj.crs, true)
                    if (projectionHandler) {
                        JtsGeometry processedGeom = projectionHandler.preProcess(geom.g)
                        if (processedGeom) {
                            geom = Geometry.wrap(processedGeom)
                        }
                    }
                    // Reproject
                    JtsGeometry geometry = Projection.transform(geom, layer.proj, b.proj).g
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
                    geometry.userData = attributes
                    // To Screen
                    MathTransform worldToScreenTransform = ProjectiveTransform.create(RendererUtilities.worldToScreenTransform(b.env, new Rectangle(0,0, tileSize, tileSize)))
                    geometry = JTS.transform(geometry, worldToScreenTransform)
                    //Encode
                    encoder.addFeature(layer.name, attributes, geometry)
                }
            })
        }

        encoder.encode()
    }

}