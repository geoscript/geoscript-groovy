package geoscript.layer

import geoscript.feature.Feature
import geoscript.workspace.Directory
import geoscript.workspace.Memory
import org.geotools.data.DataUtilities
import org.geotools.data.DefaultTransaction
import org.geotools.data.FeatureStore
import org.geotools.data.Transaction
import org.geotools.data.collection.ListFeatureCollection
import org.geotools.feature.DefaultFeatureCollection

/**
 * The Writer allows for adding batches of Features to a Layer
 * within a Transaction.
 * <p><blockquote><pre>
 * Writer writer = new Writer(layer, batch: 1000, transaction: 'default')
 * try {
 *      Feature f = writer.newFeature
 *      writer.add(f)
 * } finally {
 *      writer.close()
 * }
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Writer {

    /**
     * The number of features to write at one time
     */
    private int batch

    /**
     * The Layer we are editing
     */
    private Layer layer

    /**
     * The FeatureStore
     */
    private FeatureStore store

    /**
     * The Transaction used during writing
     */
    private Transaction transaction

    /**
     * The internal cache of Features
     * Must be ListFeatureCollection to preserve sorting.
     * The DefaultFeatureCollection sorts by feature ID and DataUtilities.collection(List)
     * converts to a DefaultFeatureCollection.
     */
    private ListFeatureCollection features

    /**
     * Create a new Writer for a Layer.
     * @param options The named parameters
     * <ul>
     *   <li>batch: The number of features to write at one time (defaults to 1000)</li>
     *   <li>transaction: The type of transaction: null, auto or autocommit, or default.  The default value
     *      depends on the type of Layer.
     *   </li>
     * </ul>
     * @param layer The Layer to write to
     */
    Writer(Map options = [:], Layer layer) {
        this.layer = layer
        this.store = layer.fs as FeatureStore
        this.batch = options.get("batch", 1000)
        String transactionType = options.get("transaction", getDefaultTransactionType(layer))
        this.transaction = getTransactionByType(transactionType, layer)
        this.store.transaction = transaction
        this.features = new ListFeatureCollection(layer.schema.featureType)
    }

    /**
     * Get a default Transaction by string type
     * @param type The string type (null, auto | autocommit, default)
     * @param layer The Layer
     * @return A GeoTools Transaction
     */
    private Transaction getTransactionByType(String type, Layer layer) {
        if (type.equalsIgnoreCase("null")) {
            null
        } else if (type.equalsIgnoreCase("auto") || type.equalsIgnoreCase("autocommit")) {
            Transaction.AUTO_COMMIT
        } else {
            new DefaultTransaction("${layer.name}Transaction")
        }
    }

    /**
     * Get a default transaction type by Layer
     * @param layer The Layer
     * @return A transaction type (null, auto | autocommit, default)
     */
    private String getDefaultTransactionType(Layer layer) {
        if (layer instanceof Shapefile || layer.workspace instanceof Directory
                || layer.fs instanceof org.geotools.data.directory.DirectoryFeatureStore) {
            "null"
        } else if (layer.workspace instanceof Memory || layer.workspace instanceof geoscript.workspace.Property) {
            "autocommit"
        } else {
            "default"
        }
    }

    /**
     * Get a new Feature with default values
     * @return A new Feature
     */
    Feature getNewFeature() {
        this.layer.schema.feature()
    }

    /**
     * Add a Feature
     * @param feature The Feature to add
     */
    void add(Feature feature) {
        // Make sure the Schema of the Feature matches the Schema of the Layer
        if (!feature.schema.equals(layer.schema)) {
            feature = layer.schema.feature(feature)
        }
        // Add the feature to the internal cache
        features.add(feature.f)
        // If there are more features in the cache than the batch
        // size, write it
        if (features.size() >= batch) {
            // Write to the store
            store.addFeatures(features)
            // Commit and transaction
            if (transaction) transaction.commit()
            // And the clear the internal cache
            features.clear()
        }
    }

    /**
     * Closes the writing session.  If there are unwritten Features in the cache, they
     * are written before the Transaction is finally closed.
     */
    void close() {
        // Make sure to add any left over cached features
        if (!features.isEmpty()) {
            store.addFeatures(features)
            if (transaction) transaction.commit()
            features.clear()
        }
        // Close the transaction
        if (transaction) transaction.close()
        // Reset the store's transaction to auto commit
        store.transaction = Transaction.AUTO_COMMIT
    }

    /**
     * Write batches of Features to the Layer
     * @param options The named parameters
     * <ul>
     *   <li>batch: The number of features to write at one time (defaults to 1000)</li>
     *   <li>transaction: The type of transaction: null, auto or autocommit, or default.  The default value
     *      depends on the type of Layer.
     *   </li>
     * </ul>
     * @param layer The Layer to write to
     * @param c The Cursor which takes the Writer
     */
    static void write(Map options = [:], Layer layer, Closure c) {
        Writer writer = new Writer(options, layer)
        try {
            c.call(writer)
        } finally {
            writer.close()
        }
    }
}
