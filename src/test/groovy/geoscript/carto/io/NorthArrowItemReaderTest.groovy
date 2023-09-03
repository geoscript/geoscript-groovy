package geoscript.carto.io

import org.junit.jupiter.api.Test

class NorthArrowItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void northArrowJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 130,
  "height": 130,
  "type": "northarrow",
  "style": "North",
  "fillColor1": "black",
  "fillColor2": "white",
  "strokeColor1": "black",
  "strokeColor2": "black",
  "strokeWidth": 1,
  "drawText": true,
  "textColor": "black",
  "font": {
    "name": "Arial",
    "style": "plain",
    "size": 24
  }
}"""
        createCartoFragment("json", "northarrow", fragment, 150, 150)
    }

    @Test
    void northArrowXML() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>130</width>
    <height>130</height>
    <type>northarrow</type>
    <style>NorthEastSouthWest</style>
    <fillColor1>black</fillColor1>
    <fillColor2>white</fillColor2>
    <strokeColor1>black</strokeColor1>
    <strokeColor2>black</strokeColor2>
    <strokeWidth>1</strokeWidth>
    <drawText>true</drawText>
    <textColor>black</textColor>
    <font>
        <name>Arial</name>
        <style>plain</style>
        <size>24</size>
    </font>
</item>"""
        createCartoFragment("xml", "northarrow", fragment, 150, 150)
    }

    @Test
    void northArrowYaml() {
        String fragment = """- x: 10
  y: 10
  width: 130
  height: 130
  type: northarrow
  style: North
  fillColor1: black
  fillColor2: white
  strokeColor1: black
  strokeColor2: black
  strokeWidth: 1
  drawText: true
  textColor: black
  font:
    name: Arial
    style: plain
    size: 24
"""
        createCartoFragment("yaml", "northarrow", fragment, 150, 150)
    }

}
