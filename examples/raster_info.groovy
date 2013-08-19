import geoscript.layer.*

// Read a GeoTIFF
def format = new GeoTIFF()
def raster = format.read(new File("raster.tif"))

// Print some information
println "Format = ${raster.format}"
println "Proj EPSG = ${raster.proj.id}"
println "Proj WKT = ${raster.proj.wkt}"
println "Bounds = ${raster.bounds.geometry.wkt}"
println "Size = ${raster.size}"
println "Block Size = ${raster.blockSize}"
println "Pixel Size = ${raster.pixelSize}"
println "Band:"
raster.bands.eachWithIndex{b,i ->
    println "   ${i}). ${b}"
}



