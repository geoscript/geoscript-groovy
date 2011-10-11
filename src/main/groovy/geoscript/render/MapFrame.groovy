package geoscript.render

import java.awt.BorderLayout
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JToolBar
import org.geotools.swing.JMapPane
import org.geotools.swing.control.JMapStatusBar
import org.geotools.swing.action.*

/**
 * The MapFrame is a custom JFrame that creates the interactive
 * user interface.
 * @author Jared Erickson
 */
class MapFrame extends JFrame {

    /**
     * Create a new MapFrame
     * @param mapPane The JMapPane
     */
    MapFrame(Map map) {
        super()

        // Prepare the Map for rendering
        map.setUpRendering()

        // Create a new JMapPane
        JMapPane mapPane = new JMapPane(map.context)
        mapPane.setSize(map.width, map.height)
        mapPane.visible = true

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

        // Add a separator
        toolbar.addSeparator()

        // Create an info button
        JButton infoBtn = new JButton(new InfoAction(mapPane))
        toolbar.add(infoBtn)

        // Add the toolbar to the north
        add(toolbar, BorderLayout.NORTH)

        // Set the JFrame size and visibility
        setSize(map.width, map.height)
    }
}
