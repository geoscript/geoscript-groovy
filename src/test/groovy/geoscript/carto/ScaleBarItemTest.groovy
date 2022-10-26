package geoscript.carto

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.render.Map
import org.junit.jupiter.api.Test

import java.awt.*

import static org.junit.jupiter.api.Assertions.assertEquals

class ScaleBarItemTest extends AbstractCartoTest {

    @Test
    void create() {

        Map map = new Map(layers: [
            new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        ])

        ScaleBarItem item = new ScaleBarItem(10,20,300,50)
            .font(new Font("Verdana", Font.BOLD, 14))
            .textColor(Color.BLACK)
            .strokeColor(Color.BLUE)
            .fillColor(Color.GRAY)
            .strokeWidth(1.45f)
            .barStrokeColor(Color.BLUE)
            .barStrokeWidth(1.2f)
            .border(6)
            .units(ScaleBarItem.Units.US)
            .map(map)

        assertEquals(map, item.map)
        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(50, item.height)
        assertEquals(item.font.name, "Verdana")
        assertEquals(item.font.style, Font.BOLD)
        assertEquals(item.font.size, 14)
        assertEquals(item.textColor, Color.BLACK)
        assertEquals(item.strokeColor, Color.BLUE)
        assertEquals(item.fillColor, Color.GRAY)
        assertEquals(item.barStrokeColor, Color.BLUE)
        assertEquals(item.barStrokeWidth, 1.2f)
        assertEquals(item.border, 6)
        assertEquals(item.strokeWidth, 1.45f, 0.01f)
        assertEquals(ScaleBarItem.Units.US, item.units)
    }

    @Test void calculateScaleBarInfoMeters() {

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        Map map = new Map(
            bounds: layer.bounds,
            proj: layer.proj,
            width: 400,
            height: 400,
            layers: [ layer ]
        )
        ScaleBarItem item = new ScaleBarItem(10,20,300,50)
                .units(ScaleBarItem.Units.METRIC)
                .map(map)
        ScaleBarItem.ScaleBarInfo info = item.calculateScaleBarInfo()
        assertEquals(311.0, info.widthInPixels, 0.01)
        assertEquals(5000, info.widthInUnits, 0.01)
        assertEquals("km", info.unitForScaleText)
    }

    @Test void calculateScaleBarInfoMiles() {

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        Map map = new Map(
                bounds: layer.bounds,
                proj: layer.proj,
                width: 400,
                height: 400,
                layers: [ layer ]
        )
        ScaleBarItem item = new ScaleBarItem(10,20,300,50)
                .units(ScaleBarItem.Units.US)
                .map(map)
        ScaleBarItem.ScaleBarInfo info = item.calculateScaleBarInfo()
        assertEquals(300.0, info.widthInPixels, 0.01)
        assertEquals(3000.0, info.widthInUnits, 0.01)
        assertEquals("miles", info.unitForScaleText)
    }

    @Test
    void draw() {
        draw("scalebar", 400, 300, { CartoBuilder cartoBuilder ->
            Map map = new Map(layers: [
                    new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
            ])
            cartoBuilder.map(new MapItem(10,10,380,280).map(map))
            cartoBuilder.scaleBar(new ScaleBarItem(30,250,330,20)
                    .units(ScaleBarItem.Units.US)
                    .map(map)
            )
        })
    }

}
