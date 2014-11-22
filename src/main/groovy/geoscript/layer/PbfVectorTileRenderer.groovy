package geoscript.layer

import geoscript.geom.Bounds
import geoscript.layer.io.Pbf

/**
 * A TileRenderer that creates MapBox PBF Vector Tiles
 * @author Jared Erickson
 */
class PbfVectorTileRenderer implements TileRenderer {

    /**
     * The List of Layers
     */
    private List<Layer> layers

    /**
     * The Map of subfields by Layer name
     */
    private Map<String, List> fields

    /**
     * Create a new PbfVectorTileRenderer with a single Layer and a List of subfields
     * @param layer The Layer
     * @param fields The List of Fields to include
     */
    PbfVectorTileRenderer(Layer layer, List fields) {
        this([layer], [(layer.name): fields])
    }

    /**
     * Create a new PbfVectorTileRenderer with a List of Layers and a Map of sub fields
     * where the key is the Layer name and the value is a List of sub fields
     * @param layers The List of Layers
     * @param fields The Map of sub fields
     */
    PbfVectorTileRenderer(List<Layer> layers, Map<String, List> fields) {
        this.layers = layers
        this.fields = fields
    }

    /**
     * Renderer a Tile's data for a given Bounds
     * @param b The Bounds
     * @return The Tile's data
     */
    @Override
    byte[] render(Bounds b) {
        Pbf.write(layers, b, subFields: fields)
    }
}
