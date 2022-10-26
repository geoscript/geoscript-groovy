package geoscript.carto.io

import org.junit.jupiter.api.Test

class ImageItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void imageJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 272,
  "height": 275,
  "type": "image",
  "path": "src/test/resources/image.png"
}"""
        createCartoFragment("json", "image", fragment, 292, 295)
    }

    @Test
    void imageXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>272</width>
    <height>275</height>
    <type>image</type>
    <path>src/test/resources/image.png</path>
</item>
"""
        createCartoFragment("xml", "image", fragment, 292, 295)
    }

}
