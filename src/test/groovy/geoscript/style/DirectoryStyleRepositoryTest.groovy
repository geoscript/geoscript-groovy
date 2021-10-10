package geoscript.style

import geoscript.FileUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

class DirectoryStyleRepositoryTest {

    @TempDir
    File folder

    @Test
    void sld() {
        File directory = FileUtil.createDir(folder,"sld")

        File file = new File(getClass().getClassLoader().getResource("states.sld").toURI())
        assertNotNull(file)

        StyleRepository styleRepository = new DirectoryStyleRepository(directory)
        styleRepository.save("states", "states", file.text)
        assertTrue(new File(directory, "states.sld").exists())

        String sld = styleRepository.getDefaultForLayer("states")
        assertNotNull(sld)
        assertTrue(sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        List<Map<String, String>> styles = styleRepository.getForLayer("states")
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styles = styleRepository.getAll()
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styleRepository.delete("states","states")
        assertTrue(styleRepository.getAll().isEmpty())
    }

    @Test
    void css() {
        File directory = FileUtil.createDir(folder,"css")

        File file = new File(getClass().getClassLoader().getResource("states.css").toURI())
        assertNotNull(file)

        StyleRepository styleRepository = new DirectoryStyleRepository(directory)
        styleRepository.save("states", "states", file.text, [type: 'css'])
        assertTrue(new File(directory, "states.css").exists())

        String sld = styleRepository.getDefaultForLayer("states")
        assertNotNull(sld)
        assertTrue(sld.startsWith("states {"))

        List<Map<String, String>> styles = styleRepository.getForLayer("states")
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.startsWith("states {"))

        styles = styleRepository.getAll()
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.startsWith("states {"))

        styleRepository.delete("states","states")
        assertTrue(styleRepository.getAll().isEmpty())
    }

}
