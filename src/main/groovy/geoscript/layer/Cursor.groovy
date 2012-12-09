package geoscript.layer

import geoscript.feature.Feature
import org.geotools.feature.FeatureIterator
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType
import org.geotools.feature.FeatureCollection
import org.geotools.data.store.MaxFeaturesIterator
import org.geotools.data.sort.SortedFeatureIterator
import org.opengis.filter.sort.SortBy

/**
 * A Cursor is a Iterator over a Feature objects.
 * <p>Most often you will get a Cursor by calling the getCursor() method of a Layer</p>
 * <p><blockquote><pre>
 * Shapefile shp = new Shapefile('states.shp')
 * Cursor c = shp.cursor
 * while(c.hasNext()) {
 *      Feature f = c.next()
 * }
 * c.close()
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Cursor implements Iterator {

    /**
     * The Layer
     */
    private Layer layer

    /**
     * The GeoTools FeatureIterator
     */
    private FeatureIterator<SimpleFeature> iter

    /**
     * The GeoTools FeatureCollection
     */
    FeatureCollection<SimpleFeatureType, SimpleFeature> col

    /**
     * A Map of options.  Options can be: sort, start, max
     */
    private Map options

    /**
     * Create a new Cursor with a FeatureCollection
     * @param options A Map of options (sort, start, max)
     * @param col The GeoTools FeatureCollection
     */
    Cursor(Map options = [:], FeatureCollection<SimpleFeatureType, SimpleFeature> col) {
        this.options = options
        this.col = col
        createIterator()
    }

    /**
     * Create a new Cursor with a FeatureCollection and a Layer
     * @param options A Map of options (sort, start, max)
     * @param col The GeoTools FeatureCollection
     * @param layer The Geoscript Layer
     */
    Cursor(Map options = [:], FeatureCollection<SimpleFeatureType, SimpleFeature> col, Layer layer) {
        this.options = options
        this.col = col
        createIterator()
        this.layer = layer
    }

    /**
     * Create the FeatureIterator based on the FeatureCollection and options
     */
    protected void createIterator() {
       this.iter = col.features()
       if (options.containsKey("sort")) {
           this.iter = new SortedFeatureIterator(this.iter, col.schema, options.sort as SortBy[], Integer.MAX_VALUE)
       }
       if (options.containsKey("start") && options.containsKey("max")) {
           long start = options.start as long
           long end = start + options.max as long
           this.iter = new MaxFeaturesIterator<SimpleFeature>(this.iter, start, end)
       }
    }

    /**
     * Get the next Feature
     * @return The next Feature
     */
    Feature next() {
        Feature f = new Feature(iter.next())
        f.layer = layer
        f
    }

    /**
     * This method is unsupported and throws an UnsupportedOperationException
     */
    void remove() {
        throw new UnsupportedOperationException()
    }

    /**
     * Read n features into a List
     * @param n The number of features to read
     * @return A List of Features
     */
    List<Feature> read(int n) {
        List<Feature> features = []
        (0..n).each {
            features.append(next())
        }
        features
    }

    /**
     * Whether there are Features remaining
     * @return Whether there are Features remaining
     */
    boolean hasNext() {
        boolean b = iter.hasNext()
        // Auto close
        if (!b) {
            close()
        }
        b
    }

    /**
     * Closes the Cursor.  This should always be called.
     */
    void close() {
        iter.close()
    }

    /**
     * Reset and read the Features again.
     */
    void reset() {
        createIterator()
    }
}