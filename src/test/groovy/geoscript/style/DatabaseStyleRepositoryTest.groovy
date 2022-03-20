package geoscript.style

import geoscript.filter.Color
import geoscript.style.io.SLDWriter
import geoscript.workspace.H2
import groovy.sql.Sql
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

class DatabaseStyleRepositoryTest {

    @TempDir
    File folder

    @Test
    void sqlite() {
        File databaseFile = new File(folder,"styles.db")
        Sql sql = Sql.newInstance("jdbc:sqlite:${databaseFile.absolutePath}", "org.sqlite.JDBC")
        runDatabaseTest(DatabaseStyleRepository.forSqlite(sql))
    }

    @Test
    void h2() {
        File databaseFile = new File(folder,"styles.db")
        H2 h2 = new H2(databaseFile)
        Sql sql = h2.sql
        runDatabaseTest(DatabaseStyleRepository.forH2(sql))
    }

    private String createStyle(Color color) {
        new SLDWriter().write(new Fill(color))
    }

    private void runDatabaseTest(DatabaseStyleRepository styleRepository) {

        File file = new File(getClass().getClassLoader().getResource("states.sld").toURI())
        assertNotNull(file)

        // States
        styleRepository.save("states", "states", file.text)
        styleRepository.save("states", "states_blue", createStyle(new Color("blue")))
        styleRepository.save("states", "states_red", createStyle(new Color("red")))
        // Cities
        styleRepository.save("cities", "cities", createStyle(new Color("gray")))
        styleRepository.save("cities", "cities_black", createStyle(new Color("black")))

        Style style = styleRepository.getDefaultStyleForLayer("states")
        assertNotNull(style)
        assertTrue(style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        String styleStr = styleRepository.getDefaultForLayer("states")
        assertNotNull(styleStr)
        assertTrue(styleStr.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        List<Map<String, Object>> styles = styleRepository.getForLayer("states")
        assertEquals(3, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styles = styleRepository.getAll()
        assertEquals(5, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styleRepository.delete("states", "states")
        assertEquals(4, styleRepository.getAll().size())

    }

}
