import geoscript.feature.*
import geoscript.geom.LineString
import static geoscript.render.Draw.*

def schema  = new Schema("shapes",[new Field("geom","Polygon"), new Field("name", "String")])
def feature = new Feature([new LineString([0,0],[1,1]).bounds.polygon, "square"], "0",  schema)
draw(feature)