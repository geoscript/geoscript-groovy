package geoscript

import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import okio.Okio

class ServerTestUtil {

    static void withServer(Closure closure) {
        MockWebServer server = new MockWebServer()
        try {
            closure.call(server)
        } finally {
            server.shutdown()
        }
    }



    static File getResource(String resource) {
        new File(ServerTestUtil.getClassLoader().getResource(resource).toURI())
    }

    static Buffer fileToBytes(File file) throws IOException {
        Buffer result = new Buffer()
        result.writeAll(Okio.source(file))
        result
    }

}
