package geoscript.geom

import org.geotools.geometry.jts.ReferencedEnvelope
import geoscript.proj.Projection

/**
 * A Bounds is an Envelope with a Projection.
 * <p><code>
 * Bounds b = new Bounds(1,2,3,4, new Projection("EPSG:2927"))
 * </p></code>
 * @author Jared Erickson
 */
class Bounds {
	
    /**
     * The GeoTools' wrapped ReferencedEnvelope
     */
    ReferencedEnvelope env
	
    /**
     * Create a new Bounds wrapping a ReferencedEnvelope.
     * <p><code>ReferencedEnvelope e = new ReferencedEnvelope(1,3,2,4,null)</code></p>
     * <p><code>Bounds b = new Bounds(e)</code></p>
     * @param env The ReferencedEnvelope
     */
    Bounds(ReferencedEnvelope env) {
        this.env = env
    }
	
    /**
     * Create a new Bounds with west, south, east, and north coordinates.
     * <p><code>Bounds b = new Bounds(1,2,3,4)</code></p>
     * @param west The left/west most coordinate (minX)
     * @param south the bottom/south most coordinate (minY)
     * @param east The right/east most coordinate (maxX)
     * @param north The top/north most coordinate (maxY)
     */
    Bounds(double west, double south, double east, double north) {
        this(west, south, east, north, null)
    }
	
    /**
     * Create a new Bounds with west, south, east, and north coordinates
     * and a Projection.
     * <p><code>Bounds b = new Bounds(1,2,3,4, new Projection("EPSG:2927"))</code></p>
     * <p><code>Bounds b = new Bounds(1,2,3,4, "EPSG:2927")</code></p>
     * @param west The left/west most coordinate (minX)
     * @param south the bottom/south most coordinate (minY)
     * @param east The right/east most coordinate (maxX)
     * @param north The top/north most coordinate (maxY)
     * @param proj The Projection can either be a Projection or a String
     */
    Bounds(double west, double south, double east, double north, def proj) {
        this(new ReferencedEnvelope(west, east, south, north, new Projection(proj).crs))
    }
	
    /**
     * Get the left most coordinate (minX)
     * @return The left most coordinate (minX)
     */
    @Deprecated
    double getL() {
        west
    }
	
    /**
     * Get the left/west most coordinate (minX)
     * @return The left/west most coordinate (minX)
     */
    double getWest() {
        env.minX()
    }

    /**
     * Get the right/east most coordinate (maxX)
     * @return The right/east most coordinate (maxX)
     */
    @Deprecated
    double getR() {
        east
    }

    /**
     * Get the right/east most coordinate (maxX)
     * @return The right/east most coordinate (maxX)
     */
    double getEast() {
        env.maxX()
    }

    /**
     * Get the bottom/south most coordinate (minY)
     * @return The bottom/south most coordinate (minY)
     */
    @Deprecated
    double getB() {
        south
    }

    /**
     * Get the bottom/south most coordinate (minY)
     * @return The bottom/south most coordinate (minY)
     */
    double getSouth() {
        env.minY()
    }

    /**
     * Get the top/north most coordinate (maxY)
     * @return The top/north most coordinate (maxY)
     */
    @Deprecated
    double getT() {
        north
    }

    /**
     * Get the top/north most coordinate (maxY)
     * @return The top/north most coordinate (maxY)
     */
    double getNorth() {
        env.maxY()
    }

    /**
     * Get the width
     * @return The width
     */
    double getWidth() {
        env.width
    }

    /**
     * Get the height
     * @return The height
     */
    double getHeight() {
        env.height
    }

    /**
     * Expand the Bounds by the given distance in all directions
     * @param distance The distance
     * @return The modified Bounds
     */
    Bounds expandBy(double distance) {
        env.expandBy(distance)
        this
    }

    /**
     * Expand this Bounds to include another Bounds
     * @param other Another Bounds
     * @return The modified Bounds
     */
    Bounds expand(Bounds other) {
        env.expandToInclude(other.env)
        this
    }

    /**
     * Scales the current Bounds by a specific factor.
     * @param factor The scale factor
     * @return The new scaled Bounds
     */
    Bounds scale(double factor) {
        double w = width * (factor - 1) / 2
        double h = height * (factor - 1) / 2
        new Bounds(west - w, south - h, east + w, north + h)
    }


    /**
     * Get the Projection (if any) or null
     * @return The Projection (if any) or null
     */
    Projection getProj() {
        if (env.coordinateReferenceSystem)
            return new Projection(env.coordinateReferenceSystem)
        else
            return null
    }
	
    /**
     * Reprojects the Bounds
     * @param proj A Projection or String
     * @return The reprojected Bounds
     */
    Bounds reproject(def proj) {
        proj = new Projection(proj)
        new Bounds(env.transform(proj.crs, true))
    }

    /**
     * Convert this Bounds into a Geometry object
     * @return A Geometry
     */
    Geometry getGeometry() {
        Geometry.wrap(Geometry.factory.toGeometry(env))
    }

    /**
     * Convert this Bounds into a Polygon
     * @return A Polygon
     */
    Polygon getPolygon() {
        new Polygon([west, south], [west, north], [east, north], [east, south], [west, south])
    }

    /**
     * Get a value from this Bounds at the given index (west = 0, south = 1,
     * east = 2, north = 3).
     * <p><code>Bounds b = new Bounds(1,2,3,4)</code></p>
     * <p><code>def w = b[0]</code></p>
     * <p><code>def (w,s,e,n) = b</code></p>
     * @return A value from this Bounds or null if the index is greater than 3
     */
    Object getAt(int index) {
        if (index == 0) {
            west
        } else if (index == 1) {
            south
        } else if (index == 2) {
            east
        } else if (index == 3) {
            north
        } else {
            null
        }
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        "(${west},${south},${east},${north}${if (proj != null){',' + proj.id } else {''}})"
    }
}
