import geoscript.geom.Point
import static geoscript.render.Draw.*

def geom = new Point(10,10).buffer(10)
draw(geom)