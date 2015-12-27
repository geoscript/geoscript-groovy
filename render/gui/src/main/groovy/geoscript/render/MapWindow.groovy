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

/**
 * A complex GUI for viewing a {@link geoscript.render.Map Map}.
 * <p><blockquote><pre>
 * import geoscript.render.*
 * import geoscript.layer.*
 * import geoscript.style.*
 *
 * Map map = new Map(layers:[new Shapefile("states.shp")])
 * new MapWindow(map)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class MapWindow implements Displayer {

    /**
     * Open a complex GUI for viewing a Map
     * @param map The Map
     */
    MapWindow(Map map) {

        // Prepare the Map for rendering
        map.setUpRendering()

        // Create the map
        JMapPane mapPane = new JMapPane()
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
        def frame = swing.frame(title:'Window', size:[map.width, map.height], show:false) {
            borderLayout()
            container(toolbar, constraints: BorderLayout.NORTH)
            container(mapPane, constraints:BorderLayout.CENTER)
            container(statusBar, constraints: BorderLayout.SOUTH)
        }
        // If we are opening Windows from the GroovyConsole, we can't use EXIT_ON_CLOSE because the GroovyConsole
        // itself will exit
        if (java.awt.Frame.frames.find{it.title.contains("GroovyConsole")}) {
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        } else {
            // The Groovy Shell has a special SecurityManager that doesn't allow EXIT_ON_CLOSE
            try { frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE } catch (SecurityException ex) {frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE}
        }
        swing.edt {
            frame.visible = true
            mapPane.mapContent = map.content
        }
    }

    /**
     * Creae a MapWindow
     */
    MapWindow() {
    }

    /**
     * Display the Map in a GUI
     * @param map The Map
     */
    @Override
    void display(Map map) {
        new MapWindow(map)
    }

}
