package geoscript.layer.io

import geoscript.AssertUtil
import geoscript.layer.Pyramid
import geoscript.layer.TMS
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertTrue
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The GdalTmsPyramidWriter Unit Test
 * @author Jared Erickson
 */
class GdalTmsPyramidWriterTest {

    @Test
    void writePyramid() {
        Pyramid p = Pyramid.createGlobalMercatorPyramid()
        String actual = new GdalTmsPyramidWriter().write(p)
        String expected = """<GDAL_WMS>
  <Service name='TMS'>
    <ServerURL>\${z}/\${x}/\${y}.png</ServerURL>
    <SRS>EPSG:3857</SRS>
    <ImageFormat>png</ImageFormat>
  </Service>
  <DataWindow>
    <UpperLeftX>-20036395.14788131</UpperLeftX>
    <UpperLeftY>20037471.20513706</UpperLeftY>
    <LowerRightX>20036395.14788131</LowerRightX>
    <LowerRightY>-20037471.205137067</LowerRightY>
    <TileLevel>19</TileLevel>
    <TileCountX>1</TileCountX>
    <TileCountY>1</TileCountY>
    <YOrigin>bottom</YOrigin>
  </DataWindow>
  <Projection>EPSG:3857</Projection>
  <BlockSizeX>256</BlockSizeX>
  <BlockSizeY>256</BlockSizeY>
  <BandsCount>3</BandsCount>
</GDAL_WMS>"""
        assertTrue(actual.startsWith(expected.substring(0, expected.indexOf("<UpperLeftX>"))))
        assertTrue(actual.endsWith(expected.substring(expected.indexOf("</LowerRightY>"))))
    }

    @Test
    void writeTMS() {
        TMS tms = new TMS("world","png","http://tiles.org/world",Pyramid.createGlobalGeodeticPyramid())
        String actual = new GdalTmsPyramidWriter().write(tms)
        String expected = """<GDAL_WMS>
  <Service name='TMS'>
    <ServerURL>http://tiles.org/world/\${z}/\${x}/\${y}.png</ServerURL>
    <SRS>EPSG:4326</SRS>
    <ImageFormat>png</ImageFormat>
  </Service>
  <DataWindow>
    <UpperLeftX>-179.99</UpperLeftX>
    <UpperLeftY>89.99</UpperLeftY>
    <LowerRightX>179.99</LowerRightX>
    <LowerRightY>-89.99</LowerRightY>
    <TileLevel>19</TileLevel>
    <TileCountX>2</TileCountX>
    <TileCountY>1</TileCountY>
    <YOrigin>bottom</YOrigin>
  </DataWindow>
  <Projection>EPSG:4326</Projection>
  <BlockSizeX>256</BlockSizeX>
  <BlockSizeY>256</BlockSizeY>
  <BandsCount>3</BandsCount>
</GDAL_WMS>"""
        assertTrue(actual.startsWith(expected.substring(0, expected.indexOf("<UpperLeftX>"))))
        assertTrue(actual.endsWith(expected.substring(expected.indexOf("</LowerRightY>"))))
    }


}
