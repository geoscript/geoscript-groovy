package geoscript.carto.io

import org.junit.jupiter.api.Test

class LegendItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void legendJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 380,
  "height": 190,
  "type": "map",
  "name": "mainMap",
  "layers": [
    {"layertype": "layer", "file": "src/test/resources/states.shp", "layername": "states", "style": "src/test/resources/states.sld"}
  ]
}, {
  "x": 10,
  "y": 210,
  "width": 380,
  "height": 70,
  "type": "legend",
  "map": "mainMap",
  "backgroundColor": "white",
  "title": "Legend",
  "titleFont":{
    "name": "Arial",
    "style": "bold",
    "size": 18
  },
  "titleColor": "black",
  "textColor": "black",
  "textFont": {
    "name": "Arial",
    "style": "plain",
    "size": 12
  },
  "numberFormat": "#.##",
  "legendEntryWidth": "50",
  "legendEntryHeight": "30",
  "gapBetweenEntries": "10"
}"""
        createCartoFragment("json", "legend", fragment, 400, 300)
    }

    @Test
    void legendXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>380</width>
    <height>190</height>
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
    <y>210</y>
    <width>380</width>
    <height>70</height>
    <type>legend</type>
    <map>mainMap</map>
    <backgroundColor>white</backgroundColor>
    <title>Legend</title>
    <titleFont>
        <name>Arial</name>
        <style>bold</style>
        <size>14</size>
    </titleFont>
    <titleColor>black</titleColor>
    <textColor>black</textColor>
    <textFont>
        <name>Arial</name>
        <style>plain</style>
        <size>12</size>
    </textFont>
    <numberFormat>#.##</numberFormat>
    <legendEntryWidth>50</legendEntryWidth>
    <legendEntryHeight>30</legendEntryHeight>
    <gapBetweenEntries>10</gapBetweenEntries>
</item>
"""
        createCartoFragment("xml", "legend", fragment, 400, 300)
    }

    @Test
    void legendYaml() {
        String fragment = """- x: 10
  y: 10
  width: 380
  height: 190
  type: map
  name: mainMap
  layers:
  - layertype: layer
    file: src/test/resources/states.shp
    layername: states
    style: src/test/resources/states.sld
- x: 10
  y: 210
  width: 380
  height: 70
  type: legend
  map: mainMap
  backgroundColor: white
  title: Legend
  titleFont:
    name: Arial
    style: bold
    size: 18
  titleColor: black
  textColor: black
  textFont:
    name: Arial
    style: plain
    size: 12
  numberFormat: "#.##"
  legendEntryWidth: '50'
  legendEntryHeight: '30'
  gapBetweenEntries: '10'
"""
        createCartoFragment("yaml", "legend", fragment, 400, 300)
    }

}
