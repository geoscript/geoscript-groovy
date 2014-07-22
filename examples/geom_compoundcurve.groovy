import geoscript.geom.*
import static geoscript.viewer.Viewer.draw

CompoundCurve cc = new CompoundCurve(
    new CircularString([1, 1], [6, 3], [10, 11]),
    new LineString([10,11],[3,5],[5,4])
)

println cc.wkt
println cc.curvedWkt
draw(cc, size: [400,400])