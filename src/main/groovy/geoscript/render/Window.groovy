package geoscript.render

import java.awt.Panel
import java.awt.Frame
import java.awt.image.BufferedImage
import java.awt.Graphics

/**
 * A Renderer that displays a simple GUI.
 * @author Jared Erickson
 */
class Window extends Renderer {

    /**
     * Encode the BufferedImage as a GUI
     * @param img The BufferedImage
     * @param g The Java2D Graphics
     * @param size The size of the canvas
     * @param options The additional options
     */
    @Override
    protected void encode(BufferedImage img, Graphics g, List size, java.util.Map options) {
        Panel p = new MapPanel(img, size)
        Frame f = new Frame()
        f.windowClosing = {e ->
            e.window.dispose()
        }
        if (options.containsKey("title")) {
            f.title = options["title"]
        }
        f.add(p)
        f.pack()
        f.visible = true
    }

    /**
     * The MapPanel is a custom Panel that displays our Renderered Image.
     * @author Jared Erickson
     */
    private static class MapPanel extends Panel {

        /**
         * The renderer image
         */
        private BufferedImage image

        /**
         * Create a new MapPanel with an image and a size
         * @param img The BufferedImage
         * @param size The size of the image
         */
        MapPanel(BufferedImage img, List size) {
            preferredSize = new java.awt.Dimension(size[0], size[1])
            this.image = img
        }

        /**
         * When painting this Panel, just draw the image
         * @param g The Java2D graphics
         */
        @Override
        void paint(Graphics g) {
            g.drawImage(image, 0, 0, this)
        }
    }
}