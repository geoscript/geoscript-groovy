package geoscript.render

import javax.imageio.ImageIO
import geoscript.layer.Renderable
import geoscript.layer.Renderables
import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.assertTrue

class MapCubeTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

    @Test void renderToFile() {
        File file = folder.newFile('mapcube.png')
        List<Renderable> layers = Renderables.getRenderables([
                "layertype=raster source=${getFile('raster.tif')}",
                "layertype=layer file=${getFile('states.shp')} stroke=black stroke-width=1.0 fill=white"
        ])
        MapCube mapCube = new MapCube(title: 'GeoScript MapCube', source: 'GeoScript')
        mapCube.render(layers, file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/mapcube.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 150000)
    }

    @Test void renderToOutputStream() {
        File file = folder.newFile('mapcube.png')
        List<Renderable> layers = Renderables.getRenderables([
                "layertype=raster source=${getFile('raster.tif')}",
                "layertype=layer file=${getFile('states.shp')} stroke=black stroke-width=1.0 fill=white"
        ])
        MapCube mapCube = new MapCube(title: 'GeoScript MapCube', source: 'GeoScript')
        file.withOutputStream { OutputStream out ->
            mapCube.render(layers, out)
        }
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/mapcube.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 150000)
    }

    @Test void renderToBytes() {
        File file = folder.newFile('mapcube.png')
        List<Renderable> layers = Renderables.getRenderables([
                "layertype=raster source=${getFile('raster.tif')}",
                "layertype=layer file=${getFile('states.shp')} stroke=black stroke-width=1.0 fill=white"
        ])
        MapCube mapCube = new MapCube(title: 'GeoScript MapCube', source: 'GeoScript')
        byte[] bytes = mapCube.render(layers)
        assertTrue bytes.length > 0
        file.bytes = bytes
        File expectedFile = getFile("geoscript/render/mapcube.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 150000)
    }

}
