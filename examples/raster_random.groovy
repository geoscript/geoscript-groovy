import geoscript.filter.Color
import geoscript.geom.Bounds
import geoscript.layer.Band
import geoscript.layer.GeoTIFF
import geoscript.layer.Raster

// Create a new Raster
Raster raster = new Raster(
        new Bounds(-180,-90,180,90,"EPSG:4326"),
        400,300,
        [
                new Band("red", 0, 255, 256),
                new Band("green", 0, 255, 256),
                new Band("blue", 0, 255, 256)
        ]
)
println "Bounds: ${raster.bounds}"
println "Size: ${raster.size[0]} x ${raster.size[1]}"

// Set values of each pixel
raster.eachCell { double value, double x, double y ->
    Color color = Color.randomPastel
    raster.setValue([x,y], color.rgb[0], 0)
    raster.setValue([x,y], color.rgb[1], 1)
    raster.setValue([x,y], color.rgb[2], 2)
}

// Write the Raster to disk
File file = new File("random.tif")
GeoTIFF geotiff = new GeoTIFF(file)
geotiff.write(raster)