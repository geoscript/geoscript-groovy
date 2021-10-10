package geoscript.layer.io

import geoscript.AssertUtil
import geoscript.layer.Pyramid
import geoscript.layer.TMS
import org.junit.jupiter.api.Test

/**
 * The GdalTmsPyramidWriter Unit Test
 * @author Jared Erickson
 */
class GdalTmsPyramidWriterTest {

    @Test
    void writePyramid() {
        Pyramid p = Pyramid.createGlobalMercatorPyramid()
        String xml = new GdalTmsPyramidWriter().write(p)
        String expected = """<GDAL_WMS>
  <Service name='TMS'>
    <ServerURL>\${z}/\${x}/\${y}.png</ServerURL>
    <SRS>EPSG:3857</SRS>
    <ImageFormat>png</ImageFormat>
  </Service>
  <DataWindow>
    <UpperLeftX>-2.0036395147881314E7</UpperLeftX>
    <UpperLeftY>2.003747120513706E7</UpperLeftY>
    <LowerRightX>2.0036395147881314E7</LowerRightX>
    <LowerRightY>-2.0037471205137067E7</LowerRightY>
    <TileLevel>19</TileLevel>
    <TileCountX>1</TileCountX>
    <TileCountY>1</TileCountY>
    <YOrigin>bottom</YOrigin>
  </DataWindow>
  <Projection>EPSG:3857</Projection>
  <BlockSizeX>256</BlockSizeX>
  <BlockSizeY>256</BlockSizeY>
  <BandsCount>3</BandsCount>
</GDAL_WMS>
"""
        AssertUtil.assertStringsEqual(expected, xml, trim: true)
    }

    @Test
    void writeTMS() {
        TMS tms = new TMS("world","png","http://tiles.org/world",Pyramid.createGlobalGeodeticPyramid())
        String xml = new GdalTmsPyramidWriter().write(tms)
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
</GDAL_WMS>
"""
        AssertUtil.assertStringsEqual(expected, xml, trim: true)
    }


}
