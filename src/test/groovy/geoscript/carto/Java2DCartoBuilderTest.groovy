package geoscript.carto

import org.junit.Rule;
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.awt.Font

import static org.junit.Assert.*

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage;

class Java2DCartoBuilderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    boolean showInTarget = false

    @Test
    void drawNorthArrow() {
        draw(new PageSize(80, 140), "northarrow.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0,0,pageSize.width, pageSize.height))
        })
    }

    @Test
    void drawNorthArrowWithText() {
        draw(new PageSize(80, 140), "northarrow_text.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0,0,pageSize.width, pageSize.height).drawText(true))
        })
    }

    @Test
    void drawNESWArrow() {
        draw(new PageSize(200, 200), "northarrow_nesw.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0, 0, pageSize.width, pageSize.height).style(NorthArrowStyle.NorthEastSouthWest))
        })
    }

    @Test
    void drawNESWArrowWithText() {
        draw(new PageSize(200, 200), "northarrow_nesw_text.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0, 0, pageSize.width, pageSize.height)
                .style(NorthArrowStyle.NorthEastSouthWest)
                .drawText(true)
                .font(new Font("Arial", Font.BOLD, 24))
            )
        })
    }

    private void draw(PageSize pageSize, String fileName, Closure closure) {
        BufferedImage image = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_ARGB)
        Graphics2D graphics = image.createGraphics()
        graphics.renderingHints = [
                (RenderingHints.KEY_ANTIALIASING)     : RenderingHints.VALUE_ANTIALIAS_ON,
                (RenderingHints.KEY_TEXT_ANTIALIASING): RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        ]
        Java2DCartoBuilder builder = new Java2DCartoBuilder(graphics, pageSize)
        closure.call(pageSize, builder)
        File file = getTempFile(fileName)
        ImageIO.write(image, "png", file)
        assertTrue(file.exists())
        assertTrue(file.length() > 1)
    }

    private File getTempFile(String fileName) {
        new File(getDirectory(), fileName)
    }

    private File getDirectory() {
        showInTarget ? new File("target") : temporaryFolder.newFolder("carto")
    }

}
