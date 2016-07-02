package geoscript.feature.io

import geoscript.feature.Schema
import org.geotools.data.DataUtilities
import org.opengis.feature.simple.SimpleFeatureType

/**
 * Read a Schema from a simple String.  This implementation uses GeoTools
 * DataUtilities.createType method.
 * @author Jared Erickosn
 */
class StringSchemaReader implements SchemaReader {

    @Override
    Schema read(String str) {
        read([:], str)
    }

    Schema read(Map options, String str) {
        String uri = options.get('uri','http://geoscript.org/feature')
        String name = options.get('name','layer')
        SimpleFeatureType featureType = DataUtilities.createType(uri, name, str)
        new Schema(featureType)
    }

}
