package geoscript.render

import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JToolBar
import org.geotools.swing.JMapPane
import org.geotools.swing.control.JMapStatusBar
import org.geotools.swing.action.*
import geoscript.layer.Shapefile

/**
 * A complex GUI for viewing a Map.
 * @author Jared Erickson
 */
class MapWindow {

    /**
     * Open a complex GUI for viewing a Map
     * @param map The Map
     */
    MapWindow(Map map) {

        // Prepare the Map for rendering
        map.setUpRendering()

        // Create the map
        JMapPane mapPane = new JMapPane(map.context)
        mapPane.setSize(map.width, map.height)
        mapPane.visible = true

        // Create the status bar
        JMapStatusBar statusBar = JMapStatusBar.createDefaultStatusBar(mapPane)

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

        // Use SwingBuilder to build the GUI
        def swing = new SwingBuilder()
        swing.edt {
            frame(title:'Window', size:[map.width, map.height], defaultCloseOperation:JFrame.EXIT_ON_CLOSE, show:true) {
                borderLayout()
                container(toolbar, constraints: BorderLayout.NORTH)
                container(mapPane, constraints:BorderLayout.CENTER)
                container(statusBar, constraints: BorderLayout.SOUTH)
            }
        }
    }
}
