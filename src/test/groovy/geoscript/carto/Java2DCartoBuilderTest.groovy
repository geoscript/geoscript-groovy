package geoscript.carto

import geoscript.filter.Color
import geoscript.layer.GeoTIFF
import geoscript.layer.Raster
import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.ColorMap
import geoscript.style.Fill
import geoscript.style.Shape
import geoscript.style.Stroke
import geoscript.style.UniqueValues
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.awt.Font

import static org.junit.jupiter.api.Assertions.*

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage;

class Java2DCartoBuilderTest {

    @TempDir
    private File folder

    boolean showInTarget = false

    @Test
    void drawScaleBarMiles() {
        File fileForShapefile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(fileForShapefile)
        Map map = new Map(layers: [shapefile])
        draw(new PageSize(400, 300), "scalebar_miles.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder
                .map(new MapItem(0,0,400,300).map(map))
                .scaleBar(new ScaleBarItem(10,10,200,20).map(map).units(ScaleBarItem.Units.US))
        })
    }

    @Test
    void drawScaleBarMetric() {
        File fileForShapefile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(fileForShapefile)
        Map map = new Map(layers: [shapefile])
        draw(new PageSize(400, 300), "scalebar_metric.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder
                .map(new MapItem(0,0,400,300).map(map))
                .scaleBar(new ScaleBarItem(10,10,200,20).map(map).units(ScaleBarItem.Units.METRIC))
        })
    }

    @Test
    void drawScaleBarWithOptions() {
        File fileForShapefile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(fileForShapefile)
        Map map = new Map(layers: [shapefile])
        draw(new PageSize(400, 300), "scalebar_options.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder
                .map(new MapItem(0,0,400,300).map(map))
                .scaleBar(new ScaleBarItem(10,10,200,20)
                    .map(map)
                    .units(ScaleBarItem.Units.US)
                    .barStrokeWidth(2.0f)
                    .barStrokeColor(java.awt.Color.RED)
                    .textColor(java.awt.Color.BLUE)
                )
        })
    }

    @Test
    void drawNorthArrow() {
        draw(new PageSize(80, 140), "northarrow.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0,0,pageSize.width, pageSize.height))
        })
    }

    @Test
    void drawNorthArrowWithText() {
        draw(new PageSize(80, 140), "northarrow_text.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0,0,pageSize.width, pageSize.height).drawText(true))
        })
    }

    @Test
    void drawNESWArrow() {
        draw(new PageSize(200, 200), "northarrow_nesw.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0, 0, pageSize.width, pageSize.height).style(NorthArrowStyle.NorthEastSouthWest))
        })
    }

    @Test
    void drawNESWArrowWithText() {
        draw(new PageSize(200, 200), "northarrow_nesw_text.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.northArrow(new NorthArrowItem(0, 0, pageSize.width, pageSize.height)
                .style(NorthArrowStyle.NorthEastSouthWest)
                .drawText(true)
                .font(new Font("Arial", Font.BOLD, 24))
            )
        })
    }

    @Test
    void drawLegend() {
        draw(new PageSize(150, 160), "legend.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.legend(new LegendItem(0, 0, pageSize.width, pageSize.height)
                    .title("Legend")
                    .addPointEntry("Cities", new Shape("Black", 8, "circle"))
                    .addLineEntry("Rivers", new Stroke("Blue", 2))
                    .addPolygonEntry("Parcels", new Fill("red", 0.75) + new Stroke("black",1))
            )
        })
    }

    @Test
    void drawLegendForLayer() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)
        shapefile.style = new Fill("white") + new Stroke("black", 0.1)
        draw(new PageSize(150, 150), "legend_layer_simple.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.legend(new LegendItem(0, 0, pageSize.width, pageSize.height)
                    .title("Legend")
                    .addLayer(shapefile)
            )
        })
    }

    @Test
    void drawLegendForLayerWithCategories() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)
        shapefile.style = new UniqueValues(shapefile, shapefile.schema.field("SUB_REGION"))
        draw(new PageSize(300, 800), "legend_layer_categories.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.legend(new LegendItem(0, 0, pageSize.width, pageSize.height)
                    .title("Legend")
                    .addLayer(shapefile)
            )
        })
    }

    @Test
    void drawLegendForLayerAndRaster() {
        File fileForShapefile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(fileForShapefile)
        File fileForRaster = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        Raster raster = new GeoTIFF(fileForRaster).read()
        draw(new PageSize(300, 800), "legend_layer_raster.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.legend(new LegendItem(0, 0, pageSize.width, pageSize.height)
                    .title("Legend")
                    .addLayer(shapefile)
                    .addRaster(raster)
            )
        })
    }

    @Test
    void drawLegendForMap() {
        File fileForShapefile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(fileForShapefile)
        File fileForRaster = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        Raster raster = new GeoTIFF(fileForRaster).read()
        Map map = new Map(layers: [shapefile, raster])

        draw(new PageSize(300, 800), "legend_map.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.legend(new LegendItem(0, 0, pageSize.width, pageSize.height)
                .title("Legend")
                .addMap(map)
            )
        })
    }

    @Test
    void drawLegendForRasterWithoutColorMap() {
        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        Raster raster = new GeoTIFF(file).read()
        Raster smallRaster = raster.resample(size: [50, 30])
        draw(new PageSize(300, 300), "legend_image.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.legend(new LegendItem(0, 0, pageSize.width, pageSize.height)
                .title("Legend")
                .addGroupEntry("Vector")
                .addPointEntry("Cities", new Shape("Black", 8, "circle"))
                .addLineEntry("Rivers", new Stroke("Blue", 2))
                .addPolygonEntry("Parcels", new Fill("red", 0.75) + new Stroke("black",1))
                .addGroupEntry("Raster")
                .addImageEntry("Elevation", smallRaster.bufferedImage)
            )
        })
    }

    @Test
    void drawLegendWithMultipleColumns() {

        List units = [
            ["AHa",175,0,111],
            ["AHat",192,54,22],
            ["AHcf",150,70,72],
            ["AHh",109,13,60],
            ["AHpe",232,226,82],
            ["AHt",99,0,95],
            ["AHt3",233,94,94],
            ["Aa1",255,236,207],
            ["Aa2",145,73,76],
            ["Aa3",254,212,164],
            ["Aa4",212,109,19],
            ["Aa5",175,66,28],
            ["Aam",92,87,129],
            ["Aau",129,0,0],
            ["Ach",22,0,219],
            ["Achu",79,82,246],
            ["Achp",55,177,183],
            ["Ad",186,60,0],
            ["Adc",254,190,0],
            ["Adl",247,146,29],
            ["Ae",0,198,40],
            ["Ael1",237,64,45],
            ["Ael2",255,147,135],
            ["Ael3",209,183,153],
            ["Ael4",123,135,209],
            ["Ah4",210,172,60],
            ["Ah5",169,212,254],
            ["Ah6",109,166,16],
            ["Ah7",104,142,37],
            ["Ah8",164,195,12],
            ["Am",19,163,171],
            ["Aml",0,99,66],
            ["Amm",102,192,9],
            ["Amu",121,219,224],
            ["Aoa1",239,64,46],
            ["Aoa2",254,194,194],
            ["Aoa3",168,20,20],
            ["Aoa4",201,0,219],
            ["Aop",254,172,0],
            ["Aos",234,97,50],
            ["Api",255,255,255]
        ]

        draw(new PageSize(700, 300), "legend_cols.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            LegendItem legendItem = new LegendItem(0, 0, pageSize.width, pageSize.height).title("Mars Geology")
            units.each { List values ->
                String color = "${values[1]},${values[2]},${values[3]}"
                legendItem.addPolygonEntry(values[0], new Fill(color) + new Stroke("black",1))
            }
            builder.legend(legendItem)
        })
    }

    @Test
    void drawLegendWithRasterColorMap() {
        draw(new PageSize(240, 350), "legend_rastercolormap.png", { PageSize pageSize, Java2DCartoBuilder builder ->
            builder.legend(new LegendItem(0, 0, pageSize.width, pageSize.height)
                    .title("Legend")
                    .addPointEntry("Cities", new Shape("Black", 8, "circle"))
                    .addLineEntry("Rivers", new Stroke("Blue", 2))
                    .addPolygonEntry("Parcels", new Fill("red", 0.75) + new Stroke("black",1))
                    .addColorMapEntry("Elevation", new ColorMap(0, 2300, Color.getPaletteColors("MutedTerrain", 5)))
                    .addColorMapEntry("Potential", new ColorMap(0, 2300, Color.getPaletteColors("YellowToRedHeatMap", 7)))
                    .addPointEntry("Sites", new Shape("Red", 10, "star"))
            )
        })
    }


    private void draw(PageSize pageSize, String fileName, Closure closure) {
        BufferedImage image = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_ARGB)
        Graphics2D graphics = image.createGraphics()
        graphics.renderingHints = [
                (RenderingHints.KEY_ANTIALIASING)     : RenderingHints.VALUE_ANTIALIAS_ON,
                (RenderingHints.KEY_TEXT_ANTIALIASING): RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        ]
        Java2DCartoBuilder builder = new Java2DCartoBuilder(graphics, pageSize)
        closure.call(pageSize, builder)
        File file = getTempFile(fileName)
        ImageIO.write(image, "png", file)
        assertTrue(file.exists())
        assertTrue(file.length() > 1)
    }

    private File getTempFile(String fileName) {
        new File(getDirectory(), fileName)
    }

    private File getDirectory() {
        if (showInTarget) {
            new File("target")
        } else {
            File dir = new File(folder, "carto")
            dir.mkdir()
            dir
        }
    }

}
