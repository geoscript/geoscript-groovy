package geoscript.style

import geoscript.AssertUtil
import geoscript.feature.Field
import geoscript.style.io.SLDWriter
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.filter.Filter

import org.geotools.styling.Style
import org.opengis.filter.Filter as GTFilter
import org.geotools.styling.LineSymbolizer
import org.geotools.styling.PointSymbolizer
import org.geotools.styling.PointSymbolizerImpl
import org.geotools.styling.PolygonSymbolizer
import org.geotools.styling.TextSymbolizer

/**
 * The Symbolizer Unit Test
 */
class SymbolizerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void construct() {

        // Create a Symbolizer
        Symbolizer sym = new Symbolizer()
        assertEquals Filter.PASS, sym.filter
        assertEquals(-1, sym.scale[0], 0.1)
        assertEquals(-1, sym.scale[1], 0.1)
        assertEquals 0, sym.z
        assertTrue sym.options.isEmpty()

        // Add Filter
        assertTrue sym.where(new Filter("name='Washington'")) instanceof Symbolizer
        assertEquals "[ name = Washington ]", sym.filter.toString()

        // Add min and max scale dependencies
        assertTrue sym.range(100, 500) instanceof Symbolizer
        assertEquals 100, sym.scale[0], 0.1
        assertEquals 500, sym.scale[1], 0.1

        // Add Z-index
        assertTrue sym.zindex(5) instanceof Symbolizer
        assertEquals 5, sym.z

