import geoscript.geom.Point
import static geoscript.render.Plot.*

def geom = new Point(10,10).buffer(10)
plot(geom)