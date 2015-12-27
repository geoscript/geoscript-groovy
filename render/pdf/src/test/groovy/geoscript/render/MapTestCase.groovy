package geoscript.render

import geoscript.layer.Layer
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Workspace
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class MapTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void renderToPdf() {
        File f = folder.newFile("map.pdf")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"pdf", layers:[shp])
        map.addLayer(shp)
        map.render(f)
        assertTrue(f.exists())
        map.close()
    }

}
