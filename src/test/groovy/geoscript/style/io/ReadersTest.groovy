package geoscript.style.io

import geoscript.style.io.CSSReader.CSSStyle
import geoscript.style.io.YSLDReader.YsldStyle
import geoscript.style.io.SLDReader.SLDStyle
import geoscript.style.Style
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The Style Readers Unit Test
 * @author Jared Erickson
 */
class ReadersTest {

    @Test void list() {
        List<Reader> readers = Readers.list()
        assertNotNull readers
        assertTrue readers.size() > 0
    }

    @Test void find() {
        Reader reader = Readers.find("sld")
        assertNotNull reader
        reader = Readers.find("css")
        assertNotNull reader

        reader = Readers.find("asdf")
        assertNull reader
    }

    @Test void readSimpleStyleString() {
        Style style = Readers.read("fill=#555555 fill-opacity=0.6 stroke=#555555 stroke-width=0.5")
        assertNotNull style
        assertEquals style.toString(), "Composite (Fill(color = #555555, opacity = 0.6), Stroke(color = #555555, width = 0.5))"
    }

    @Test void readColorTableStyleString() {
        Style style = Readers.read("""0  255:255:255
2  255:255:0
5  0:255:0
10 0:255:255
15 0:0:255
30 255:0:255
50 255:0:0
90 0:0:0
""")
        assertNotNull style
        assertEquals style.toString(), "ColorMap(values = [[quantity:0, color:#ffffff], [quantity:2, color:#ffff00], " +
                "[quantity:5, color:#00ff00], [quantity:10, color:#00ffff], [quantity:15, color:#0000ff], " +
                "[quantity:30, color:#ff00ff], [quantity:50, color:#ff0000], [quantity:90, color:#000000]], " +
                "type = ramp, extended = false)"
    }

    @Test void readCssString() {
        Style style = Readers.read("""
            states {
              fill: #E6E6E6;
              fill-opacity: 0.5;
              stroke: #4C4C4C;
              stroke-width: 0.1;
            }
        """)
        assertNotNull style
        assertTrue style instanceof CSSStyle
    }

    @Test void readYsldString() {
        Style style = Readers.read("""name: Default Styler
feature-styles:
- name: name
  rules:
  - scale: [min, max]
    symbolizers:
    - polygon:
        fill-color: '#F5DEB3'
    - line:
        stroke-color: '#A52A2A'
        stroke-width: 1
""")
        assertNotNull style
        assertTrue style instanceof YsldStyle
    }

    @Test void readSldString() {
        Style style = Readers.read("""<?xml version="1.0" encoding="UTF-8"?>
            <sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
                <sld:UserLayer>
                    <sld:LayerFeatureConstraints>
                        <sld:FeatureTypeConstraint/>
                    </sld:LayerFeatureConstraints>
                    <sld:UserStyle>
                        <sld:Name>Default Styler</sld:Name>
                        <sld:Title/>
                        <sld:IsDefault>true</sld:IsDefault>
                        <sld:FeatureTypeStyle>
                            <sld:Name>simple</sld:Name>
                            <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
                            <sld:SemanticTypeIdentifier>generic:geometry</sld:SemanticTypeIdentifier>
                            <sld:SemanticTypeIdentifier>simple</sld:SemanticTypeIdentifier>
                            <sld:Rule>
                                <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                                <sld:PolygonSymbolizer>
                                    <sld:Fill>
                                        <sld:CssParameter name="fill">
                                            <ogc:Literal>#E6E6E6</ogc:Literal>
                                        </sld:CssParameter>
                                        <sld:CssParameter name="fill-opacity">
                                            <ogc:Literal>0.5</ogc:Literal>
                                        </sld:CssParameter>
                                    </sld:Fill>
                                    <sld:Stroke>
                                        <sld:CssParameter name="stroke">
                                            <ogc:Literal>#4C4C4C</ogc:Literal>
                                        </sld:CssParameter>
                                        <sld:CssParameter name="stroke-linecap">
                                            <ogc:Literal>butt</ogc:Literal>
                                        </sld:CssParameter>
                                        <sld:CssParameter name="stroke-linejoin">
                                            <ogc:Literal>miter</ogc:Literal>
                                        </sld:CssParameter>
                                        <sld:CssParameter name="stroke-opacity">
                                            <ogc:Literal>1.0</ogc:Literal>
                                        </sld:CssParameter>
                                        <sld:CssParameter name="stroke-width">
                                            <ogc:Literal>0.0</ogc:Literal>
                                        </sld:CssParameter>
                                        <sld:CssParameter name="stroke-dashoffset">
                                            <ogc:Literal>0.0</ogc:Literal>
                                        </sld:CssParameter>
                                    </sld:Stroke>
                                </sld:PolygonSymbolizer>
                            </sld:Rule>
                        </sld:FeatureTypeStyle>
                    </sld:UserStyle>
                </sld:UserLayer>
            </sld:StyledLayerDescriptor>""")
        assertNotNull style
        assertTrue style instanceof SLDStyle
    }

    @Test
    void readFromNullOrEmpty() {
        assertNull(Readers.read(null as String))
        assertNull(Readers.read(""))
    }
}
