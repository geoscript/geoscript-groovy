package geoscript.carto

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals


class PageSizeTest {

    @Test
    void create() {
        PageSize pageSize = new PageSize(400,500)
        assertEquals(400, pageSize.width)
        assertEquals(500, pageSize.height)
        assertEquals("PageSize(400, 500)", pageSize.toString())
    }

    @Test
    void createStandard() {
        PageSize pageSize = PageSize.LETTER_LANDSCAPE
        assertEquals(792, pageSize.width)
        assertEquals(612, pageSize.height)
        assertEquals("PageSize(792, 612)", pageSize.toString())
    }

}
