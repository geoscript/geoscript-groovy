package geoscript.style.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.style.Style

/**
 * The SLDReader UnitTest
 * @author Jared Erickson
 */
class SLDReaderTestCase {

    @Test void readFromFile() {

        File file = new File(getClass().getClassLoader().getResource("states.sld").toURI())
        assertNotNull(file)

        SLDReader reader = new SLDReader()
        Style style = reader.read(file)
        assertNotNull(style)
        assertNotNull(style.style)
    }

    @Test void readFromInputStream() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("states.sld")
        assertNotNull(inputStream)

        SLDReader reader = new SLDReader()
        Style style = reader.read(inputStream)
        assertNotNull(style)
        assertNotNull(style.style)
    }

    @Test void readFromString() {

        String sld = """<?xml version="1.0" encoding="UTF-8"?>
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
            </sld:StyledLayerDescriptor>
        """

        SLDReader reader = new SLDReader()
        Style style = reader.read(sld)
        assertNotNull(style)
        assertNotNull(style.style)
    }

}
