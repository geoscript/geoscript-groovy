package geoscript.carto.io

import org.junit.jupiter.api.Test

class ParagraphItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void paragraphJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 240,
  "height": 140,
  "type": "paragraph",
  "text": "The Carto package contains classes for creating cartographic documents. All items are added to the document with x and y coordinates whose origin is the upper left and width and a height.",
  "color": "black",
  "font": {
    "name": "Arial",
    "style": "plain",
    "size": 14
  }
}"""
        createCartoFragment("json", "paragraph", fragment, 250, 150)
    }

    @Test
    void paragraphXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>240</width>
    <height>140</height>
    <type>paragraph</type>
    <text>The Carto package contains classes for creating cartographic documents. All items are added to the document with x and y coordinates whose origin is the upper left and width and a height.t</text>
    <color>black</color>
    <font>
        <name>Arial</name>
        <style>plain</style>
        <size>14</size>
    </font>
</item>"""
        createCartoFragment("xml", "paragraph", fragment, 250, 150)
    }

}
