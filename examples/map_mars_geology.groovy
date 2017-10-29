import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.io.UniqueValuesReader

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

File dir = new File("mars")
dir.mkdir()

unzip(
    download(new URL("https://astropedia.astrogeology.usgs.gov/download/Mars/Geology/Mars15MGeologicGISRenovation.zip"),
             new File(dir, "mars.zip"), overwrite: false
    )
)

Layer layer = new Shapefile("mars/I1802ABC_Mars_global_geology/Shapefiles/I1802ABC_Mars2000_Sphere/geo_units_oc_dd.shp")

UniqueValuesReader styleReader = new UniqueValuesReader("UnitSymbol", "polygon")
layer.style = styleReader.read(new File("mars/I1802ABC_Mars_global_geology/I1802ABC_geo_units_RGBlut.txt"))

Map map = new Map(layers: [layer])
map.render(new File("mars_geology.png"))
