package geoscript.style

import org.junit.Test
import static org.junit.Assert.*


/**
 *
 * @author Jared Erickson
 */
class StyleTestCase {

    @Test void constructors() {

        File sldFile = new File(getClass().getClassLoader().getResource("states.sld").toURI())
        Style style = new Style(sldFile)
        style.toSLD()

        println(style)
        List subStyles = style.subStyles
        subStyles.each{subStyle -> 
            println(subStyle)
            subStyle.rules.each{rule ->
                println(rule)
            }
        }
    }

}

