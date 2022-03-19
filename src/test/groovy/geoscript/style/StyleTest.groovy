package geoscript.style

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class StyleTest {

    @Test
    void toAndFromSLD() {
        Style style = Style.fromSLD("""<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" version="1.0.0">
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
              <sld:CssParameter name="fill-opacity">0.6</sld:CssParameter>
            </sld:Fill>
          </sld:PolygonSymbolizer>
          <sld:LineSymbolizer>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#555555</sld:CssParameter>
              <sld:CssParameter name="stroke-width">1.2</sld:CssParameter>
            </sld:Stroke>
          </sld:LineSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:UserLayer>
</sld:StyledLayerDescriptor>
        """)
        assertNotNull(style)
        String sld = style.sld
        assertNotNull(sld)
        assertTrue(sld.contains("<sld:PolygonSymbolizer>"))
    }

    @Test
    void toAndFromYSLD() {
        Style style = Style.fromYSLD("""name: Default Styler
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
        assertNotNull(style)
        String ysld = style.ysld
        assertNotNull(ysld)
        assertTrue(ysld.contains("feature-styles:"))
    }

    @Test
    void fromCss() {
        Style style = Style.fromCSS("""states {
  fill: #E6E6E6;
  fill-opacity: 0.5;
  stroke: #4C4C4C;
  stroke-width: 0.1;
}
""")
        assertNotNull(style)
    }

}
