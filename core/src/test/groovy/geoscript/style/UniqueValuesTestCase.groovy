package geoscript.style

import geoscript.layer.Layer
import geoscript.workspace.Workspace
import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO

import static org.junit.Assert.*
import geoscript.filter.Color

import geoscript.render.Map

/**
 * The UniqueValues UnitTest
 * @author Jared Erickson
 */
class UniqueValuesTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void create() {

        // Get states shapefile
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")

        // Create a Map
        Map map = new Map()
        map.addLayer(layer)

        // Default is random colors
        UniqueValues sym1 = new UniqueValues(layer, "STATE_ABBR")
        assertNotNull(sym1)
        assertEquals(49, sym1.parts.size())

        layer.style = sym1
        File imgFile = folder.newFile("uniquevalues_states1.png")
        map.render(imgFile)
        assertTrue imgFile.length() > 0

        // Color palette
        UniqueValues sym2 = new UniqueValues(layer, "STATE_ABBR", "Greens")
        assertNotNull(sym2)
        assertEquals(49, sym2.parts.size())

        layer.style = sym2
        imgFile = folder.newFile("uniquevalues_states2.png")
        map.render(imgFile)
        ImageAssert.assertEquals(getFile("geoscript/style/uniquevalues_states2.png"), ImageIO.read(imgFile), 200)

        // Color list
        UniqueValues sym3 = new UniqueValues(layer, "STATE_ABBR", ["teal","slateblue","tan","wheat","salmon"])
        assertNotNull(sym3)
        assertEquals(49, sym3.parts.size())

        layer.style = sym3
        imgFile = folder.newFile("uniquevalues_states3.png")
        map.render(imgFile)
        ImageAssert.assertEquals(getFile("geoscript/style/uniquevalues_states3.png"), ImageIO.read(imgFile), 300)

        // Color Closure
        UniqueValues sym4 = new UniqueValues(layer, "STATE_ABBR", {i,v -> Color.getRandom()})
        assertNotNull(sym4)
        assertEquals(49, sym4.parts.size())

        layer.style = sym4
        imgFile = folder.newFile("uniquevalues_states4.png")
        map.render(imgFile)
        assertTrue imgFile.length() > 0
    }

    private File getFile(String resource) {
        new File(getClass().getClassLoader().getResource(resource).toURI())
    }
}
