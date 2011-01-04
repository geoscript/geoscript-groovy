package geoscript.print

import java.awt.Graphics
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Dimension

/**
 * The Print Template can display a Map with cartographic elements such as
 * neatlines, scale, and north arrow.
 * @author Jared Erickson
 */
class Template {

    /**
     * The page width
     */
    int width
    
    /**
     * The page height
     */
    int height

    /**
     * The List of Items
     */
    List<Item> items;

    /**
     * The background Color
     */
    Color backgroundColor = Color.WHITE

    /**
     * The List of Producers (image, pdf, and svg)
     */
    private List producers = [
        new ImageProducer(),
        new PdfProducer(),
        new SvgProducer()
    ]

    /**
     * Create a new Template with the given width and height and a List of Items.
     * @param width The page width
     * @param height The page height
     * @param items The List of Items
     */
    Template(int width, int height, List items) {
        this.width = width
        this.height = height
        this.items = items
    }

    /**
     * Create a new Template with the given page size and a List of Items
     * @param size The page size
     * @param items The List of Items
     */
    Template(Dimension size, List items) {
        this.width = size.width
        this.height = size.height
        this.items = items
    }

    /**
     * Render the print Template to BufferedImage.
     * @param mimeType The type of image (defaults to image/png)
     */
    BufferedImage render(String mimeType = "image/png") {
        new ImageProducer().produce(this, mimeType)
    }

    /**
     * Render the print Template using the given mime type.
     * @param out A file name, a File, or an OutputStream
     * @param mimeType The mime type
     */
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

    /**
     * Draw all of the Items to the Graphics context in order.
     * @param g The Graphics
     */
    protected void draw(Graphics g) {
        if (backgroundColor) {
            g.color = backgroundColor
            g.fillRect(0,0,width,height)
        }
        items.each{item ->
            item.draw(g)
        }
    }

    /**
     * The letter landscape 8.5 x 11 page size
     */
    public static final Dimension MAPSIZE_LETTER_LANDSCAPE = new Dimension(792, 612)

    /**
     * The tabloid landscape 11 x 17 page size
     */
    public static final Dimension MAPSIZE_TABLOID_LANDSCAPE = new Dimension(1224, 792)

    /**
     * The C sized landscape page size
     */
    public static final Dimension MAPSIZE_C_LANDSCAPE = new Dimension(1584, 1224)

    /**
     * The D sized landscape page size
     */
    public static final Dimension MAPSIZE_D_LANDSCAPE = new Dimension(2448, 1584)

    /**
     * The E sized landscape page size
     */
    public static final Dimension MAPSIZE_E_LANDSCAPE = new Dimension(3168, 2448)

    /**
     * The letter portrait 11 x 8.5 page size
     */
    public static final Dimension MAPSIZE_LETTER_PORTRAIT = new Dimension(612, 792)

    /**
     * The tabloid portraait 17 x 11 page size
     */
    public static final Dimension MAPSIZE_TABLOID_PORTRAIT = new Dimension(792, 1224)

    /**
     * The C sized portrait page size
     */
    public static final Dimension MAPSIZE_C_PORTRAIT = new Dimension(1224, 1584)

    /**
     * The D sized portrait page size
     */
    public static final Dimension MAPSIZE_D_PORTRAIT = new Dimension(1584, 2448)

    /**
     * The E sized portrait page size
     */
    public static final Dimension MAPSIZE_E_PORTRAIT = new Dimension(2448, 3168)

}


