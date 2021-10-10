package geoscript.style.io

import geoscript.AssertUtil
import geoscript.style.ColorMap
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * The ColorTableWriter Unit Test
 * @author Jared Erickson
 */
class ColorTableWriterTest {

    @TempDir
    File folder
    
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
        File file = new File(folder,"colortable.txt")
        writer.write(colorMap, file)
        String actual = file.text
        String expected = """70 0 128 0
256 102 51 51"""
        AssertUtil.assertStringsEqual(expected, actual)
    }

    @Test void writeToOutputStream() {
        ColorTableWriter writer = new ColorTableWriter()
        ColorMap colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        File file = new File(folder,"colortable.txt")
        writer.write(colorMap, new FileOutputStream(file))
        String actual = file.text
        String expected = """70 0 128 0
256 102 51 51"""
        AssertUtil.assertStringsEqual(expected, actual)
    }

}
