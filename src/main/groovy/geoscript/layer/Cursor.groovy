package geoscript.layer

import geoscript.feature.Feature
import geoscript.filter.Filter
import org.geotools.feature.FeatureIterator
import org.geotools.data.FeatureReader
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType

/**
 * A Cursor is a Iterator over a Feature objects.
 * <p>Most often you will get a Cursor by calling the getCursor() method of a Layer</p>
 * <code><pre>
 * Shapefile shp = new Shapefile('states.shp')
 * Cursor c = shp.cursor
 * while(c.hasNext()) {
 *      Feature f = c.next()
 * }
 * c.close()
 * </pre></code>
 * @author Jared Erickson
 */
class Cursor {

    /**
     * The Layer
     */
    private Layer layer

    /**
     * The GeoTools FeatureReader
     */
    private FeatureReader<SimpleFeatureType, SimpleFeature> iter

    /**
     * Create a new Cursor with a FeatureReader and a Layer
     * @param iter The GeoTools FeatureReader
     * @param layer The Geoscript Layer
     */
    Cursor(FeatureReader<SimpleFeatureType, SimpleFeature> iter, Layer layer) {
        this.iter = iter
        this.layer = layer
    }

    /**
     * Get the next Feature
     * @return The next Feature
     */
    Feature next() {
        new Feature(iter.next())
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
        iter.hasNext()
    }

    /**
     * Closes the Cursor.  This should always be called.
     */
    void close() {
        iter.close()
    }

}