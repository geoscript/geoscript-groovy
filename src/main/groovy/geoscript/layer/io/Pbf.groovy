package geoscript.layer.io

import com.vividsolutions.jts.algorithm.CGAlgorithms
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.LinearRing
import com.vividsolutions.jts.geom.Polygon
import com.wdtinc.mapbox_vector_tile.VectorTile
import com.wdtinc.mapbox_vector_tile.adapt.jts.IGeometryFilter
import com.wdtinc.mapbox_vector_tile.adapt.jts.JtsAdapter
import com.wdtinc.mapbox_vector_tile.adapt.jts.TagKeyValueMapConverter
import com.wdtinc.mapbox_vector_tile.adapt.jts.TileGeomResult
import com.wdtinc.mapbox_vector_tile.adapt.jts.UserDataKeyValueMapConverter
import com.wdtinc.mapbox_vector_tile.adapt.jts.model.JtsLayer
import com.wdtinc.mapbox_vector_tile.adapt.jts.model.JtsMvt
import com.wdtinc.mapbox_vector_tile.adapt.jts.MvtReader as JtsMvtReader
import com.wdtinc.mapbox_vector_tile.build.MvtLayerBuild
import com.wdtinc.mapbox_vector_tile.build.MvtLayerParams
import com.wdtinc.mapbox_vector_tile.build.MvtLayerProps
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.workspace.Memory

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
        int extent = options.get("extent", 4096)
        List<Layer> layers = []
        GeometryFactory geometryFactory = new GeometryFactory()
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)
        JtsMvt jtsMvt = JtsMvtReader.loadMvt(inputStream, geometryFactory, new TagKeyValueMapConverter(), new NonValidatingRingClassifier())

        jtsMvt.layers.each { JtsLayer jtsLayer ->
            String name = jtsLayer.name
            Layer layer
            Schema schema
            jtsLayer.geometries.eachWithIndex { JtsGeometry jtsGeometry, int i ->
                Geometry geometry = fromPixel(Geometry.wrap(jtsGeometry), b, tileSize, extent)
                Map properties = jtsGeometry.userData as Map
                properties.put("geometry", geometry)
                Feature feature
                if (!schema) {
                    feature = new Feature(properties, "1")
                    schema = new Schema(name, feature.schema.fields).reproject(proj)
                    layer = new Memory().create(schema)
                } else {
                    feature = schema.feature(properties)
                }
                layer.add(feature)
            }
            layers.add(layer)
        }
        layers
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
            // Convert from 0 - extent (4096) to 0 - tileSize (256)
            double tileX = size * (c.x / extent)
            double tileY = size * (c.y / extent)
            // Determine percent based on tile size
            double px = tileX / size
            double py = tileY / size
            // Convert from pixel to geographic
            c.x = b.minX + (b.width * px)
            c.y = b.maxY - (b.height * py)
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

        VectorTile.Tile.Builder tileBuilder = VectorTile.Tile.newBuilder()
        MvtLayerParams mvtParams = MvtLayerParams.DEFAULT
        GeometryFactory geometryFactory = new GeometryFactory()
        IGeometryFilter geometryFilter = new IGeometryFilter() {
            @Override
            boolean accept(com.vividsolutions.jts.geom.Geometry geometry) {
                true
            }
        }

        layers.each { Layer layer ->
            Bounds projectedBounds = b.reproject(layer.proj)
            projectedBounds.expandBy(projectedBounds.width * 0.1f)
            Geometry boundsGeom = projectedBounds.geometry
            boolean isPoint = layer.schema.geom.typ.equalsIgnoreCase("point")
            VectorTile.Tile.Layer.Builder layerBuilder = MvtLayerBuild.newLayerBuilder(layer.name, mvtParams)
            MvtLayerProps layerProps = new MvtLayerProps()
            List<JtsGeometry> geometries = []
            layer.eachFeature(Filter.intersects(boundsGeom), { Feature f ->
                Geometry geom = isPoint ? f.geom : f.geom.intersection(boundsGeom)
                if (!geom.empty) {
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

                    TileGeomResult tileGeom = JtsAdapter.createTileGeom([geometry], b.env, clipEnvelope, geometryFactory, mvtParams, geometryFilter);
                    geometries.addAll(tileGeom.mvtGeoms)
                }
            })

            List<VectorTile.Tile.Feature> features = JtsAdapter.toFeatures(geometries, layerProps, new UserDataKeyValueMapConverter())
            layerBuilder.addAllFeatures(features);
            MvtLayerBuild.writeProps(layerBuilder, layerProps);

            VectorTile.Tile.Layer vtLayer = layerBuilder.build()
            tileBuilder.addLayers(vtLayer)
        }

        VectorTile.Tile tile = tileBuilder.build()
        byte[] bytes = tile.toByteArray()
        bytes
    }

    private static final class NonValidatingRingClassifier implements com.wdtinc.mapbox_vector_tile.adapt.jts.MvtReader.RingClassifier {

        @Override
        public List<Polygon> classifyRings(List<LinearRing> rings, GeometryFactory geomFactory) {

            final List<Polygon> polygons = new ArrayList<>();
            final List<LinearRing> holes = new ArrayList<>();

            double outerArea = 0d;
            LinearRing outerPoly = null;

            for(LinearRing r : rings) {

                double area = CGAlgorithms.signedArea(r.getCoordinates());

                if(area == 0d) {
                    continue; // zero-area
                }

                if(area > 0d) {
                    if(outerPoly != null) {
                        polygons.add(geomFactory.createPolygon(outerPoly, holes.toArray(new LinearRing[holes.size()])));
                        holes.clear();
                    }

                    // Pos --> CCW, Outer
                    outerPoly = r;
                    outerArea = area;
                } else {

                    if(Math.abs(outerArea) < Math.abs(area)) {
                        continue; // Holes must have less area, could probably be handled in a isSimple() check
                    }

                    // Neg --> CW, Hole
                    holes.add(r);
                }
            }

            if(outerPoly != null) {
                holes.toArray();
                polygons.add(geomFactory.createPolygon(outerPoly, holes.toArray(new LinearRing[holes.size()])));
            }

            return polygons;
        }

    }

}