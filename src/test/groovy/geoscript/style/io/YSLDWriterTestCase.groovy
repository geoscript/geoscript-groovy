package geoscript.style.io

import geoscript.AssertUtil
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.Symbolizer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The SLDWriter UnitTest
 * @author Jared Erickson
 */
class YSLDWriterTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()
    
    private String NEW_LINE = System.getProperty("line.separator")

    private String expectedYaml = """name: Default Styler
feature-styles:
- name: name
  rules:
  - scale: [min, max]
    symbolizers:
    - polygon:
        fill-color: '#F5DEB3'
    - line:
        stroke-color: '#A52A2A'
        stroke-width: 1
"""

    @Test void writeToOutputStream() {
        Symbolizer sym = new Fill("wheat") + new Stroke("brown")
        YSLDWriter writer = new YSLDWriter()
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(sym, out)
        String yaml = out.toString().trim()
        assertNotNull yaml
        assertTrue yaml.length() > 0
        AssertUtil.assertStringsEqual expectedYaml, yaml, trim: true
    }

    @Test void writeToFile() {
        Symbolizer sym = new Fill("wheat") + new Stroke("brown")
        YSLDWriter writer = new YSLDWriter()
        File file = folder.newFile("simple.yaml")
        writer.write(sym, file)
        String yaml = file.text.trim()
        assertNotNull yaml
        assertTrue yaml.length() > 0
        AssertUtil.assertStringsEqual expectedYaml, yaml, trim: true
    }

    @Test void writeToString() {
        Symbolizer sym = new Fill("wheat") + new Stroke("brown")
        YSLDWriter writer = new YSLDWriter()
        String yaml = writer.write(sym).trim()
        assertNotNull yaml
        assertTrue yaml.length() > 0
        AssertUtil.assertStringsEqual expectedYaml, yaml, trim: true
    }
}
