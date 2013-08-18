import geoscript.geom.*
import geoscript.plot.Scatter

def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 100)
List data = points.geometries.collect{pt ->
    [pt.x,pt.y]
}
Scatter.scatterplot(data).show()