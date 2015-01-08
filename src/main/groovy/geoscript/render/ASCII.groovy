package geoscript.render

import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.util.List

/**
 * A Renderer that outputs ASCII art maps.
 * @author Jared Erickson
 */
class ASCII extends Renderer<String> {

    /**
     * The Image Renderer
     */
    Image renderer = new PNG()

    /**
     * The List of characters used to display the map
     */
    List characters = ['@', '#', '%', '$', '*', '+', '-', ':', ')', '^', '(', '?', '!', ':', '.']

    /**
     * The maximum output width
     */
    int width = 50

    /**
     * The new line separator
     */
    private static final String NEW_LINE = System.getProperty("line.separator")

    /**
     * Render the Map to a Type
     * @param map The Map
     * @return The Type
     */
    @Override
    String render(Map map) {
        // Render the Map to an image
        BufferedImage image = renderer.render(map)
        // Convert the image to Gray scale and resize it
        int imageWidth = image.width
        int imageHeight = image.height
        double ratio = (imageHeight as double) / (imageWidth as double)
        int height = ratio * width
        BufferedImage grayScaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        Graphics2D g = grayScaleImage.getGraphics()
        g.drawImage(image, 0, 0, width, height, null)
        g.dispose()
        // Convert pixels by mapping value to characters
        List chars = []
        Raster raster = grayScaleImage.data
        (0..<grayScaleImage.height).each { int r ->
            List row = []
            (0..<grayScaleImage.width).each { int c ->
                int value = raster.getSample(c, r, 0)
                int p = value / (255 / characters.size() + 1)
                row.add(characters[p])
            }
            chars.add(row.join(""))
        }
        chars.join(NEW_LINE)
    }

    /**
     * Render the Map to the OutputStream
     * @param map The Map
     * @param out The OutputStream
     */
    @Override
    void render(Map map, OutputStream out) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream()
        out.write(render(map).bytes)
        bout.close()
    }
}
