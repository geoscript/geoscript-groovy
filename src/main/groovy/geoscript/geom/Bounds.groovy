package geoscript.geom

import org.geotools.geometry.jts.ReferencedEnvelope
import com.vividsolutions.jts.geom.Envelope
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
     * Create a new Bounds wrapping an Envelope.
     * <p><code>Envelope e = new Envelope(1,3,2,4)</code></p>
     * <p><code>Bounds b = new Bounds(e)</code></p>
     * @param env The ReferencedEnvelope
     */
    Bounds(Envelope env) {
        this(new ReferencedEnvelope(env, null))
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
     * Get the area of this Bounds
     * @return The area
     */
    double getArea() {
        env.area
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
     * Partitions the bounding box into a set of smaller bounding boxes.
     * @param res The resolution to tile at and should be in range 0-1.
     * @return A List of smaller bounding boxes
     */
    List<Bounds> tile(double res) {
        double dx = width * res
        double dy = height * res
        List bounds = []
        double y = south
        while (y < north) {
            double x = west
            while (x < east) {
                bounds.add(new Bounds(x,y,Math.min(x + dx, east), Math.min(y+dy, north), proj))
                x += dx
            }
            y += dy
        }
        bounds
    }

    /**
     * Calculate a quad tree for this Bounds between the start and stop levels. The Closure
     * is called for each new Bounds generated.
     * @param start The start level
     * @param stop The stop level
     * @param closure The Closure called for each new Bounds
     */
    void quadTree(int start, int stop, Closure closure) {
        Projection p = getProj()
        for(int level = start; level < stop; level++) {
            double factor = Math.pow(2, level)
            double dx = (this.east - this.west) / factor
            double dy = (this.north - this.south) / factor
            double minx = this.west
            for(int x = 0; x < factor; ++x) {
                double miny = this.south
                for(int y = 0; y < factor; ++y) {
                    closure(new Bounds(minx, miny, minx + dx, miny + dy, p))
                    miny += dy
                }
                minx += dx
            }
        }
    }

    /**
     * Get whether the Bounds is empty (width and height are zero)
     * @return Whether the Bounds is empty
     */
    boolean isEmpty() {
       env.empty 
    }
    
    /**
     * Determine whether this Bounds equals another Bounds
     * @param other The other Bounds
     * @return Whether this Bounds and the other Bounds are equal
     */
    boolean equals(Object other) {
        other instanceof Bounds && env.equals(other.env)
    }
    
    /**
     * Determine whether this Bounds contains the other Bounds
     * @param other The other Bounds
     * @return Whether this Bounds contains the other Bounds
     */
    boolean contains(Bounds other) {
        env.contains(other.env)
    }
    
    /**
     * Determine whether this Bounds intersects with the other Bounds
     * @param other The other Bounds
     * @return Whether this Bounds intersects with the other Bounds
     */
    boolean intersects(Bounds other) {
        env.intersects(other.env)
    }
    
    /**
     * Calculate the intersection between this Bounds and the other Bounds
     * @param other The other Bounds
     * @return The intersection Bounds between this and the other Bounds
     */
    Bounds intersection(Bounds other) {
        new Bounds(env.intersection(other.env))
    }

    /**
     * Ensure that the Bounds has a width and height.  Handle vertical and horizontal lines and points.
     * @return A new Bounds with a width and height
     */
    Bounds ensureWidthAndHeight() {
        Bounds b = new Bounds(env)
        if (b.width == 0 || b.height == 0) {
            if (b.height > 0) {
                double h = b.height / 2.0
                b = new Bounds(b.west - h, b.south, b.east + h, b.north, b.proj)
            } else if (b.width > 0) {
                double w = b.width / 2.0
                b = new Bounds(b.west, b.south - w, b.east, b.north + w, b.proj)
            } else {
                def e = new Point(b.west, b.south).buffer(0.1).envelopeInternal
                b = new Bounds(e.minX, e.minY, e.maxX, e.maxY, proj)
            }
        }
        return b
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
