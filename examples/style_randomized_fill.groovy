import geoscript.layer.Shapefile
import geoscript.render.Draw
import geoscript.style.*

shp = new Shapefile("states.shp")
shp.style = (new Fill(null).hatch("circle", new Fill("#aaaaaa"), 1).random([random: true, symbolCount: "50", tileSize: "100"]).where("PERSONS < 2000000")) +
        (new Fill(null).hatch("circle", new Fill("#aaaaaa"), 2).random([random: true, symbolCount: "200", tileSize: "100"]).where("PERSONS BETWEEN 2000000 AND 4000000")) +
        (new Fill(null).hatch("circle", new Fill("#aaaaaa"), 2).random([random: true, symbolCount: "700", tileSize: "100"]).where("PERSONS > 4000000")) +
        (new Stroke("black", 0.1) + new Label(property: "STATE_ABBR", font: new Font(family: "Times New Roman", style: "normal", size: 14)).point([0.5, 0.5]).halo(new Fill("#FFFFFF"), 2))

println shp.style.sld
Draw.draw(shp)