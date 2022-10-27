package geoscript.render

import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.layer.Raster
import geoscript.layer.Renderable
import geoscript.proj.Projection
import org.geotools.referencing.operation.projection.MapProjection

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * Create a map cube using the gnonomic cubed projection.
 * <p><blockquote><pre>
 * import static geoscript.render.MapCube
 * MapCube mapCube = new MapCube(title: 'Earth Map Cube', source: 'Natural Earth')
 * mapCube.render([countries], new File('mapcube.png'))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class MapCube {

    boolean drawOutline = false

    boolean drawTabs = true

    int tabSize = 30

    String title = ""

    String source = ""

    String imageType = "png"

    void render(List<Renderable> layers, File file) {
        file.withOutputStream { OutputStream out ->
            render(layers, out)
        }
    }

    byte[] render(List<Renderable> layers) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        try {
            render(layers, out)
        } finally {
            out.close()
        }
        out.toByteArray()
    }

    void render(List<Renderable> layers, OutputStream out) {

        MapProjection.SKIP_SANITY_CHECKS = true

        BufferedImage image = new BufferedImage(1800, 1500, BufferedImage.TYPE_INT_ARGB)
        Graphics2D g2d = image.createGraphics()
        g2d.paint = java.awt.Color.WHITE
        g2d.fillRect(0, 0, 1800, 1500)

        Raster preRaster = null
        if (layers.size() > 0) {
            File preFile = File.createTempFile("map", ".${imageType}")
            Map preMap = new Map(
                    width: 1600,
                    height: 800,
                    proj: new Projection("EPSG:4326"),
                    fixAspectRatio: false,
                    bounds: new Bounds(-180, -89.9, 180, 89.9, "EPSG:4326"),
                    layers: layers
            )
            preMap.render(preFile)

            preRaster = new Raster(ImageIO.read(preFile), new Bounds(-180, -90, 180, 90, "EPSG:4326"))
        } else {
            drawOutline = true
        }

        List cubes = [
                // north
                [id: "0", image: [500, 140], center: new Point(-45, 90 - 0.1), bounds: new Bounds(-180.0, 45.0, 180.0, 90.0, "EPSG:4326")],
                // equators
                [id: "1", image: [100, 540], center: new Point(-135, 0), bounds: new Bounds(-180 + 0.1, -35, -90 + 0.1, 35, "EPSG:4326")],
                [id: "2", image: [500, 540], center: new Point(-45, 0), bounds: new Bounds(-90 + 0.1, -35, 0, 35, "EPSG:4326")],
                [id: "3", image: [900, 540], center: new Point(45, 0), bounds: new Bounds(0, -35, 90 - 0.1, 35, "EPSG:4326")],
                [id: "4", image: [1300, 540], center: new Point(135, 0), bounds: new Bounds(90 - 0.1, -35, 180 - 0.1, 35, "EPSG:4326")],
                // south
                [id: "5", image: [500, 940], center: new Point(-45, -90 + 0.1), bounds: new Bounds(-180.0, -45.0, 180.0, -90.0, "EPSG:4326")]
        ]

        if (preRaster) {
            cubes.each { java.util.Map cube ->
                Point center = cube.center
                Projection p = new Projection("AUTO:97001,9001,${center.x},${center.y}")
                Bounds b = cube.bounds.reproject(p)
                Map map = new Map(
                        scaleComputation: "ogc",
                        layers: [preRaster],
                        width: 400,
                        height: 400,
                        fixAspectRatio: false,
                        backgroundColor: "white",
                        proj: p,
                        bounds: b
                )
                BufferedImage img = map.renderToImage()
                g2d.drawImage(img, cube.image[0], cube.image[1], null)
            }
        }

        if (drawOutline) {
            cubes.each { java.util.Map cube ->
                g2d.paint = new Color(0, 0, 0)
                g2d.drawRect(cube.image[0], cube.image[1], 400, 400)
            }
        }

        if (drawTabs) {
            g2d.paint = new Color(0, 0, 0)
            // top
            drawTab(g2d, 100, 540, "top", tabSize)
            drawTab(g2d, 100 + 400, 540 - 400, "top", tabSize)
            drawTab(g2d, 100 + (400 * 2), 540, "top", tabSize)
            // bottom
            drawTab(g2d, 100, 940, "bottom", tabSize)
            drawTab(g2d, 100 + (400 * 2), 940, "bottom", tabSize)
            drawTab(g2d, 100 + (400 * 3), 940, "bottom", tabSize)
            // east
            drawTab(g2d, 100 + (400 * 4), 540, "east", tabSize)
        }

        int x = 20
        int y = 50
        if (title) {
            g2d.setFont(new Font("Verdana", Font.BOLD, 42))
            g2d.drawString(title, x, y)
            y += 40
        }
        if (source) {
            g2d.setFont(new Font("Verdana", Font.ITALIC, 24))
            g2d.drawString(source, x, y)
        }

        g2d.dispose()
        ImageIO.write(image, imageType, out)
        out.close()

        MapProjection.SKIP_SANITY_CHECKS = false
    }

    protected void drawTab(Graphics2D g, int x, int y, String side, int tab) {
        int size = 400
        // top
        if (side.equalsIgnoreCase("top")) {
            g.drawLine(x, y, x + tab, y - tab)
            g.drawLine(x + tab, y - tab, x + size - tab, y - tab)
            g.drawLine(x + size, y, x + size - tab, y - tab)
        }
        // bottom
        else if (side.equalsIgnoreCase("bottom")) {
            g.drawLine(x, y, x + tab, y + tab)
            g.drawLine(x + tab, y + tab, x + size - tab, y + tab)
            g.drawLine(x + size, y, x + size - tab, y + tab)
        }
        // east
        else if (side.equalsIgnoreCase("east")) {
            g.drawLine(x, y, x + tab, y + tab)
            g.drawLine(x + tab, y + tab, x + tab, y + size - tab)
            g.drawLine(x, y + size, x + tab, y + size - tab)
        }
    }
}