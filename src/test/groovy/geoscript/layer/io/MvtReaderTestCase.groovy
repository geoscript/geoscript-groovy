package geoscript.layer.io

import geoscript.layer.Layer
import org.apache.commons.codec.binary.Base64
import org.junit.Test
import static org.junit.Assert.assertEquals

/**
 * The MvtReader Unit Test
 * @author Jared Erickson
 */
class MvtReaderTestCase {

    @Test void readFromString() {
        URL url = getClass().getClassLoader().getResource("1582.mvt")
        Reader reader = new MvtReader()
        byte[] bytes = url.bytes
        String str = new String(Base64.encodeBase64(bytes), "UTF-8")
        Layer layer = reader.read(str)
        assertEquals 6, layer.count
    }

    @Test void readFromUrl() {
        URL url = getClass().getClassLoader().getResource("1582.mvt")
        MvtReader reader = new MvtReader()
        Layer layer = reader.read(url)
        assertEquals 6, layer.count
    }

    @Test void readFromFile() {
        URL url = getClass().getClassLoader().getResource("1582.mvt")
        Reader reader = new MvtReader()
        Layer layer = reader.read(new File(url.toURI()))
        assertEquals 6, layer.count
    }

    @Test void readFromInputStream() {
        URL url = getClass().getClassLoader().getResource("1582.mvt")
        InputStream input = url.openStream()
        Reader reader = new MvtReader()
        Layer layer = reader.read(input)
        input.close()
        assertEquals 6, layer.count
    }

}
