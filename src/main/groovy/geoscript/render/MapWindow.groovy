package geoscript.render

import java.awt.image.BufferedImage
import java.awt.Graphics
import javax.swing.JFrame
import java.awt.BorderLayout
import org.geotools.renderer.lite.StreamingRenderer
import org.geotools.swing.JMapPane
import org.geotools.swing.control.JMapStatusBar
import org.geotools.swing.action.ZoomInAction
import org.geotools.swing.action.ZoomOutAction
import org.geotools.swing.action.PanAction
import org.geotools.swing.action.ResetAction
import javax.swing.JToolBar
import javax.swing.ButtonGroup
import javax.swing.JButton
import geoscript.geom.Bounds

/**
 * The MapWindow is a Renderer that draws a map to a interactive rich user interface.
 * @author Jared Erickson
 */
class MapWindow extends Renderer {

    /**
     * Render the List of Layers and Styles to a rich interactive user interface.
     * @param layers The List of Layers
     * @param styles The List of Styles
     * @param bounds The geographic Bounds
     * @param size The size of the GUI
     * @param options The additional options
     */
    @Override
    void render(List layers, List styles, Bounds bounds, List size, java.util.Map options) {

        // Create the GeoTools StreamingRenderer
        StreamingRenderer renderer = createStreamingRenderer(layers, styles, bounds, size, options)

        // Get the canvas width and height
        def (w,h) = size

        // Create a new JMapPane
        JMapPane mapPane = new JMapPane(renderer, map)
        mapPane.setSize(w,h)
        mapPane.visible = true

        // Create a JFrame to hold the JMapPane
        JFrame frame = new MapFrame(mapPane)
        frame.setSize(w,h)
        frame.visible = true
    }

    /**
     * Encode the BufferedImage
     * @param img The BufferedImage
     * @param g The Java2D Graphics
     * @param size The size of the canvas
     * @param options The additional options
     */
    @Override
    protected void encode(BufferedImage img, Graphics g, List size, java.util.Map options) {
        // Do nothing...really...
    }

    /**
     * The MapFrame is a custom JFrame that creates the interactive
     * user interface.
     * @author Jared Erickson
     */
    private static class MapFrame extends JFrame {

        /**
         * Create a new MapFrame
         * @param mapPane The JMapPane
         */
        MapFrame(JMapPane mapPane) {

            // Add the Map pane to the center
            add(mapPane, BorderLayout.CENTER)

            // Create and add a statusbar to the south
            JMapStatusBar statusBar = JMapStatusBar.createDefaultStatusBar(mapPane)
            add(statusBar, BorderLayout.SOUTH)

            // Create a toolbar
            JToolBar toolbar = new JToolBar()
            toolbar.orientation = JToolBar.HORIZONTAL
            toolbar.floatable = false

            // Create a button group to hold tools
            ButtonGroup buttonGroup = new ButtonGroup()

            //  Create a zoom in button
            JButton zoomInBtn = new JButton(new ZoomInAction(mapPane))
            toolbar.add(zoomInBtn)
            buttonGroup.add(zoomInBtn)

            // Create a zoom out button
            JButton zoomOutBtn = new JButton(new ZoomOutAction(mapPane))
            toolbar.add(zoomOutBtn)
            buttonGroup.add(zoomOutBtn)

            // Add separator
            toolbar.addSeparator()

            // Create a pan button
            JButton panBtn = new JButton(new PanAction(mapPane))
            toolbar.add(panBtn)
            buttonGroup.add(panBtn)

            // Add a separator
            toolbar.addSeparator()

            // Create a reset button
            JButton resetBtn = new JButton(new ResetAction(mapPane))
            toolbar.add(resetBtn)

            // Add the toolbar to the north
            add(toolbar, BorderLayout.NORTH)
        }
    }
}