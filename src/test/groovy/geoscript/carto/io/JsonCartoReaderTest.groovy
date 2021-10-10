package geoscript.carto.io

import geoscript.carto.CartoBuilder
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertEquals

class JsonCartoReaderTest {

    @Test void getName() {
        CartoReader cartoReader = new JsonCartoReader()
        assertEquals("json", cartoReader.name)
    }

    @Test void readCarto() {
        String json = """{
          "type": "png",
          "width": 792, 
          "height": 612,
          "items": [
            {
                "x": 0,
                "y": 0,
                "width": 792,
                "height": 612,
                "type": "rectangle",
                "fillColor": "white",
                "strokeColor": "white"
            },
            {
                "x": 0,
                "y": 0,
                "width": 792,
                "height": 612,
                "type": "grid",
                "strokeWidth": 1,
                "strokeColor": "gray",
                "size": 10,
            },
            {
                "x": 10,
                "y": 10,
                "width": 772,
                "height": 592,
                "type": "rectangle",
                "strokeWidth": 1.0,
                "strokeColor": "black"
            },
            {
                "x": 20,
                "y": 20,
                "width": 752,
                "height": 80,
                "type": "rectangle"
            },
            {
                "x": 30,
                "y": 50,
                "width": 200,
                "height": 20,
                "type": "text",
                "text": "Map Title",
                "color": "Black",
                "horizontalAlign": "left",
                "verticalAlign": "bottom",
                "font": {
                    "name": "Arial",
                    "style": "Bold",
                    "size": 36
                }
            },
            {
                "x": 30,
                "y": 85,
                "width": 200,
                "height": 10,
                "type": "dateText",
                "color": "black",
                "format": "MM/dd/yyyy",
                "date": "12/29/2020",
                "horizontalAlign": "left",
                "verticalAlign": "bottom",
                "font": {
                    "name": "Arial",
                    "style": "Italic",
                    "size": 18
                }
            },
            {
                "x": 250,
                "y": 30,
                "width": 380,
                "height": 70,
                "type": "paragraph",
                "text": "Permission is hereby granted, free of charge, to any person obtaining a copy\nof this software and associated documentation files (the Software), to deal\nin the Software without restriction, including without limitation the rights\nto use, copy, modify, merge, publish, distribute, sublicense, and/or sell\ncopies of the Software, and to permit persons to whom the Software is\nfurnished to do so, subject to the following conditions:\nThe above copyright notice and this permission notice shall be included in\nall copies or substantial portions of the Software.",
                "color": "black",
                "font": {
                    "name": "Arial",
                    "style": "Plain",
                    "size": 8
                }
            },
            {
                "x": 710,
                "y": 30,
                "width": 1,
                "height": 60,
                "type": "line",
                "strokeColor": "black",
                "strokeWidth": 1
            },
            {
                "x": 640,
                "y": 30,
                "width": 60,
                "height": 60,
                "type": "image",
                "path": "${new File(getClass().getClassLoader().getResource("image.png").toURI()).absolutePath}"
            },
            {
                "x": 720,
                "y": 30,
                "width": 40,
                "height": 60,
                "type": "northArrow",
                "style": "North",
                "fillColor1": "black",
                "strokeColor1": "black",
                "fillColor2": "white",
                "strokeColor2": "black",
                "strokeWidth": 1,
                "textColor": "black",
                "drawText": false,
                "font": {
                    "name": "Arial",
                    "style": "Bold",
                    "size": 48
                }
            },
            {
                "x": 20,
                "y": 110,
                "width": 752,
                "height": 480,
                "type": "rectangle"
            },
            {
                "x": 30,
                "y": 120,
                "width": 742,
                "height": 470,
                "type": "map",
                "name": "mainMap",
                "layers": [
                    {"layertype": "layer", "file": "${new File("src/test/resources/states.shp").absolutePath}"}
                ]
            },
            {
                "x": 30,
                "y": 490,
                "width": 100,
                "height": 90,
                "type": "overViewMap",
                "linkedMap": "mainMap",
                "layers": [
                    {"layertype": "raster", "source": "${new File("src/test/resources/raster.tif").absolutePath}"}
                ]
            },
            {
                "x": 150,
                "y": 490,
                "width": 100,
                "height": 90,
                "type": "overViewMap",
                "linkedMap": "mainMap",
                "zoomIntoBounds": true,
                "scaleFactor": 3.0,
                "layers": [
                    {"layertype": "raster", "source": "${new File("src/test/resources/raster.tif").absolutePath}"}
                ]
            },
            {
                "x": 460,
                "y": 120,
                "width": 300,
                "height": 200,
                "type": "table",
                "columns": ["ID","Name"],
                "rows": [
                    {"ID": 1, "Name": "One"},
                    {"ID": 2, "Name": "Two"},
                    {"ID": 3, "Name": "Three"}
                ]
            },
            {
                "x": 640,
                "y": 500,
                "width": 120,
                "height": 80,
                "type": "legend",
                "map": "mainMap",
                "backgroundColor": "white",
                "title": "Legend",
                "titleFont": {
                    "name": "Arial",
                    "style": "bold",
                    "size": 18
                },
                "titleColor": "black",
                "textFont": {
                    "name": "Arial",
                    "style": "plain",
                    "size": 12
                },
                "textColor": "black",
                "legendEntryWidth": 50,
                "legendEntryHeight": 30,
                "gapBetweenEntries": 10,
                "numberFormat": "#.##"
            },
            {
                "x": 150,
                "y": 85,
                "width": 200,
                "height": 10,
                "type": "scaleText",
                "map": "mainMap",
                "color": "black",
                "horizontalAlign": "left",
                "verticalAlign": "bottom",
                "format": "#",
                "prefixText": "Scale: ",
                "font": {
                    "name": "Arial",
                    "style": "italic",
                    "size": 18
                }
            },
            {
                "x": 50,
                "y": 125,
                "width": 200,
                "height": 20,
                "type": "scaleBar",
                "map": "mainMap",
                "strokeColor": "black",
                "fillColor": "white",
                "strokeWidth": 1,
                "font": {
                    "name": "Arial",
                    "style": "plain",
                    "size": 12
                },
                "units": "METRIC",
                "border": 5
            }
          ] 
        }"""
        CartoReader cartoReader = new JsonCartoReader()
        CartoBuilder cartoBuilder = cartoReader.read(json)
        boolean saveToTarget = false
        File file = saveToTarget ? new File("target/carto_json.png"): File.createTempFile("carto_json","png")
        file.withOutputStream { OutputStream outputStream ->
            cartoBuilder.build(outputStream)
        }
    }

}
