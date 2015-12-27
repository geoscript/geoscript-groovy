package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.layer.io.Writer as IOWriter

/**
 * A Vector TileRenderer that can write a single Layer using any gesocript.layer.io.Writer
 * @author Jared Erickson
 */
class VectorTileRenderer implements TileRenderer {

    /**
     * The Layer
     */
    private Layer layer

    /**
     * The List of Fields to include
     */
    private List fields

    /**
     * The geoscript.layer.io.Writer
     */
    private IOWriter writer

    /**
     * Create a new VectorTileRenderer
     * @param writer The geoscript.layer.io.Writer
     * @param layer The Layer
     * @param fields The List of Fields to include
     */
    VectorTileRenderer(IOWriter writer, Layer layer, List fields) {
        this.writer = writer
        this.layer = layer
        this.fields = fields
    }

    @Override
    byte[] render(Bounds b) {
        // Make sure the Geometry field is included
        if (!fields.contains(layer.schema.geom.name) && !fields.contains(layer.schema.geom)) {
            fields.add(layer.schema.geom)
        }
        // Get the subset of Features for each Layer
        Layer outLayer = new Layer(layer.name, layer.schema.includeFields(fields, layer.name))
        Bounds projectedBounds = b.reproject(layer.proj)
        Geometry boundsGeom = projectedBounds.geometry
        layer.eachFeature(Filter.intersects(boundsGeom), { Feature f ->
            // Crop the Geometry to the Bounds
            Geometry geom = f.geom.intersection(boundsGeom)
            // Collect the attributes
            Map attributes = [(outLayer.schema.geom.name): geom]
            fields.each { def fld ->
                attributes.put(fld instanceof Field ? fld.name : fld, f.get(fld))
            }
            // Add the Feature to the output Layer
            outLayer.add(outLayer.schema.feature(attributes))
        })
        // Write the output Layer of cropped Features using the Writer
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(outLayer, out)
        out.close()
        out.toByteArray()
    }
}
