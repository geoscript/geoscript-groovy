import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.proj.Projection
import geoscript.render.Map
import geoscript.style.io.SimpleStyleReader

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Download data from natural earth
File dir = new File("naturalearth")
[
        [name: "countries",  url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip"],
        [name: "ocean",      url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_ocean.zip"],
        [name: "graticules", url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_graticules_20.zip"]
].each { java.util.Map item ->
    unzip(download(new URL(item.url), new File(dir, "${item.name}.zip"), overwrite: false))
}

// Use simple style reader to create styles
SimpleStyleReader styleReader = new SimpleStyleReader()

// Get Layers and their styles
Layer ocean = new Shapefile("naturalearth/ne_110m_ocean.shp")
ocean.style = styleReader.read("fill=#88caf8 stroke=black stroke-width=0.5")

Layer graticules = new Shapefile("naturalearth/ne_110m_graticules_20.shp")
graticules.style = styleReader.read("stroke=black stroke-width=0.5")

Layer countries = new Shapefile("naturalearth/ne_110m_admin_0_countries.shp")
countries.style = styleReader.read("stroke=black stroke-width=0.5 fill=white")

File imagesDir = new File("images")
imagesDir.mkdir()

// Create a map
[
    "Mercator",
    "WGS84",
    "EqualEarth",
    "Mollweide",
    "Aitoff",
    "EckertIV",
    "WagnerIV",
    "Robinson",
    "WinkelTripel",
    "Sinusoidal",
    "WorldVanderGrintenI"
].each { String projectionName ->
    Projection projection = new Projection(projectionName)
    Bounds bounds = projectionName.equalsIgnoreCase("Mercator")
            ? new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326")
            : new Bounds(-180,-90,180,90, "EPSG:4326")
    Map map = new Map(
            layers: [ocean, countries, graticules],
            proj: projection,
            bounds: bounds.reproject(projection),
            width: 800,
            height: 350
    )
    map.render(new File(imagesDir,"map_${projectionName.toLowerCase()}.png"))
}