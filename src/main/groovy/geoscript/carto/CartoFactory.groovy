package geoscript.carto

/**
 * A CartoFactory can create a CartoBuilder with a given PageSize.
 * @author Jared Erickson
 */
interface CartoFactory {

    /**
     * Create a CartoBuilder with the given PageSize
     * @param pageSize The PageSize
     * @return A CartoBuilder
     */
    CartoBuilder create(PageSize pageSize)

    /**
     * The CartoFactory name
     * @return The name of the CartoFactory
     */
    String getName()

    /**
     * The CartoFactory mime type
     * @return The mime type of the CartoFactory
     */
    String getMimeType()

}
