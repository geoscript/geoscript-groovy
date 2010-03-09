package geoscript.layer

import geoscript.feature.Feature
import geoscript.filter.Filter
import org.geotools.feature.FeatureIterator
import org.geotools.data.FeatureReader
import org.opengis.feature.simple.SimpleFeature
import org.opengis.feature.simple.SimpleFeatureType

/**
 * A Cursor is a Iterator over a Feature objects
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
     */
    Cursor(FeatureReader<SimpleFeatureType, SimpleFeature> iter, Layer layer) {
        this.iter = iter
        this.layer = layer
    }

    /**
     * Get the next Feature
     */
    Feature next() {
        new Feature(iter.next())
    }

    /**
     * Read n features into a List
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