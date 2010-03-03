import geoscript.geom.*
import geoscript.viewer.Viewer

def poly = Geometry.fromWKT('POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0))')
def viewer = new Viewer()
viewer.draw(poly, [20,20])
