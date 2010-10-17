package geoscript.print

import java.awt.Graphics
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Dimension

/**
 * A Print Template
 * @author Jared Erickson
 */
class Template {

    int width
    int height
    List<Item> items;
    Color backgroundColor = Color.WHITE
    List producers = [
        new ImageProducer(),
        new PdfProducer(),
        new SvgProducer()
    ]

    Template(int width, int height, List items) {
        this.width = width
        this.height = height
        this.items = items
    }

    Template(Dimension size, List items) {
        this.width = size.width
        this.height = size.height
        this.items = items
    }

    BufferedImage render(String mimeType = "image/png") {
        new ImageProducer().produce(this, mimeType)
    }

    void render(def out, String mimeType = "image/png") {
        Producer producer = producers.find{p->p.handlesMimeType(mimeType)}
        if(!producer) throw new IllegalArgumentException("Unknown mime type!")
        if (out instanceof String) {
            out = new File(out)
        }
        if (out instanceof File) {
            out = new FileOutputStream(out)
        }
        producer.produce(this, mimeType, out)
    }

    protected void draw(Graphics g) {
        if (backgroundColor) {
            g.color = backgroundColor
            g.fillRect(0,0,width,height)
        }
        items.each{item ->
            item.draw(g)
        }
    }

    public static final Dimension MAPSIZE_LETTER_LANDSCAPE = new Dimension(792, 612)

    public static final Dimension MAPSIZE_TABLOID_LANDSCAPE = new Dimension(1224, 792)

    public static final Dimension MAPSIZE_C_LANDSCAPE = new Dimension(1584, 1224)

    public static final Dimension MAPSIZE_D_LANDSCAPE = new Dimension(2448, 1584)

    public static final Dimension MAPSIZE_E_LANDSCAPE = new Dimension(3168, 2448)

    public static final Dimension MAPSIZE_LETTER_PORTRAIT = new Dimension(612, 792)

    public static final Dimension MAPSIZE_TABLOID_PORTRAIT = new Dimension(792, 1224)

    public static final Dimension MAPSIZE_C_PORTRAIT = new Dimension(1224, 1584)

    public static final Dimension MAPSIZE_D_PORTRAIT = new Dimension(1584, 2448)

    public static final Dimension MAPSIZE_E_PORTRAIT = new Dimension(2448, 3168)

}


