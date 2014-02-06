import geoscript.layer.*

// Read a GeoTIFF
def format = new GeoTIFF(new File("raster.tif"))
def raster = format.read()

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



