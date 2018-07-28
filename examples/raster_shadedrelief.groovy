import geoscript.layer.GeoTIFF
import geoscript.layer.Raster


File file = new File("raster.tif")
GeoTIFF geoTIFF = new GeoTIFF(file)
Raster raster = geoTIFF.read()

List<Double> degrees = [45.0,90.0,135.0,180.0]
double scale = 1.0

degrees.each { double altitude ->
    degrees.each { double azimuth ->
        Raster shadedReliefRaster = raster.createShadedRelief(scale, altitude, azimuth)
        File outFile = new File("shadedrelief_${altitude}_${azimuth}.tif")
        println "Writing ${outFile}..."
        new GeoTIFF(outFile).write(shadedReliefRaster)
    }
}
