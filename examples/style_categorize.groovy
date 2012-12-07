import geoscript.layer.Shapefile
import geoscript.filter.Function
import geoscript.render.Draw
import geoscript.style.*

shp = new Shapefile("states.shp")
shp.style = new Fill(new Function("Categorize(PERSONS / LAND_KM, '#87CEEB', 20, '#FFFACD', 100, '#F08080')")) + new Stroke("#999999",0.1) + new Label("STATE_ABBR").point([0.5,0.5])
shp.style.asSLD()

Draw.drawToImage(shp)
