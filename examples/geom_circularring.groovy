import geoscript.geom.*
import static geoscript.viewer.Viewer.draw

CircularRing cr = new CircularRing(
    [1, 1], [2, 0], [1, 1]
)

println cr.wkt
println cr.curvedWkt
draw(cr, size: [400,400])