package geoscript.layer

/**
 * A Utility for getting all TileLayerFactories.
 * @author Jared Erickson
 */
class TileLayerFactories {

    /**
     * Get a list of all TileLayerFactories
     * @return A List of TileLayerFactories
     */
    public static List<TileLayerFactory> list() {
        ServiceLoader.load(TileLayerFactory.class).iterator().collect()
    }

}
