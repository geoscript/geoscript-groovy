import geoscript.geom.*
import static geoscript.viewer.Viewer.draw

CompoundRing cr = new CompoundRing(
    new CircularString([0, 2], [2, 0], [4, 2]), 
    new CircularString([4, 2], [2, 4], [0, 2])
)

println cr.wkt
println cr.curvedWkt
draw(cr, size: [400,400])