package geoscript.geom

import org.geotools.geometry.jts.ReferencedEnvelope
import geoscript.proj.Projection

/**
 * A Bounds is an Envelope with a Projection.
 * <p><code>
 * Bounds b = new Bounds(1,2,3,4, new Projection("EPSG:2927"))
 * </p></code>
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
     * Create a new Bounds with left, bottom, right, and top coordinates.
     * <p><code>Bounds b = new Bounds(1,2,3,4)</code></p>
     * @param l The left most coordinate (minX)
     * @param b the bottom most coordinate (minY)
     * @param r The right most coordinate (maxX)
     * @param t The top most coordinate (maxY)
     */
    Bounds(double l, double b, double r, double t) {
        this(new ReferencedEnvelope(l, r, b, t, null))
    }
	
    /**
     * Create a new Bounds with left, bottom, right, and top coordinates
     * and a Projection.
     * <p><code>Bounds b = new Bounds(1,2,3,4, new Projection("EPSG:2927"))</code></p>
     * <p><code>Bounds b = new Bounds(1,2,3,4, "EPSG:2927")</code></p>
     * @param l The left most coordinate (minX)
     * @param b the bottom most coordinate (minY)
     * @param r The right most coordinate (maxX)
     * @param t The top most coordinate (maxY)
     * @param proj The Projection can either be a Projection or a String
     */
    Bounds(double l, double b, double r, double t, def proj) {
        this(new ReferencedEnvelope(l, r, b, t, new Projection(proj).crs))
    }
	
    /**
     * Get the left most coordinate (minX)
     * @return The left most coordinate (minX)
     */
    double getL() {
        env.minX()
    }
	
    /**
     * Get the right most coordinate (maxX)
     * @return The right most coordinate (maxX)
     */
    double getR() {
        env.maxX()
    }
	
    /**
     * Get the bottom most coordinate (minY)
     * @return The bottom most coordinate (minY)
     */
    double getB() {
        env.minY()
    }
	
    /**
     * Get the top most coordinate (maxY)
     * @return The top most coordinate (maxY)
     */
    double getT() {
        env.maxY()
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
     * Convert this Bounds into a Geometry object
     * @return A Geometry
     */
    Geometry getGeometry() {
        Geometry.wrap(Geometry.factory.toGeometry(env))
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        "(${l},${b},${r},${t}${if (proj != null){',' + proj.id } else {''}})"
    }
}