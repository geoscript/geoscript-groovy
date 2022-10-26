package geoscript.carto.io

import org.junit.jupiter.api.Test

class TextItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void textJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 140,
  "height": 30,
  "type": "text",
  "text": "Map Text",
  "horizontalAlign": "center",
  "verticalAlign": "middle",
  "color": "black",
  "font": {
    "name": "Arial",
    "style": "plain",
    "size": 14
  }
}"""
        createCartoFragment("json", "text", fragment, 150, 50)
    }

    @Test
    void textXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>140</width>
    <height>30</height>
    <type>text</type>
    <text>Map Text</text>
    <horizontalAlign>center</horizontalAlign>
    <verticalAlign>middle</verticalAlign>
    <color>black</color>
    <font>
        <name>Arial</name>
        <style>plain</style>
        <size>14</size>
    </font>
</item>"""
        createCartoFragment("xml", "text", fragment, 150, 50)
    }

}
