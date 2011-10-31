package geoscript.render

import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.JFrame

/**
 * A simple GUI for viewing a Map.
 * @author Jared Erickson
 */
class Window {

    /**
     * Open a simple GUI for viewing a Map
     * @param map The Map
     */
    Window(Map map) {
        def swing = new SwingBuilder()
        swing.edt {
            frame(title:'Window', size:[map.width, map.height], defaultCloseOperation:JFrame.EXIT_ON_CLOSE, show:true) {
                borderLayout()
                label(icon:imageIcon(map.renderToImage()), size:[map.width, map.height],constraints: BorderLayout.CENTER)
            }
        }
    }
}

