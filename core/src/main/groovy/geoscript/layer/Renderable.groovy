package geoscript.layer

import geoscript.geom.Bounds
import org.geotools.map.Layer as GtLayer

/**
 * An interface for marking a class as Renderable.
 * @author Jared Erickson
 */
interface Renderable {

    /**
     * Get a List of Map Layers (GeoTools org.geotools.map.Layer) for the given Bounds and map size
     * @param bounds The Bounds
     * @param size The map size (width and height)
     * @return
     */
    List<GtLayer> getMapLayers(Bounds bounds, List size)

}
