package geoscript.style

/**
 * A StyleRepository is a way to manage styles for layers.
 * @author Jared Erickson
 */
interface StyleRepository {

    /**
     * Get the default style for a Layer by name
     * @param layerName The layer name
     * @return The default style
     */
    String getDefaultForLayer(String layerName)

    /**
     * Get a style by Layer name and Style name
     * @param layerName The Layer name
     * @param styleName The Style name
     * @return The Style
     */
    String getForLayer(String layerName, String styleName)

    /**
     * Get all of the styles for a Layer Name.
     * @param layerName The Layer name
     * @return A List of style maps with layerName, styleName, and style keys.
     */
    List<Map<String, String>> getForLayer(String layerName)

    /**
     * Get all styles
     * @return A List of style maps with layerName, styleName, and style keys.
     */
    List<Map<String, String>> getAll()

    /**
     * Save a style for a Layer with a given name
     * @param layerName The layer name
     * @param styleName The style name
     * @param style The style
     * @param options A Map of options
     */
    void save(String layerName, String styleName, String style, Map options)

    /**
     * Delete the given style by layer and style name
     * @param layerName The layer name
     * @param styleName The style name
     */
    void delete(String layerName, String styleName)

}