package geoscript.carto.io

import org.junit.jupiter.api.Test

class ScaleTextItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void scaleTextJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 380,
  "height": 280,
  "type": "map",
  "name": "mainMap",
  "layers": [
    {"layertype": "layer", "file": "src/test/resources/states.shp", "layername": "states", "style": "src/test/resources/states.sld"}
  ]
}, {
  "x": 10,
  "y": 250,
  "width": 380,
  "height": 40,
  "type": "scaletext",
  "map": "mainMap",
  "format": "#",
  "prefixText": "Scale: ",
  "horizontalAlign": "center",
  "verticalAlign": "middle",
  "color": "black",
  "font": {
    "name": "Arial",
    "style": "plain",
    "size": 14
  }
}"""
        createCartoFragment("json", "scaletext", fragment, 400, 300)
    }

    @Test
    void scaleTextXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>380</width>
    <height>280</height>
    <type>map</type>
    <name>mainMap</name>
    <layers>
        <layer>
            <layertype>layer</layertype>
            <file>src/test/resources/states.shp</file>
            <layername>states</layername>
            <style>src/test/resources/states.sld</style>
        </layer>
    </layers>
</item>
<item>
    <x>10</x>
    <y>250</y>
    <width>380</width>
    <height>40</height>
    <type>scaletext</type>
    <map>mainMap</map>
    <format>#</format>
    <prefixText>Scale :</prefixText>
    <horizontalAlign>center</horizontalAlign>
    <verticalAlign>middle</verticalAlign>
    <color>black</color>
    <font>
        <name>Arial</name>
        <style>plain</style>
        <size>14</size>
    </font>
</item>
"""
        createCartoFragment("xml", "scaletext", fragment, 400, 300)
    }

    @Test
    void scaleTextYaml() {
        String fragment = """- x: 10
  y: 10
  width: 380
  height: 280
  type: map
  name: mainMap
  layers:
  - layertype: layer
    file: src/test/resources/states.shp
    layername: states
    style: src/test/resources/states.sld
- x: 10
  y: 250
  width: 380
  height: 40
  type: scaletext
  map: mainMap
  format: "#"
  prefixText: 'Scale: '
  horizontalAlign: center
  verticalAlign: middle
  color: black
  font:
    name: Arial
    style: plain
    size: 14
"""
        createCartoFragment("yaml", "scaletext", fragment, 400, 300)
    }
}
