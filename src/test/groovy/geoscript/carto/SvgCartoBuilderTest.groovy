package geoscript.carto

import geoscript.layer.GeoTIFF
import geoscript.layer.Shapefile
import geoscript.render.Map
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.awt.*

class SvgCartoBuilderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test
    void build() {

        Map map = new Map(layers: [
            new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        ])

        Map overViewMap = new Map(layers: [
            new GeoTIFF(new File(getClass().getClassLoader().getResource("raster.tif").toURI())).read()
        ])

        boolean saveToTarget = false
        File file = saveToTarget ? new File("target/map.svg") : temporaryFolder.newFile("map.svg")
        file.withOutputStream { OutputStream outputStream ->
            new SvgCartoBuilder(PageSize.LETTER_LANDSCAPE)
                .rectangle(new RectangleItem(0, 0, 792, 612).strokeColor(Color.WHITE).fillColor(Color.WHITE))
                .grid(new GridItem(0,0,792, 612))
                .rectangle(new RectangleItem(10, 10, 772, 592))
                .rectangle(new RectangleItem(20, 20, 752, 80))
                .text(new TextItem(30, 50, 200, 20).text("Map Title").font(new Font("Arial", Font.BOLD, 36)))
                .dateText(new DateTextItem(30, 85, 200, 10).font(new Font("Arial", Font.ITALIC, 18)))
                .scaleText(new ScaleTextItem(150, 85, 200, 10).map(map).font(new Font("Arial", Font.ITALIC, 18)))
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
                .build(outputStream)
        }
    }

}
