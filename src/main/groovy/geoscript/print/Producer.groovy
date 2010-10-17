package geoscript.print

/**
 * A Producer can write a print Template to an OutputStream for a given
 * mime type
 * @author Jared Erickson
 */
interface Producer {

    /**
     * Does this Producer support the given mime type?
     * @param mimeType The mime type
     * @return Whether this Producer handles the mime type
     */
    boolean handlesMimeType(String mimeType)

    /**
     * Write the print Template to the OutputStream in the given mime type
     * @param template The print Template
     * @param mimeType The mime type
     * @param out The OutputStream
     */
    void produce(Template template, String mimeType, OutputStream out)

}

