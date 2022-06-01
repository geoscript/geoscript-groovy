import geoscript.layer.Shapefile
import geoscript.filter.Function
import geoscript.render.Draw
import geoscript.style.*

import javax.imageio.ImageIO

shp = new Shapefile("states.shp")
func = new Function("Recode(SUB_REGION,'N Eng','#6495ED','Mid Atl','#B0C4DE','S Atl','#00FFFF','E N Cen','#9ACD32','E S Cen','#00FA9A','W N Cen','#FFF8DC','W S Cen','#F5DEB3','Mtn','#F4A460','Pacific','#87CEEB')")

shp.style = new Fill(func) + new Stroke("#999999",0.1) + new Label("STATE_ABBR").point([0.5,0.5])
shp.style.asSLD()

ImageIO.write(Draw.drawToImage(shp), "png", new File("states_recode.png"))