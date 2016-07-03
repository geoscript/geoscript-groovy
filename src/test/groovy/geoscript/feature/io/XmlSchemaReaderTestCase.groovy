package geoscript.feature.io

import geoscript.feature.Schema
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * The XmlSchemaReader Unit Test
 * @author Jared Erickson
 */
class XmlSchemaReaderTestCase {

    @Test void read() {
        String str = """<schema>
  <name>points</name>
  <projection>EPSG:4326</projection>
  <geometry>geom</geometry>
  <fields>
    <field>
      <name>geom</name>
      <type>Point</type>
      <projection>EPSG:4326</projection>
    </field>
    <field>
      <name>name</name>
      <type>String</type>
    </field>
    <field>
      <name>price</name>
      <type>Float</type>
    </field>
  </fields>
</schema>"""
        SchemaReader reader = new XmlSchemaReader()
        Schema schema = reader.read(str)
        assertEquals "points", schema.name
        assertEquals 3, schema.fields.size()
        assertEquals "geom", schema.fields[0].name
        assertEquals "Point", schema.fields[0].typ
        assertEquals "EPSG:4326", schema.fields[0].proj.id
        assertEquals "name", schema.fields[1].name
        assertEquals "String", schema.fields[1].typ
        assertNull schema.fields[1].proj
        assertEquals "price", schema.fields[2].name
        assertEquals "Float", schema.fields[2].typ
        assertNull schema.fields[2].proj
    }

}
