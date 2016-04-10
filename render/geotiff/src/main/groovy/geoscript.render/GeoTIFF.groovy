package geoscript.render

import geoscript.layer.Format
import geoscript.layer.Raster
import org.geotools.gce.geotiff.GeoTiffFormat

class GeoTIFF extends Image
{
    GeoTIFF() { super("geotiff") }

    @Override
    public void render(Map map, OutputStream out) {
        def image = render(map)
        def raster = new Raster(image, map.bounds)
        def format = new Format(new GeoTiffFormat(), out)

        format.write(raster)
    }
}
