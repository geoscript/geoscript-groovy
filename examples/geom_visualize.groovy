import geoscript.geom.*
import geoscript.viewer.Viewer

def poly1 = Geometry.fromWKT('POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0))')
def poly2 = Geometry.fromWKT('POLYGON ((20 20, 40 20, 40 40, 20 40, 20 20))')
def viewer = new Viewer()
viewer.draw([poly1, poly2], [20,20])
