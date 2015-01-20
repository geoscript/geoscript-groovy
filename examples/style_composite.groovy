import geoscript.filter.Function
import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.Fill
import geoscript.style.Label
import geoscript.style.Stroke

shp = new Shapefile("states.shp")
func = new Function("Recode(SUB_REGION,'N Eng','#6495ED','Mid Atl','#B0C4DE','S Atl','#00FFFF','E N Cen','#9ACD32','E S Cen','#00FA9A','W N Cen','#FFF8DC','W S Cen','#F5DEB3','Mtn','#F4A460','Pacific','#87CEEB')")
shp.style = (new Fill(func).composite("multiply", symbolizer: false, base: true)).zindex(1) +
        (new Stroke("black", 10).composite("destination-in", symbolizer: false)).zindex(2) +
        (new Stroke("#999999", 0.1) + new Label("STATE_ABBR").point([0.5, 0.5])).zindex(3)
shp.style.asSLD()

Map map = new Map(
        layers: [shp],
        backgroundColor: "white"
)
map.render(new File("style_composite.png")) 
