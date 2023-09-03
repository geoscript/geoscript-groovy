package geoscript.carto.io

import org.junit.jupiter.api.Test

class LineItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void lineJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 180,
  "height": 0,
  "type": "line",
  "strokeColor": "black",
  "strokeWidth": 2
}"""
        createCartoFragment("json", "line", fragment, 200, 50)
    }

    @Test
    void lineXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>180</width>
    <height>0</height>
    <type>line</type>
    <strokeColor>black</strokeColor>
    <strokeWidth>2</strokeWidth>
</item>
"""
        createCartoFragment("xml", "line", fragment, 200, 50)
    }

    @Test
    void lineYaml() {
        String fragment = """- x: 10
  y: 10
  width: 180
  height: 0
  type: line
  strokeColor: black
  strokeWidth: 2"""
        createCartoFragment("yaml", "line", fragment, 200, 50)
    }

}
