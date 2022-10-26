package geoscript.carto.io

import org.junit.jupiter.api.Test

class OverviewMapItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void overviewMapJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 580,
  "height": 240,
  "type": "map",
  "name": "mainMap",
  "fixAspectRatio": false,
  "bounds": {
    "minX": -108.917446,
    "minY": 43.519820,
    "maxX": -89.229946,
    "maxY": 50.137433
  },
  "layers": [
    {"layertype": "layer", "file": "src/test/resources/states.shp", "layername": "states", "style": "src/test/resources/states.sld"}
  ]
},
{
  "x": 10,
  "y": 260,
  "width": 580,
  "height": 240,
  "type": "overViewMap",
  "zoomIntoBounds": false,
  "scaleFactor": 2.0,
  "linkedMap": "mainMap", 
  "layers": [
    {"layertype": "layer", "file": "src/test/resources/states.shp", "layername": "states", "style": "src/test/resources/states.sld"}
  ]
}
"""
        createCartoFragment("json", "overViewMap", fragment, 600, 510)
    }

    @Test
    void overViewMapXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>580</width>
    <height>240</height>
    <type>map</type>
    <name>mainMap</name>
    <imageType>png</imageType>
    <backgroundColor>white</backgroundColor>
    <fixAspectRatio>true</fixAspectRatio>
    <proj>EPSG:4326</proj>
    <bounds>
        <minX>-108.917446</minX>
        <minY>43.519820</minY>
        <maxX>-89.229946</maxX>
        <maxY>50.137433</maxY>
        <proj>EPSG:4326</proj>
    </bounds>
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
    <y>260</y>
    <width>580</width>
    <height>240</height>
    <type>overviewMap</type>
    <zoomIntoBounds>false</zoomIntoBounds>
    <scaleFactor>2.0</scaleFactor>
    <linkedMap>mainMap</linkedMap>
    <layers>
        <layer>
            <layertype>layer</layertype>
            <file>src/test/resources/states.shp</file>
            <layername>states</layername>
            <style>src/test/resources/states.sld</style>
        </layer>
    </layers>
</item>"""
        createCartoFragment("xml", "overviewMap", fragment, 600, 510)
    }
}
