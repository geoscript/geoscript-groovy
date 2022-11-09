import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.render.Map as GSMap
import geoscript.style.io.SimpleStyleReader
import geoscript.workspace.Sqlite

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Download data from natural earth
File dir = new File("naturalearth")
[
        [name: "countries",  url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip"],
        [name: "ocean",      url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_ocean.zip"]
].each { Map item ->
    unzip(download(new URL(item.url), new File(dir, "${item.name}.zip"), overwrite: false))
}

// Copy shapefiles to a Sqlite database
Sqlite sqlite = new Sqlite(new File("naturalearth.sqlite"))
sqlite.add(new Shapefile("naturalearth/ne_110m_admin_0_countries.shp"))
sqlite.add(new Shapefile("naturalearth/ne_110m_ocean.shp"))

// Use simple style reader to create styles
SimpleStyleReader styleReader = new SimpleStyleReader()

// Get Layers and their styles
Layer ocean = sqlite.get("ne_110m_ocean")
ocean.style = styleReader.read("fill=#88caf8")

Layer countries = sqlite.get("ne_110m_admin_0_countries")
countries.style = styleReader.read("stroke=black stroke-width=0.5 fill=white")

// Render a Map
GSMap map = new GSMap(layers: [ocean, countries], width: 700, height: 400)
map.render(new File("sqlite_naturalearth.png"))
