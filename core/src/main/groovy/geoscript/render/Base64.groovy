package geoscript.render

/**
 * A Base64 Renderer
 * @author Jared Erickson
 */
class Base64 extends Renderer<String> {

    /**
     * The Image Renderer
     */
    Image renderer = new PNG()

    /**
     * Whether to include the base64 string prefix or not
     */
    boolean includePrefix = true

    /**
     * Render the Map to a Type
     * @param map The Map
     * @return The Type
     */
    @Override
    String render(Map map) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        renderer.render(map, out)
        byte[] bytes = org.apache.commons.codec.binary.Base64.encodeBase64(out.toByteArray())
        String str = new String(bytes, "UTF-8")
        if (includePrefix) {
            str = "image/${renderer.imageType};base64," + str
        }
        str
    }

    /**
     * Render the Map to the OutputStream
     * @param map The Map
     * @param out The OutputStream
     */
    @Override
    void render(Map map, OutputStream out) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream()
        renderer.render(map, bout)
        byte[] bytes = org.apache.commons.codec.binary.Base64.encodeBase64(bout.toByteArray())
        out.write(bytes)
    }
}
