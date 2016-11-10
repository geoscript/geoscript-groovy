package geoscript.feature.io

import geoscript.feature.Schema
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * The XmlSchemaWriter Unit Test
 * @author Jared Erickson
 */
class XmlSchemaWriterTestCase {

    @Test void write() {
        Schema schema = new Schema("points", "geom:Point:srid=4326,name:String,price:float")
        SchemaWriter writer = new XmlSchemaWriter()
        String str = writer.write(schema)
        assertEquals """<schema>
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
</schema>""", str
    }

}
