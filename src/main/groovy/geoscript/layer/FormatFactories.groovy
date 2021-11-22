package geoscript.layer

/**
 * A Utility for getting a List of all FormatFactories
 * @author Jared Erickson
 */
class FormatFactories {

    /**
     * Get a List of FormatFactories
     * @return A List of FormatFactories
     */
    static List<FormatFactory> list() {
        ServiceLoader.load(FormatFactory.class).iterator().collect()
    }

}
