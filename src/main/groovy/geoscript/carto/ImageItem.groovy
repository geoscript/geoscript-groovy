package geoscript.carto

/**
 * Add an image to a cartographic document
 * @author Jared Erickson
 */
class ImageItem extends Item {

    /**
     * The File or URL
     */
    Object path

    /**
     * Create an Image from the top left
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    ImageItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Load an Image from a file
     * @param file The image file
     * @return The ImageItem
     */
    ImageItem path(File file) {
        this.path = file
        this
    }

    @Override
    String toString() {
        "ImageItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, path = ${path})"
    }

}
