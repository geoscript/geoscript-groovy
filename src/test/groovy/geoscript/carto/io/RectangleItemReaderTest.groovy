package geoscript.carto.io

import org.junit.jupiter.api.Test

class RectangleItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void rectangleJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 30,
  "height": 30,
  "type": "rectangle",
  "fillColor": "white",
  "strokeColor": "black"
}"""
        createCartoFragment("json", "rectangle", fragment, 50, 50)
    }

    @Test
    void rectangleXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>30</width>
    <height>30</height>
    <type>rectangle</type>
    <fillColor>white</fillColor>
    <strokeColor>black</strokeColor>
</item>
"""
        createCartoFragment("xml", "rectangle", fragment, 50, 50)
    }

}
