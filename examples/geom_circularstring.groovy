import geoscript.geom.*
import static geoscript.viewer.Viewer.draw

CircularString cs = new CircularString(
    [-122.358, 47.653], 
    [-122.348, 47.649], 
    [-122.348, 47.658], 
    [-122.358, 47.658], 
    [-122.358, 47.653]
)

println cs.wkt
println cs.curvedWkt
draw(cs, size: [400,400], bounds: cs.bounds.expandBy(0.005))