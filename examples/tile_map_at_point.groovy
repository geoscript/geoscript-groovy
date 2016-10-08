import geoscript.geom.Geometry
import geoscript.geom.Point
import geoscript.layer.OSM
import geoscript.layer.Raster
import geoscript.proj.Projection
import javax.imageio.ImageIO

OSM osm = new OSM()

[
    "POINT (-100.777587890625 46.800059446787316)",
    "POINT (-122.38494873046875 47.570966845786124)",
    "POINT (12.5244140625 42.032974332441405)",
    "POINT (23.752441406249996 38.004819966413194)"
].eachWithIndex { String wkt, int i ->
    [1,4,8,16].each {int z ->
        Raster raster = osm.getRaster(Projection.transform(Geometry.fromWKT(wkt), "EPSG:4326", "EPSG:3857") as Point, z, 400, 400)
        ImageIO.write(raster.image, "png", new File("map_at_point_${i}_${z}.png"))
    }
}