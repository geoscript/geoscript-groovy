package geoscript.geom

import org.geotools.geometry.jts.ReferencedEnvelope
import geoscript.proj.Projection

/**
 * A Bounds is an Envelope with a Projection
 */
class Bounds {
	
    /**
     * The GeoTools' wrapped ReferencedEnvelope
     */
    ReferencedEnvelope env
	
    /**
     * Create a new Bounds wrapping a ReferencedEnvelope.
     * <p>ReferencedEnvelope e = new ReferencedEnvelope(1,3,2,4,null)</p>
     * <p>Bounds b = new Bounds(e)</p>
     */
    Bounds(ReferencedEnvelope env) {
        this.env = env
    }
	
    /**
     * Create a new Bounds with left, bottom, right, and top coordinates.
     * <p>Bounds b = new Bounds(1,2,3,4)</p>
     */
    Bounds(double l, double b, double r, double t) {
        this(new ReferencedEnvelope(l, r, b, t, null))
    }
	
    /**
     * Create a new Bounds with left, bottom, right, and top coordinates
     * and a Projection.
     * <p>Bounds b = new Bounds(1,2,3,4, new Projection("EPSG:2927"))</p>
     */
    Bounds(double l, double b, double r, double t, Projection proj) {
        this(new ReferencedEnvelope(l, r, b, t, proj.crs))
    }
	
    /**
     * Get the left most coordinate (minX)
     */
    def double getL() {
        env.minX()
    }
	
    /**
     * Get the right most coordinate (maxX)
     */
    def double getR() {
        env.maxX()
    }
	
    /**
     * Get the bottom most coordinate (minY)
     */
    def double getB() {
        env.minY()
    }
	
    /**
     * Get the top most coordinate (maxY)
     */
    def double getT() {
        env.maxY()
    }
	
    /**
     * Get the Projection (if any) or null
     */
    def Projection getProj() {
        if (env.coordinateReferenceSystem)
            return new Projection(env.coordinateReferenceSystem)
        else
            return null
    }
	
    /**
     * The string representation
     */
    def String toString() {
        "(${l},${b},${r},${t}${if (proj != null){',' + proj.id } else {''}})"
    }
}