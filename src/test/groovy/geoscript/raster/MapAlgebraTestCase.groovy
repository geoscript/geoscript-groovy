package geoscript.raster

import org.junit.Test
import static org.junit.Assert.*

/**
 * The RasterAlgebra UnitTest
 * @author Jared Erickson
 */
class MapAlgebraTestCase {

    @Test void greatThan() {
        File rasterFile = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        Raster raster = new GeoTIFF(rasterFile)
        MapAlgebra algebra = new MapAlgebra()
        Raster output = algebra.calculate("dest = src > 200;", [src: raster], "dest", [600, 400])
        assertNotNull output
        File file = File.createTempFile("greaterthan_",".tif")
        println file
        output.write(file)
        assertTrue file.size() > 0
    }

    @Test void waves() {
        MapAlgebra algebra = new MapAlgebra()
        Raster output = algebra.calculate("""init {
              // image centre coordinates
              xc = width() / 2;
              yc = height() / 2;

              // constant term
              C = M_PI * 8;
            }

            dx = (x() - xc) / xc;
            dy = (y() - yc) / yc;
            d = sqrt(dx*dx + dy*dy);

            destImg = sin(C * d);""", null, "destImg")
        assertNotNull output
        File file = File.createTempFile("waves_",".tif")
        println file
        output.write(file)
        assertTrue file.size() > 0
    }
}