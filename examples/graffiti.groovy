@GrabResolver(name="graffiti", root="http://simple-dm.googlecode.com/svn/repository")
@Grab("com.goodercode:graffiti:1.0-SNAPSHOT")
import graffiti.*
import geoscript.geom.Geometry

@Get("/buffer")
def buffer() {
    Geometry.fromWKT(params.geom).buffer(params.distance as double).wkt
}

@Get("/centroid")
def centroid() {
    Geometry.fromWKT(params.geom).centroid.wkt
}

@Get("/convexHull")
def convexHull() {
    Geometry.fromWKT(params.geom).convexHull.wkt
}

Graffiti.root 'graffiti'
Graffiti.serve this
Graffiti.start()
