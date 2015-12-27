package geoscript.render

import geoscript.layer.Layer
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.Symbolizer
import geoscript.workspace.Workspace
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static geoscript.render.Draw.draw
import static org.junit.Assert.assertTrue

class DrawTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void drawLayerToPdf() {
        Symbolizer sym = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = sym
        File file = folder.newFile("draw_layer.pdf")
        draw(layer, bounds: layer.bounds.scale(1.1), size: [250, 250], out: file, format: "pdf")
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

}
