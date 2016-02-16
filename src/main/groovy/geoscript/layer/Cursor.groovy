package geoscript.layer

import geoscript.feature.Feature
import org.geotools.data.store.ReprojectingFeatureIterator
import org.geotools.feature.FeatureIterator
import org.geotools.feature.FeatureTypes
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer
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
     * @param options A Map of options (sort - a List of Strings, start - is 0 based, max - the max number of features)
     * @param col The GeoTools FeatureCollection
     */
    Cursor(Map options = [:], FeatureCollection<SimpleFeatureType, SimpleFeature> col) {
        this.options = options
        this.col = col
        createIterator()
    }

    /**
     * Create a new Cursor with a FeatureCollection and a Layer
     * @param options A Map of options (sort - a List of Strings, start - is 0 based, max - the max number of features)
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
           long max = options.max as long
           if (!options.containsKey("sort")
               && layer
               && layer.workspace.format in ['Directory', 'org.geotools.data.shapefile.ShapefileDataStore', 'org.geotools.data.directory.DirectoryDataStore']) {
               this.iter = new SortedFeatureIterator(this.iter, col.schema, [SortBy.NATURAL_ORDER] as SortBy[], Integer.MAX_VALUE)
           }
           this.iter = new MaxFeaturesIterator<SimpleFeature>(this.iter, start, max)
       }
       if (options.containsKey("sourceProj") && options.containsKey("destProj")) {
           this.iter = new ReprojectingFeatureIterator(
               this.iter,
               options["sourceProj"].crs,
               options["destProj"].crs,
               FeatureTypes.transform(this.col.schema, options["destProj"].crs),
               new GeometryCoordinateSequenceTransformer()
           )
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
        try {
            iter.close()
        } catch(IllegalStateException ex) {
            // Do nothing, it just means it's already been closed
        }
    }

    /**
     * Reset and read the Features again.
     */
    void reset() {
        createIterator()
    }
}