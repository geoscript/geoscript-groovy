package geoscript.carto.io

import org.junit.jupiter.api.Test

class ScaleBarItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void scaleBarJson() {
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
  "type": "scalebar",
  "map": "mainMap",
  "strokeColor": "black",
  "strokeWidth": 1,
  "border": 5,
  "units": "METRIC",
  "fillColor": "white",
  "font": {
    "name": "Arial",
    "style": "plain",
    "size": 14
  }
}"""
        createCartoFragment("json", "scalebar", fragment, 400, 300)
    }

    @Test
    void scaleBarXml() {
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
    <width>320</width>
    <height>40</height>
    <type>scalebar</type>
    <map>mainMap</map>
    <strokeColor>black</strokeColor>
    <strokeWidth>1</strokeWidth>
    <border>5</border>
    <units>US</units>
    <fillColor>white</fillColor>
    <font>
        <name>Arial</name>
        <style>plain</style>
        <size>14</size>
    </font>
</item>
"""
        createCartoFragment("xml", "scalebar", fragment, 400, 300)
    }
}
