package geoscript.carto.io

import org.junit.jupiter.api.Test

class MapItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void mapJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 380,
  "height": 280,
  "type": "map",
  "name": "mainMap",
  "imageType": "png",
  "backgroundColor": "white",
  "fixAspectRatio": true,
  "proj": "EPSG:4326",
  "bounds": {
    "minX": -180,
    "minY": -90,
    "maxX": 180,
    "maxY": 90
  },
  "layers": [
    {"layertype": "layer", "file": "src/test/resources/states.shp", "layername": "states", "style": "src/test/resources/states.sld"}
  ]
}"""
        createCartoFragment("json", "map", fragment, 400, 300)
    }

    @Test
    void mapXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>380</width>
    <height>280</height>
    <type>map</type>
    <name>mainMap</name>
    <imageType>png</imageType>
    <backgroundColor>white</backgroundColor>
    <fixAspectRatio>true</fixAspectRatio>
    <proj>EPSG:4326</proj>
    <bounds>
        <minX>-180</minX>
        <minY>-90</minY>
        <maxX>180</maxX>
        <maxY>90</maxY>
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
</item>"""
        createCartoFragment("xml", "map", fragment, 400, 300)
    }

}
