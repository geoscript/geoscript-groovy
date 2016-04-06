package geoscript.render

import geoscript.render.Map as GeoScriptMap
import geoscript.layer.Format
import geoscript.layer.Raster

import org.geotools.gce.geotiff.GeoTiffFormat

class GeoTIFF extends Image
{
    GeoTIFF() { super("geotiff") }

    @Override
    public void render(GeoScriptMap map, OutputStream out) {
        def image = render(map)
        def raster = new Raster(image, map.bounds)
        def format = new Format(new GeoTiffFormat(), out)

        format.write(raster)
    }
}
