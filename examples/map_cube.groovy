import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.render.MapCube
import geoscript.style.io.SimpleStyleReader

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Download data from natural earth
File dir = new File("naturalearth")
[
        [name: "countries", url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip"],
        [name: "ocean",     url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_ocean.zip"]
].each { Map item ->
    unzip(download(new URL(item.url), new File(dir, "${item.name}.zip"), overwrite: false))
}

// Use simple style reader to create styles
SimpleStyleReader styleReader = new SimpleStyleReader()

// Get Layers and their styles
Layer ocean = new Shapefile("naturalearth/ne_110m_ocean.shp")
ocean.style = styleReader.read("fill=#88caf8")

Layer countries = new Shapefile("naturalearth/ne_110m_admin_0_countries.shp")
countries.style = styleReader.read("stroke=black stroke-width=0.5 fill=white")

// Create a map cube
MapCube mapCube = new MapCube(title: "The Earth Map Cube", source: "Natural Earth", drawOutline: true)
mapCube.render([ocean, countries], new File("map_cube.png"))


