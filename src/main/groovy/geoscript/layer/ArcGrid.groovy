package geoscript.layer

import geoscript.proj.Projection
import org.apache.commons.io.input.ReaderInputStream
import org.geotools.factory.GeoTools
import org.geotools.factory.Hints

import java.nio.charset.Charset

/**
 * A Format that can read and write ArcGrids
 * @author Jared Erickson
 */
class ArcGrid extends Format {

    /**
     * Create a new ArcGrid
     */
    ArcGrid() {
        super(new org.geotools.gce.arcgrid.ArcGridFormat())
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @return A Raster
     */
    Raster read(Map options = [:], def source) {
        if (source instanceof String) {
            String charset = "UTF-8"
            if (options.containsKey("charset")) {
                charset = options.get("charset")
                options.remove(charset)
            }
            source = new ReaderInputStream(new StringReader(source), Charset.forName(charset))
        }
        super.read(options, source, GeoTools.getDefaultHints())
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @param proj The Projection
     * @return A Raster
     */
    Raster read(Map options = [:], def source, Projection proj) {
        if (source instanceof String) {
            String charset = "UTF-8"
            if (options.containsKey("charset")) {
                charset = options.get("charset")
                options.remove(charset)
            }
            source = new ReaderInputStream(new StringReader(source), Charset.forName(charset))
        }
        super.read(options, source, proj)
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @param hints GeoTools Hints
     * @return A Raster
     */
    Raster read(Map options = [:], def source, Hints hints) {
        if (source instanceof String) {
            String charset = "UTF-8"
            if (options.containsKey("charset")) {
                charset = options.get("charset")
                options.remove(charset)
            }
            source = new ReaderInputStream(new StringReader(source), Charset.forName(charset))
        }
        super.read(options, source, hints)
    }

    /**
     * Write an ArcGrid Raster to a String
     * @param raster The Raster
     * @param format The string format can either be "arc" which is the default, or grass
     * @return A String
     */
    String writeToString(Raster raster, String format = "arc") {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(raster, out, GRASS: format.equalsIgnoreCase("grass") ? true : false)
        out.close()
        out.toString()
    }
}