        // Make sure asSLD() works
        sym.asSLD()
    }

    @Test void range() {

        // Both named parameters
        Symbolizer sym = new Symbolizer().range(min: 100, max:200)
        assertEquals 100, sym.scale[0], 0.1
        assertEquals 200, sym.scale[1], 0.1

        // Just one named parameters (min)
        sym = new Symbolizer().range(min: 100)
        assertEquals 100, sym.scale[0], 0.1
        assertEquals(-1, sym.scale[1], 0.1)

        // Just one named parameters (max)
        sym = new Symbolizer().range(max: 1000)
        assertEquals(-1, sym.scale[0], 0.1)
        assertEquals 1000, sym.scale[1], 0.1

        // Constructor
        sym = new Symbolizer().range(100, 200)
        assertEquals 100, sym.scale[0], 0.1
        assertEquals 200, sym.scale[1], 0.1

    }
    
    @Test void plus() {
        def composite = new Fill("red") + new Stroke("#ffffff")
        assertTrue composite instanceof Composite
        assertEquals 2, composite.parts.size()
        assertEquals "Composite (Fill(color = #ff0000, opacity = 1.0), Stroke(color = #ffffff, width = 1))", composite.toString()
    }

    @Test void and() {
        def composite = new Fill("red").and(new Stroke("#ffffff"))
        assertTrue composite instanceof Composite
        assertEquals 2, composite.parts.size()
        assertEquals "Composite (Fill(color = #ff0000, opacity = 1.0), Stroke(color = #ffffff, width = 1))", composite.toString()
    }

    @Test void getDefault() {
        def pointSym = Symbolizer.getDefault("point")
        assertTrue pointSym instanceof Shape

        def lineSym = Symbolizer.getDefault("linestring")
        assertTrue lineSym instanceof Stroke

        def polygonSym = Symbolizer.getDefault("polygon")
        assertTrue polygonSym instanceof Composite
        assertEquals 2, polygonSym.parts.size()

        def pointSym2 = Symbolizer.getDefault("point", "black")
        assertTrue pointSym instanceof Shape
        assertEquals pointSym2.color.hex, "#000000"

        def geomSym = Symbolizer.getDefault(null)
        assertTrue geomSym instanceof Composite
        assertEquals 2, geomSym.parts.size()
        assertTrue geomSym.parts[0] instanceof Composite
        assertTrue geomSym.parts[0].parts[0] instanceof Shape
        assertTrue geomSym.parts[0].parts[1] instanceof Fill
        assertTrue geomSym.parts[1] instanceof Stroke
    }

    @Test void buildString() {
        Symbolizer sym = new Symbolizer()
        assertEquals "Fill(color = #0000ff, width = 2)", sym.buildString("Fill", [color: "#0000ff", width: 2])
        sym.where("name = 'Washington'")
        assertEquals "Fill(color = #0000ff, width = 2)[ name = Washington ]", sym.buildString("Fill", [color: "#0000ff", width: 2])
    }

    @Test void createGeoToolsSymbolizer() {
        assertTrue Symbolizer.createGeoToolsSymbolizer(PointSymbolizer.class) instanceof PointSymbolizer
        assertTrue Symbolizer.createGeoToolsSymbolizer(PointSymbolizerImpl.class) instanceof PointSymbolizer
        assertTrue Symbolizer.createGeoToolsSymbolizer(LineSymbolizer.class) instanceof LineSymbolizer
        assertTrue Symbolizer.createGeoToolsSymbolizer(PolygonSymbolizer.class) instanceof PolygonSymbolizer
        assertTrue Symbolizer.createGeoToolsSymbolizer(TextSymbolizer.class) instanceof TextSymbolizer
    }

    @Test void getGeoToolsSymbolizers() {

        def rule = Symbolizer.styleFactory.createRule()
        assertEquals 1, Symbolizer.getGeoToolsSymbolizers(rule, PointSymbolizer).size()
        assertEquals 1, rule.symbolizers().size()
        assertTrue rule.symbolizers()[0] instanceof PointSymbolizer

        assertEquals 1, Symbolizer.getGeoToolsSymbolizers(rule, PointSymbolizer).size()
        assertEquals 1, rule.symbolizers().size()
        assertTrue rule.symbolizers()[0] instanceof PointSymbolizer

        assertEquals 1, Symbolizer.getGeoToolsSymbolizers(rule, TextSymbolizer).size()
        assertEquals 2, rule.symbolizers().size()
        assertTrue rule.symbolizers()[0] instanceof PointSymbolizer
        assertTrue rule.symbolizers()[1] instanceof TextSymbolizer
    }

    @Test void getStyle() {

        // Simple Symbolizer :: Fill
        Symbolizer sym = new Fill("teal")
        Style style = sym.gtStyle
        assertNotNull style
        assertEquals 1, style.featureTypeStyles().size()
        assertEquals 1, style.featureTypeStyles()[0].rules().size()
        assertEquals 0.0, style.featureTypeStyles()[0].rules()[0].minScaleDenominator, 0.1
        assertTrue Double.isInfinite(style.featureTypeStyles()[0].rules()[0].maxScaleDenominator)
        assertEquals style.featureTypeStyles()[0].rules()[0].filter, GTFilter.INCLUDE
        assertEquals 1, style.featureTypeStyles()[0].rules()[0].symbolizers().size()

        PolygonSymbolizer polygonSym = style.featureTypeStyles()[0].rules()[0].symbolizers()[0]
        assertTrue polygonSym instanceof org.geotools.styling.PolygonSymbolizer
        assertEquals "#008080", polygonSym.fill.color.value
        assertEquals 1.0, polygonSym.fill.opacity.value, 0.01

        // Simple Composite: Fill and Hatch
        sym = new Fill("teal").hatch("slash",new Stroke("navy"),6)
        style = sym.gtStyle
        assertNotNull style
        assertEquals 1, style.featureTypeStyles().size()
        assertEquals 1, style.featureTypeStyles()[0].rules().size()
        assertEquals 0.0, style.featureTypeStyles()[0].rules()[0].minScaleDenominator, 0.1
        assertTrue Double.isInfinite(style.featureTypeStyles()[0].rules()[0].maxScaleDenominator)
        assertEquals style.featureTypeStyles()[0].rules()[0].filter, GTFilter.INCLUDE
        assertEquals 1, style.featureTypeStyles()[0].rules()[0].symbolizers().size()

        polygonSym = style.featureTypeStyles()[0].rules()[0].symbolizers()[0]
        assertTrue polygonSym instanceof org.geotools.styling.PolygonSymbolizer
        assertEquals "#008080", polygonSym.fill.color.value
        assertEquals 1.0, polygonSym.fill.opacity.value, 0.01

        assertEquals "shape://slash", polygonSym.fill.graphicFill.graphicalSymbols()[0].wellKnownName.value
        assertEquals "#000080", polygonSym.fill.graphicFill.graphicalSymbols()[0].stroke.color.value
        assertEquals 1.0, polygonSym.fill.graphicFill.graphicalSymbols()[0].stroke.width.value, 0.1
        assertEquals 6, polygonSym.fill.graphicFill.size.value, 0.1

        // Simple Composite :: Fill and Stroke
        sym = new Fill("teal") + new Stroke("navy")
        style = sym.gtStyle
        assertNotNull style
        assertEquals 1, style.featureTypeStyles().size()
        assertEquals 1, style.featureTypeStyles()[0].rules().size()
        assertEquals 0.0, style.featureTypeStyles()[0].rules()[0].minScaleDenominator, 0.1
        assertTrue Double.isInfinite(style.featureTypeStyles()[0].rules()[0].maxScaleDenominator)
        assertEquals style.featureTypeStyles()[0].rules()[0].filter, GTFilter.INCLUDE
        assertEquals 2, style.featureTypeStyles()[0].rules()[0].symbolizers().size()

        polygonSym = style.featureTypeStyles()[0].rules()[0].symbolizers()[0]
        assertTrue polygonSym instanceof org.geotools.styling.PolygonSymbolizer
        assertEquals "#008080", polygonSym.fill.color.value
        assertEquals 1.0, polygonSym.fill.opacity.value, 0.01

        LineSymbolizer lineSym = style.featureTypeStyles()[0].rules()[0].symbolizers()[1]
        assertTrue lineSym instanceof org.geotools.styling.LineSymbolizer
        assertEquals "#000080", lineSym.stroke.color.value
        assertEquals 1.0, lineSym.stroke.width.value, 0.1
    }

    @Test void getStyleWithZindex() {

        Symbolizer sym = (new Fill("red") + new Stroke("blue")) + new Fill("green").zindex(1)
        Style style = sym.gtStyle

        // There should be 2 FeatureTypeStyles
        assertEquals 2, style.featureTypeStyles().size()

        // The 1st FeatureTypesStyle should have 1 rule
        assertEquals 1, style.featureTypeStyles()[0].rules().size()

        // but 2 Symbolizers
        assertEquals 2, style.featureTypeStyles()[0].rules()[0].symbolizers().size()

        // The 1st Symbolizer should be a PolygonSymbolizer
        def polygonSym = style.featureTypeStyles()[0].rules()[0].symbolizers()[0]
        assertTrue polygonSym instanceof PolygonSymbolizer
        assertEquals "#ff0000", polygonSym.fill.color.value
        assertEquals 1.0, polygonSym.fill.opacity.value, 0.01

        // The 2nd Symbolizer should be a LineSymbolizer
        def lineSym = style.featureTypeStyles()[0].rules()[0].symbolizers()[1]
        assertTrue lineSym instanceof LineSymbolizer
        assertEquals "#0000ff", lineSym.stroke.color.value
        assertEquals 1.0, lineSym.stroke.width.value, 0.1

        // The 2nd FeatureTypeStyle should have 1 rule
        assertEquals 1, style.featureTypeStyles()[1].rules().size()

        // and 1 Symbolizer
        assertEquals 1, style.featureTypeStyles()[1].rules()[0].symbolizers().size()

        // which should be a PolygonSymbolizer
        def polygonSym2 = style.featureTypeStyles()[1].rules()[0].symbolizers()[0]
        assertEquals "#008000", polygonSym2.fill.color.value
        assertEquals 1.0, polygonSym2.fill.opacity.value, 0.01
    }

    @Test void getStyleWithScale() {

        Symbolizer sym = (new Fill("red") + new Stroke("blue")).range(-1, 1000) + new Fill("green").range(1000, -1)
        Style style = sym.gtStyle

        // There should only be one FeatureTypeStyle
        assertEquals 1, style.featureTypeStyles().size()

        // but there should be two Rules
        assertEquals 2, style.featureTypeStyles()[0].rules().size()

        // Rule #1
        def rule1 = style.featureTypeStyles()[0].rules()[0]
        assertEquals 0, rule1.minScaleDenominator, 0.1
        assertEquals 1000, rule1.maxScaleDenominator, 0.1
        assertEquals 2, rule1.symbolizers().size()
        assertTrue rule1.symbolizers()[0] instanceof PolygonSymbolizer
        assertTrue rule1.symbolizers()[1] instanceof LineSymbolizer

        // Rule #2
        def rule2 = style.featureTypeStyles()[0].rules()[1]
        assertEquals 1000, rule2.minScaleDenominator, 0.1
        assertTrue Double.isInfinite(rule2.maxScaleDenominator)
        assertEquals 1, rule2.symbolizers().size()
        assertTrue rule2.symbolizers()[0] instanceof PolygonSymbolizer
    }

    @Test void getScaleWithFilter() {

        Symbolizer sym = (new Fill("red") + new Stroke("blue")).where("FOO = 'foo'") + new Fill("green").where("BAR = 'bar'")
        Style style = sym.gtStyle

        // There should only be one FeatureTypeStyle
        assertEquals 1, style.featureTypeStyles().size()

        // but there should be two Rules
        assertEquals 2, style.featureTypeStyles()[0].rules().size()

        // Rule #1
        def rule1 = style.featureTypeStyles()[0].rules()[0]
        assertEquals new Filter("FOO = 'foo'"), new Filter(rule1.filter)
        assertEquals 2, rule1.symbolizers().size()
        assertTrue rule1.symbolizers()[0] instanceof PolygonSymbolizer
        assertTrue rule1.symbolizers()[1] instanceof LineSymbolizer

        // Rule #2
        def rule2 = style.featureTypeStyles()[0].rules()[1]
        assertEquals new Filter("BAR = 'bar'"), new Filter(rule2.filter)
        assertEquals 1, rule2.symbolizers().size()
        assertTrue rule2.symbolizers()[0] instanceof PolygonSymbolizer
    }

    @Test void asSDL() {

        String NEW_LINE = System.getProperty("line.separator")

        String expectedSld = """<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:UserLayer>
    <sld:LayerFeatureConstraints>
      <sld:FeatureTypeConstraint/>
    </sld:LayerFeatureConstraints>
    <sld:UserStyle>
      <sld:Name>Default Styler</sld:Name>
      <sld:FeatureTypeStyle>
        <sld:Name>name</sld:Name>
        <sld:Rule>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">#f5deb3</sld:CssParameter>
            </sld:Fill>
          </sld:PolygonSymbolizer>
          <sld:LineSymbolizer>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#a52a2a</sld:CssParameter>
            </sld:Stroke>
          </sld:LineSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:UserLayer>
</sld:StyledLayerDescriptor>"""

        Symbolizer sym = new Fill("wheat") + new Stroke("brown")

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        sym.asSLD(out)
        String sld = out.toString().trim()
        assertNotNull sld
        assertTrue sld.length() > 0
        AssertUtil.assertStringsEqual expectedSld, sld, removeXmlNS: true, trim: true

        File file = folder.newFile("simple.sld")
        sym.asSLD(file)
        sld = file.text.trim()
        assertNotNull sld
        assertTrue sld.length() > 0
        AssertUtil.assertStringsEqual expectedSld, sld, removeXmlNS: true, trim: true

        sld = sym.sld.trim()
        assertNotNull sld
        assertTrue sld.length() > 0
        AssertUtil.assertStringsEqual expectedSld, sld, removeXmlNS: true, trim: true
    }

    @Test
    void compositeAndBlending() {
        String NEW_LINE = System.getProperty("line.separator")
        SLDWriter writer = new SLDWriter(format: false)
        // Symbolizer composite
        Symbolizer sym = new Fill("wheat").composite("source-in")
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("</sld:Fill><sld:VendorOption name=\"composite\">source-in</sld:VendorOption></sld:PolygonSymbolizer>")
        // Symbolizer composite with opacity
        sym = new Fill("wheat").composite("source-in", opacity: 0.45)
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("</sld:Fill><sld:VendorOption name=\"composite\">source-in, 0.45</sld:VendorOption></sld:PolygonSymbolizer>")
        // FeatureType composite and composite-base
        sym = new Fill("wheat").composite("source-in", base: true, symbolizer: false)
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("</sld:Rule><sld:VendorOption name=\"composite\">source-in</sld:VendorOption>" +
                "<sld:VendorOption name=\"composite-base\">true</sld:VendorOption></sld:FeatureTypeStyle>")
    }

    @Test
    void sortBy() {
        String NEW_LINE = System.getProperty("line.separator")
        SLDWriter writer = new SLDWriter(format: false)
        // Single Layer Z-Ordering
        Symbolizer sym = new Fill("wheat").sortBy(["CATEGORY A", "NAME D"])
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("<sld:VendorOption name=\"sortBy\">CATEGORY A,NAME D</sld:VendorOption>")
        sym = new Fill("wheat").sortBy([new Field("CATEGORY","String"), new Field("NAME","String")])
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("<sld:VendorOption name=\"sortBy\">CATEGORY,NAME</sld:VendorOption>")
        sym = new Fill("wheat").sortBy([[field: "CATEGORY", direction: "A"], [field: "NAME", direction: "D"]])
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("<sld:VendorOption name=\"sortBy\">CATEGORY A,NAME D</sld:VendorOption>")
        sym = new Fill("wheat").sortBy([
                [field: new Field("CATEGORY","String"), direction: "A"],
                [field: new Field("NAME","String"), direction: "D"]]
        )
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("<sld:VendorOption name=\"sortBy\">CATEGORY A,NAME D</sld:VendorOption>")
        // Cross Layer Z-Ordering
        sym = new Fill("wheat").sortBy("Group", ["CATEGORY A", "NAME D"])
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("<sld:VendorOption name=\"sortByGroup\">Group</sld:VendorOption><sld:VendorOption name=\"sortBy\">CATEGORY A,NAME D</sld:VendorOption>")
        sym = new Fill("wheat").sortBy("Group", [new Field("CATEGORY","String"), new Field("NAME","String")])
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("<sld:VendorOption name=\"sortByGroup\">Group</sld:VendorOption><sld:VendorOption name=\"sortBy\">CATEGORY,NAME</sld:VendorOption>")
        sym = new Fill("wheat").sortBy("Group", [[field: "CATEGORY", direction: "A"], [field: "NAME", direction: "D"]])
        assertTrue writer.write(sym).trim().replaceAll(NEW_LINE, "")
                .contains("<sld:VendorOption name=\"sortByGroup\">Group</sld:VendorOption><sld:VendorOption name=\"sortBy\">CATEGORY A,NAME D</sld:VendorOption>")
    }

}

