package geoscript.carto.io

import org.junit.jupiter.api.Test

class TableItemReaderTest extends AbstractCartoReaderTest {

    @Test
    void tableJson() {
        String fragment = """{
  "x": 10,
  "y": 10,
  "width": 280,
  "height": 80,
  "type": "table",
  "columns": ["ID", "Name", "Abbreviation"],
  "rows": [
    {"ID": 1, "Name": "Washington", "Abbreviation": "WA"},
    {"ID": 2, "Name": "Oregon", "Abbreviation": "OR"},
    {"ID": 3, "Name": "California", "Abbreviation": "CA"}
  ]
}"""
        createCartoFragment("json", "table", fragment, 300, 100)
    }

    @Test
    void tableXml() {
        String fragment = """<item>
    <x>10</x>
    <y>10</y>
    <width>280</width>
    <height>80</height>
    <type>table</type>
    <columns>
        <column>ID</column>
        <column>Name</column>
        <column>Abbreviation</column>
    </columns>
    <rows>
        <row>
            <ID>1</ID>
            <Name>Washington</Name>
            <Abbreviation>WA</Abbreviation>
        </row>
        <row>
            <ID>2</ID>
            <Name>Oregon</Name>
            <Abbreviation>OR</Abbreviation>
        </row>
        <row>
            <ID>3</ID>
            <Name>California</Name>
            <Abbreviation>CA</Abbreviation>
        </row>
    </rows>
</item>
"""
        createCartoFragment("xml", "table", fragment, 300, 100)
    }

}
