package geoscript.render

import geoscript.geom.Geometry
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.junit.Test
import static geoscript.render.Plot.plot
import static geoscript.render.Plot.plotToImage
import geoscript.feature.Schema
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.geom.LineString
import geoscript.layer.Shapefile
import geoscript.layer.Layer
import geoscript.workspace.Memory

/**
 * The Plot UnitTest
 * @author Jared Erickson
 */
class PlotTestCase {

    @Test void plotGeometryToFile() {
        Geometry geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        File file = File.createTempFile("plot",".png")
        println file
        plot(geom, size: [400,400], out: file)
    }

    @Test void plotGeometryToOutputStream() {
        Geometry geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        File file = File.createTempFile("plot",".png")
        println file
        OutputStream out = new FileOutputStream(file)
        plot(geom, size: [400,400], out: out, type: "png")
    }

    @Test void plotGeometryToImage() {
        Geometry geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        BufferedImage image = plotToImage(geom, size: [400,400])
        File file = File.createTempFile("plot",".png")
        println file
        ImageIO.write(image, "png", file)
    }

    @Test void plotFeatureToImage() {
        Schema schema  = new Schema("shapes",[new Field("geom","Polygon"), new Field("name", "String")])
        Feature feature = new Feature([new LineString([0,0],[1,1]).bounds.polygon, "square"], "0",  schema)
        BufferedImage image = plotToImage(feature, size: [400,400])
        File file = File.createTempFile("plot_feature",".png")
        println file
        ImageIO.write(image, "png", file)
    }

    @Test void plotLayerToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(shpFile)
        Memory mem = new Memory()
        Layer layer = mem.add(shp).filter("STATE_ABBR IN ('ND','SD','MT')","nd_sd_mt")
        BufferedImage image = plotToImage(layer, size: [400,400])
        File file = File.createTempFile("plot_layer",".png")
        println file
        ImageIO.write(image, "png", file)
    }
}
