package geoscript.carto.io

import geoscript.carto.CartoBuilder
import org.junit.jupiter.api.io.TempDir

class AbstractCartoReaderTest {

    @TempDir
    private File folder

    boolean saveToTarget = false

    void createCartoFragment(String type, String name, String fragment, int width, int height) {
        CartoReader cartoReader
        String document
        if (type.equalsIgnoreCase("json")) {
            cartoReader = new JsonCartoReader()
            document = createJsonDocument(fragment, width, height)
        } else if (type.equalsIgnoreCase("xml")) {
            cartoReader = new XmlCartoReader()
            document = createXmlDocument(fragment, width, height)
        } else {
            throw new IllegalArgumentException("Unknown CartoReader ${type}!")
        }
        CartoBuilder builder = cartoReader.read(document)
        String fileName = "carto_${name}_${type}.png"
        File file = saveToTarget ? new File("target/${fileName}") : new File(folder, fileName)
        file.withOutputStream { OutputStream outputStream ->
            builder.build(outputStream)
        }
    }

    private String createJsonDocument(String fragment, int width, int height) {
        """
        {
          "type": "png",
          "width": ${width},
          "height": ${height},
          "items": [
             ${fragment}  
          ]
        }
        """.stripMargin().trim()
    }

    private String createXmlDocument(String fragment, int width, int height) {
        """
        <carto>
            <type>png</type>
            <width>${width}</width>
            <height>${height}</height>
            <items>
                ${fragment}
            </items>
        </carto>  
        """.stripMargin().trim()
    }

}
