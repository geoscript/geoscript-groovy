package geoscript.carto

import geoscript.layer.GeoTIFF
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.UniqueValues
import org.junit.jupiter.api.io.TempDir

import java.awt.Color
import org.junit.jupiter.api.Test

import java.awt.Font

class ImageCartoBuilderTest {

    @TempDir
    private File folder

    @Test
    void build() {

        Map map = new Map(layers: [
            new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        ])

        Map overViewMap = new Map(layers: [
            new GeoTIFF(new File(getClass().getClassLoader().getResource("raster.tif").toURI())).read()
        ])

        boolean saveToTarget = false
        File file = saveToTarget ? new File("target/map.png") : new File(folder, "map.png")
        file.withOutputStream { OutputStream outputStream ->
            new ImageCartoBuilder(PageSize.LETTER_LANDSCAPE, ImageCartoBuilder.ImageType.PNG)
                .rectangle(new RectangleItem(0, 0, 792, 612).strokeColor(Color.WHITE).fillColor(Color.WHITE))
                .grid(new GridItem(0,0,792, 612))
                .rectangle(new RectangleItem(10, 10, 772, 592))
                .rectangle(new RectangleItem(20, 20, 752, 80))
                .text(new TextItem(30, 50, 200, 20).text("Map Title").font(new Font("Arial", Font.BOLD, 36)))
                .dateText(new DateTextItem(30, 85, 200, 10).font(new Font("Arial", Font.ITALIC, 18)))
                .paragraph(new ParagraphItem(250, 30, 380, 70).text("""Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
""").font(new Font("Arial", Font.PLAIN, 8)))
                .line(new LineItem(710, 30, 1, 60))
                .image(new ImageItem(640, 30, 60, 60).path(new File(getClass().getClassLoader().getResource("image.png").toURI())))
                .northArrow(new NorthArrowItem(720, 30, 40, 60))
                .rectangle(new RectangleItem(20, 110, 752, 480))
                .map(new MapItem(30, 120, 742, 470).map(map))
                .overViewMap(new OverviewMapItem(30, 490, 100, 90).overviewMap(overViewMap).linkedMap(map))
                .overViewMap(new OverviewMapItem(150, 490, 100, 90).overviewMap(overViewMap).linkedMap(map).zoomIntoBounds(true).scaleFactor(3.0))
                .table(new TableItem(460, 120, 300, 200)
                    .columns(["ID","Name"])
                    .row([[ID: 1, Name: "One"]])
                    .row([[ID: 2, Name: "Two"]])
                    .row([[ID: 3, Name: "Three"]])
                )
                .legend(new LegendItem(640, 500, 120,80).addMap(map))
                .scaleText(new ScaleTextItem(150, 85, 200, 10).map(map).font(new Font("Arial", Font.ITALIC, 18)))
                .scaleBar(new ScaleBarItem(50, 125, 200, 20).map(map))
                .build(outputStream)
        }
    }

    @Test
    void buildAnother() {

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        layer.style = new UniqueValues(layer, "SUB_REGION", "Pastel1")

        Map map = new Map(layers: [
            layer
        ])

        boolean saveToTarget = false
        File file = saveToTarget ? new File("target/carto_map.png") : new File(folder, "carto_map.png")
        file.withOutputStream { OutputStream outputStream ->
            new ImageCartoBuilder(PageSize.LETTER_LANDSCAPE, ImageCartoBuilder.ImageType.PNG)
                    // Entire Canvas
                    .rectangle(new RectangleItem(0, 0, 792, 612).strokeColor(Color.WHITE).fillColor(Color.WHITE))
                    // Outer border
                    .rectangle(new RectangleItem(5, 5, 782, 602).strokeColor(Color.BLACK).strokeWidth(3f))
                    // Title border
                    .rectangle(new RectangleItem(10, 10, 772, 80).strokeColor(Color.BLACK).strokeWidth(1f))
                    // Map border
                    .rectangle(new RectangleItem(10, 95, 600, 490).strokeColor(Color.BLACK).strokeWidth(1f))
                    // Legend border
                    .rectangle(new RectangleItem(615, 95, 167, 300).strokeColor(Color.BLACK).strokeWidth(1f))
                    // Scale border
                    .rectangle(new RectangleItem(615, 400, 167, 50).strokeColor(Color.BLACK).strokeWidth(1f))
                    // Overview border
                    .rectangle(new RectangleItem(615, 455, 167, 80).strokeColor(Color.BLACK).strokeWidth(1f))
                    // Logo border
                    .rectangle(new RectangleItem(615, 540, 167, 62).strokeColor(Color.BLACK).strokeWidth(1f))
                    // Title
                    .text(new TextItem(15, 15, 700, 60).text("United States").verticalAlign(VerticalAlign.TOP).font(new Font("Arial", Font.BOLD, 36)))
                    // Map
                    .map(new MapItem(15, 100, 590, 480).map(map))
                    // Legend
                    .legend(new LegendItem(620, 100, 157, 290).addMap(map).legendEntryHeight(15).legendEntryWidth(20))
                    // Disclaimer
                    .paragraph(new ParagraphItem(15, 587,590, 30).font(new Font("Arial", Font.ITALIC, 8))
                            .text("The map features are approximate and are intended only to provide an indication of said feature.  " +
                                    "Additional areas that have not been mapping may by present.  This is not a survey."))
                    // Scale bar
                    .scaleBar(new ScaleBarItem(615, 410, 145, 30).map(map).strokeColor(Color.WHITE))
                    // Overview
                    .overViewMap(new OverviewMapItem(620, 460, 157, 70).linkedMap(map).overviewMap(map))
                    // North arrow
                    .northArrow(new NorthArrowItem(755, 465, 20, 20))
                    // Date text
                    .dateText(new DateTextItem(620, 550, 167, 10).font(new Font("Arial", Font.PLAIN, 12)))
                    // Logo
                    .image(new ImageItem(620, 565, 30, 30).path(new File("src/test/resources/image.png")))
                    .text(new TextItem(655, 565, 100, 30).text("GeoScript").font(new Font("Arial", Font.BOLD, 24)))
                    .build(outputStream)
        }
    }

}
