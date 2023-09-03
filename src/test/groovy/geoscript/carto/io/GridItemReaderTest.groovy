package geoscript.carto.io

import org.junit.jupiter.api.Test

class GridItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void gridJson() {
        String fragment = """{
  "x": 0,
  "y": 0,
  "width": 100,
  "height": 100,
  "type": "grid",
  "size": 10,
  "strokeColor": "black",
  "strokeWidth": 0.5
}"""
        createCartoFragment("json", "grid", fragment, 100, 100)
    }

    @Test
    void gridXml() {
        String fragment = """
<item>
    <x>0</x>
    <y>0</y>
    <width>100</width>
    <height>100</height>
    <type>grid</type>
    <size>10</size>
    <strokeColor>black</strokeColor>
    <strokeWidth>0.5</strokeWidth>
</item>"""
        createCartoFragment("xml", "grid", fragment, 100, 100)
    }

    @Test
    void gridYaml() {
        String fragment = """- x: 0
  y: 0
  width: 100
  height: 100
  type: grid
  size: 10
  strokeColor: black
  strokeWidth: 0.5
"""
        createCartoFragment("yaml", "grid", fragment, 100, 100)
    }
}
