package geoscript.style.io

import geoscript.AssertUtil
import geoscript.style.ColorMap
import geoscript.style.Symbolizer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The ColorTableWriter Unit Test
 * @author Jared Erickson
 */
class ColorTableWriterTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()
    
    @Test void writeToString() {
        ColorTableWriter writer = new ColorTableWriter()
        ColorMap colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        String actual = writer.write(colorMap)
        String expected = """70 0 128 0
256 102 51 51"""
        AssertUtil.assertStringsEqual(expected, actual)
    }

    @Test void writeToFile() {
        ColorTableWriter writer = new ColorTableWriter()
        ColorMap colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        File file = folder.newFile("colortable.txt")
        writer.write(colorMap, file)
        String actual = file.text
        String expected = """70 0 128 0
256 102 51 51"""
        AssertUtil.assertStringsEqual(expected, actual)
    }

    @Test void writeToOutputStream() {
        ColorTableWriter writer = new ColorTableWriter()
        ColorMap colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        File file = folder.newFile("colortable.txt")
        writer.write(colorMap, new FileOutputStream(file))
        String actual = file.text
        String expected = """70 0 128 0
256 102 51 51"""
        AssertUtil.assertStringsEqual(expected, actual)
    }

}
