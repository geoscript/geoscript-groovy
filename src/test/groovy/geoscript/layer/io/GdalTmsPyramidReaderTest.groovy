package geoscript.layer.io

import geoscript.geom.Bounds
import geoscript.layer.Grid
import geoscript.layer.Pyramid
import geoscript.layer.TMS
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import static geoscript.AssertUtil.assertBoundsEquals

/**
 * The GdalTmsPyramidReader Unit Test
 * @author Jared Erickson
 */
class GdalTmsPyramidReaderTest {

    @Test
    void readPyramid() {
        GdalTmsPyramidReader reader = new GdalTmsPyramidReader()
        Pyramid pyramid = reader.read("""<GDAL_WMS>
  <Service name='TMS'>
    <ServerURL>\${z}/\${x}/\${y}</ServerURL>
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
""")
        assertEquals "EPSG:3857", pyramid.proj.id
        Bounds b = new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject("EPSG:3857")
        assertBoundsEquals b, pyramid.bounds, 0.000001
        assertEquals 256, pyramid.tileWidth
        assertEquals 256, pyramid.tileHeight
        assertEquals Pyramid.Origin.BOTTOM_LEFT, pyramid.origin
        assertEquals 20, pyramid.grids.size()
        pyramid.grids.eachWithIndex { Grid g, int z ->
            assertEquals z, g.z
            int n = Math.pow(2, z)
            assertEquals n, g.width
            assertEquals n, g.height
            assertEquals 156412.0 / n, g.xResolution, 0.1
            assertEquals 156412.0 / n, g.yResolution, 0.1
        }
    }

    @Test
    void readTms() {
        GdalTmsPyramidReader reader = new GdalTmsPyramidReader()
        TMS tms = reader.readTms("""<GDAL_WMS>
  <Service name='TMS'>
    <ServerURL>http://tiles.org/world/\${z}/\${x}/\${y}</ServerURL>
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
""")
        assertEquals "tms", tms.name
        assertEquals new URL("http://tiles.org/world/"), tms.url
        assertEquals "png", tms.imageType
        Pyramid pyramid = tms.pyramid
        assertEquals "EPSG:4326", pyramid.proj.id
        Bounds b = new Bounds(-179.99, -89.99, 179.99, 89.99, "EPSG:4326")
        assertEquals b, pyramid.bounds
        assertEquals 256, pyramid.tileWidth
        assertEquals 256, pyramid.tileHeight
        assertEquals Pyramid.Origin.BOTTOM_LEFT, pyramid.origin
        assertEquals 20, pyramid.grids.size()
        pyramid.grids.eachWithIndex { Grid g, int z ->
            assertEquals z, g.z
            int w = Math.pow(2, z + 1)
            int h = Math.pow(2, z)
            double res = 0.703125 / Math.pow(2, z)
            assertEquals w, g.width
            assertEquals h, g.height
            assertEquals res, g.xResolution, 0.01
            assertEquals res, g.yResolution, 0.01
        }
    }
}
