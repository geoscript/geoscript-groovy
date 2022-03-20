package geoscript.style

import geoscript.FileUtil
import geoscript.style.io.SLDWriter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

class NestedDirectoryStyleRepositoryTest {

    @TempDir
    File folder

    @Test
    void sld() {
        File baseDirectory = FileUtil.createDir(folder,"css")
        File directory = new File(baseDirectory, "states")

        File file = new File(getClass().getClassLoader().getResource("states.sld").toURI())
        assertNotNull(file)

        StyleRepository styleRepository = new NestedDirectoryStyleRepository(baseDirectory)
        styleRepository.save("states", "states", file.text)
        assertTrue(new File(directory, "states.sld").exists())

        Style style = styleRepository.getDefaultStyleForLayer("states")
        assertNotNull(style)
        assertTrue(style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        String styleStr = styleRepository.getDefaultForLayer("states")
        assertNotNull(styleStr)
        assertTrue(styleStr.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        List<Map<String, Object>> styles = styleRepository.getForLayer("states")
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styles = styleRepository.getAll()
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styleRepository.delete("states","states")
        assertTrue(styleRepository.getAll().isEmpty())
    }

    @Test
    void css() {
        File baseDirectory = FileUtil.createDir(folder,"css")
        File directory = new File(baseDirectory, "states")

        File file = new File(getClass().getClassLoader().getResource("states.css").toURI())
        assertNotNull(file)

        StyleRepository styleRepository = new NestedDirectoryStyleRepository(baseDirectory)
        styleRepository.save("states", "states", file.text, [type: 'css'])
        assertTrue(new File(directory, "states.css").exists())

        Style style = styleRepository.getDefaultStyleForLayer("states")
        assertNotNull(style)
        assertTrue(style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        String styleStr = styleRepository.getDefaultForLayer("states")
        assertNotNull(styleStr)
        assertTrue(styleStr.startsWith("states {"))

        List<Map<String, Object>> styles = styleRepository.getForLayer("states")
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styles = styleRepository.getAll()
        assertEquals(1, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styleRepository.delete("states", "states")
        assertTrue(styleRepository.getAll().isEmpty())
    }

}
