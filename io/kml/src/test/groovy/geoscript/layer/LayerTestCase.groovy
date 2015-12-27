package geoscript.layer

import geoscript.AssertUtil
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.*

class LayerTestCase {

    @Test void toKML() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(-122.444,47.2528), "House", 12.5], "house1", s1))
        def out = new java.io.ByteArrayOutputStream()
        layer1.toKML(out, {f->f.get("name")}, {f-> "${f.get('name')} ${f.get('price')}"})
        String kml = out.toString()
        String expected = """<?xml version="1.0" encoding="UTF-8"?><kml:kml xmlns:kml="http://www.opengis.net/kml/2.2">
  <kml:Document>
    <kml:Folder>
      <kml:name>facilities</kml:name>
      <kml:Schema kml:name="facilities" kml:id="facilities">
        <kml:SimpleField kml:name="name" kml:type="String"/>
        <kml:SimpleField kml:name="price" kml:type="Float"/>
      </kml:Schema>
      <kml:Placemark>
        <kml:name>House</kml:name>
        <kml:description>House 12.5</kml:description>
        <kml:Style>
          <kml:IconStyle>
            <kml:color>ff0000ff</kml:color>
          </kml:IconStyle>
        </kml:Style>
        <kml:ExtendedData>
          <kml:SchemaData kml:schemaUrl="#facilities">
            <kml:SimpleData kml:name="name">House</kml:SimpleData>
            <kml:SimpleData kml:name="price">12.5</kml:SimpleData>
          </kml:SchemaData>
        </kml:ExtendedData>
        <kml:Point>
          <kml:coordinates>-122.444,47.2528</kml:coordinates>
        </kml:Point>
      </kml:Placemark>
    </kml:Folder>
  </kml:Document>
</kml:kml>
"""
        AssertUtil.assertStringsEqual(expected.trim(), kml.trim(), trim: true)
    }

}
