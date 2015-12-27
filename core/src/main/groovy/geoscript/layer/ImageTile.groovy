package geoscript.layer

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * A Tile that is an image.
 * @author Jared Erickson
 */
class ImageTile extends Tile {

    /**
     * Create a new Tile with no data
     * @param z The zoom level
     * @param x The x or column
     * @param y The y or row
     */
    ImageTile(long z, long x, long y) {
        super(z, x, y)
    }

    /**
     * Create a new Tile with data
     * @param z The zoom level
     * @param x The x or column
     * @param y The y or row
     * @param data The array of bytes
     */
    ImageTile(long z, long x, long y, byte[] data) {
        super(z, x, y, data)
    }

    /**
     * Get the data as a BufferedImage
     * @return A BufferedImage
     */
    BufferedImage getImage() {
        if (data == null) {
            null
        } else {
            InputStream input = new ByteArrayInputStream(data)
            BufferedImage image = ImageIO.read(input)
            input.close()
            image
        }
    }

}
