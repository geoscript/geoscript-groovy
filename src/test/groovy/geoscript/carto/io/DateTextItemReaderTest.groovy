package geoscript.carto.io

import org.junit.jupiter.api.Test

class DateTextItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void dateTextJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 140,
  "height": 30,
  "type": "datetext",
  "date": "1/22/2022",
  "format": "MM/dd/yyyy",
  "horizontalAlign": "center",
  "verticalAlign": "middle",
  "color": "black",
  "font": {
    "name": "Arial",
    "style": "plain",
    "size": 14
  }
}"""
        createCartoFragment("json", "datetext", fragment, 150, 50)
    }

    @Test
    void dateTextXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>140</width>
    <height>30</height>
    <type>dateText</type>
    <date>1/22/2022</date>
    <format>MM/dd/yyyy</format>
    <horizontalAlign>center</horizontalAlign>
    <verticalAlign>middle</verticalAlign>
    <color>black</color>
    <font>
        <name>Arial</name>
        <style>plain</style>
        <size>14</size>
    </font>
</item>"""
        createCartoFragment("xml", "datetext", fragment, 150, 50)
    }

    @Test
    void dateTextYaml() {
        String fragment = """- x: 10
  y: 10
  width: 140
  height: 30
  type: datetext
  date: 1/22/2022
  format: MM/dd/yyyy
  horizontalAlign: center
  verticalAlign: middle
  color: black
  font:
    name: Arial
    style: plain
    size: 14
"""
        createCartoFragment("yaml", "datetext", fragment, 150, 50)
    }

}
