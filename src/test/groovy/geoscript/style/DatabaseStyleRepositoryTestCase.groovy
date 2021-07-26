package geoscript.style

import geoscript.filter.Color
import geoscript.style.io.SLDWriter
import geoscript.workspace.H2
import groovy.sql.Sql
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class DatabaseStyleRepositoryTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void sqlite() {
        File databaseFile = folder.newFile("styles.db")
        Sql sql = Sql.newInstance("jdbc:sqlite:${databaseFile.absolutePath}", "org.sqlite.JDBC")
        runDatabaseTest(DatabaseStyleRepository.forSqlite(sql))
    }

    @Test
    void h2() {
        File databaseFile = folder.newFile("styles.db")
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

        String sld = styleRepository.getDefaultForLayer("states")
        assertNotNull(sld)
        assertTrue(sld.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        List<Map<String, String>> styles = styleRepository.getForLayer("states")
        assertEquals(3, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styles = styleRepository.getAll()
        assertEquals(5, styles.size())
        assertEquals("states", styles[0].layerName)
        assertEquals("states", styles[0].styleName)
        assertTrue(styles[0].style.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))

        styleRepository.delete("states", "states")
        assertEquals(4, styleRepository.getAll().size())

    }

}
