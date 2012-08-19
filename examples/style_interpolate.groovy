import geoscript.layer.Shapefile
import geoscript.filter.Function
import geoscript.render.Draw
import geoscript.style.*

shp = new Shapefile("states.shp")
shp.style = new Fill(new Function("Interpolate(PERSONS, 0, '#fefeee', 9000000, '#00ff00', 23000000, '#ff0000', 'color')")) + new Stroke("#999999",0.1) + new Label("STATE_ABBR").point([0.5,0.5])
shp.style.asSLD()

Draw.drawToImage(shp)
