package geoscript.carto.io

import geoscript.carto.*
import geoscript.filter.Color
import geoscript.render.io.YamlMapReader
import org.yaml.snakeyaml.Yaml

import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Read a CartoBuilder from a YAML String
 * @author Jared Erickson
 */
class YamlCartoReader implements CartoReader {

    @Override
    String getName() {
        "yaml"
    }

    @Override
    CartoBuilder read(String str) {
        Yaml yaml = new Yaml()
        Map config = yaml.load(str)

        YamlMapReader mapReader = new YamlMapReader()
        Map mapItems = [:]
        CartoBuilder cartoBuilder = CartoFactories.findByName(config.type)
            .create(new PageSize(config.width, config.height))
        config.items.each { Map item ->
            String itemType = item.type
            if (itemType.equalsIgnoreCase("rectangle")) {
                RectangleItem rectangleItem = new RectangleItem(
                    item.x, item.y, item.width, item.height
                )
                if (item.strokeColor) {
                    rectangleItem.strokeColor(new Color(item.strokeColor).asColor())
                }
                if (item.fillColor) {
                    rectangleItem.fillColor(new Color(item.fillColor).asColor())
                }
                if (item.strokeWidth) {
                    rectangleItem.strokeWidth(item.strokeWidth as float)
                }
                cartoBuilder.rectangle(rectangleItem)
            } else if (itemType.equalsIgnoreCase("dateText")) {
                DateTextItem dateTextItem = new DateTextItem(
                    item.x, item.y, item.width, item.height
                )
                if (item.format) {
                    dateTextItem.format(item.format)
                }
                if (item.date) {
                    DateFormat dateFormat = new SimpleDateFormat(dateTextItem.format)
                    dateTextItem.date(dateFormat.parse(item.date))
                }
                if (item.color) {
                    dateTextItem.color(new Color(item.color).asColor())
                }
                if (item.font) {
                    dateTextItem.font(getFont(item.font))
                }
                if (item.horizontalAlign) {
                    dateTextItem.horizontalAlign(HorizontalAlign.valueOf(item.horizontalAlign.toString().toUpperCase()))
                }
                if (item.verticalAlign) {
                    dateTextItem.verticalAlign(VerticalAlign.valueOf(item.verticalAlign.toString().toUpperCase()))
                }
                cartoBuilder.dateText(dateTextItem)
            } else if (itemType.equalsIgnoreCase("image")) {
                ImageItem imageItem = new ImageItem(item.x, item.y, item.width, item.height)
                imageItem.path(new File(item.path).absoluteFile)
                cartoBuilder.image(imageItem)
            } else if (itemType.equalsIgnoreCase("line")) {
                LineItem lineItem = new LineItem(item.x, item.y, item.width, item.height)
                if (item.strokeColor) {
                    lineItem.strokeColor(new Color(item.strokeColor).asColor())
                }
                if (item.strokeWidth) {
                    lineItem.strokeWidth(item.strokeWidth as float)
                }
                cartoBuilder.line(lineItem)
            } else if (itemType.equalsIgnoreCase("map")) {
                MapItem mapItem = new MapItem(item.x, item.y, item.width, item.height)
                mapItem.map(mapReader.read(item))
                mapItems[item.name] = mapItem
                cartoBuilder.map(mapItem)
            } else if (itemType.equalsIgnoreCase("overViewMap")) {
                OverviewMapItem overviewMapItem = new OverviewMapItem(item.x, item.y, item.width, item.height)
                overviewMapItem.overviewMap(mapReader.read(item))
                overviewMapItem.linkedMap(mapItems[item.linkedMap].map)
                if (item.zoomIntoBounds) {
                    overviewMapItem.zoomIntoBounds(item.zoomIntoBounds)
                }
                if (item.scaleFactor) {
                    overviewMapItem.scaleFactor(item.scaleFactor)
                }
                cartoBuilder.overViewMap(overviewMapItem)
            } else if (itemType.equalsIgnoreCase("northArrow")) {
                NorthArrowItem northArrowItem = new NorthArrowItem(item.x, item.y, item.width, item.height)
                if (item.style) {
                    northArrowItem.style(NorthArrowStyle.valueOf(item.style.toString()))
                }
                if (item.fillColor1) {
                    northArrowItem.fillColor1(new Color(item.fillColor1).asColor())
                }
                if (item.fillColor2) {
                    northArrowItem.fillColor2(new Color(item.fillColor2).asColor())
                }
                if (item.strokeColor1) {
                    northArrowItem.strokeColor1(new Color(item.strokeColor1).asColor())
                }
                if (item.strokeColor2) {
                    northArrowItem.strokeColor2(new Color(item.strokeColor2).asColor())
                }
                if (item.strokeWidth) {
                    northArrowItem.strokeWidth(item.strokeWidth as float)
                }
                if (item.drawText != null) {
                    northArrowItem.drawText(item.drawText)
                }
                if (item.font) {
                    northArrowItem.font(getFont(item.font))
                }
                if (item.textColor) {
                    northArrowItem.textColor(new Color(item.textColor).asColor())
                }
                cartoBuilder.northArrow(northArrowItem)
            } else if (itemType.equalsIgnoreCase("text")) {
                TextItem textItem = new TextItem(item.x, item.y, item.width, item.height)
                textItem.text(item.text)
                if (item.color) {
                    textItem.color(new Color(item.color).asColor())
                }
                if (item.font) {
                    textItem.font(getFont(item.font))
                }
                if (item.horizontalAlign) {
                    textItem.horizontalAlign(HorizontalAlign.valueOf(item.horizontalAlign.toString().toUpperCase()))
                }
                if (item.verticalAlign) {
                    textItem.verticalAlign(VerticalAlign.valueOf(item.verticalAlign.toString().toUpperCase()))
                }
                cartoBuilder.text(textItem)
            } else if (itemType.equalsIgnoreCase("paragraph")) {
                ParagraphItem paragraphItem = new ParagraphItem(item.x, item.y, item.width, item.height)
                paragraphItem.text(item.text)
                if (item.color) {
                    paragraphItem.color(new Color(item.color).asColor())
                }
                if (item.font) {
                    paragraphItem.font(getFont(item.font))
                }
                cartoBuilder.paragraph(paragraphItem)
            } else if (itemType.equalsIgnoreCase("scaleText")) {
                ScaleTextItem scaleTextItem = new ScaleTextItem(item.x, item.y, item.width, item.height)
                scaleTextItem.map(mapItems[item.map].map)
                if (item.format) {
                    scaleTextItem.format(item.format)
                }
                if (item.prefixText) {
                    scaleTextItem.prefixText(item.prefixText)
                }
                if (item.color) {
                    scaleTextItem.color(new Color(item.color).asColor())
                }
                if (item.font) {
                    scaleTextItem.font(getFont(item.font))
                }
                if (item.horizontalAlign) {
                    scaleTextItem.horizontalAlign(HorizontalAlign.valueOf(item.horizontalAlign.toString().toUpperCase()))
                }
                if (item.verticalAlign) {
                    scaleTextItem.verticalAlign(VerticalAlign.valueOf(item.verticalAlign.toString().toUpperCase()))
                }
                cartoBuilder.scaleText(scaleTextItem)
            } else if (itemType.equalsIgnoreCase("scaleBar")) {
                ScaleBarItem scaleBarItem = new ScaleBarItem(item.x, item.y, item.width, item.height)
                scaleBarItem.map(mapItems[item.map].map)
                if (item.strokeColor) {
                    scaleBarItem.strokeColor(new Color(item.strokeColor).asColor())
                }
                if (item.fillColor) {
                    scaleBarItem.fillColor(new Color(item.fillColor).asColor())
                }
                if (item.barStrokeColor) {
                    scaleBarItem.barStrokeColor(new Color(item.barStrokeColor).asColor())
                }
                if (item.font) {
                    scaleBarItem.font(getFont(item.font))
                }
                if (item.textColor) {
                    scaleBarItem.textColor(new Color(item.textColor).asColor())
                }
                if (item.barStrokeWidth) {
                    scaleBarItem.barStrokeWidth(item.barStrokeWidth as float)
                }
                if (item.strokeWidth) {
                    scaleBarItem.strokeWidth(item.strokeWidth as float)
                }
                if (item.border) {
                    scaleBarItem.border(item.border as int)
                }
                if (item.units) {
                    scaleBarItem.units(ScaleBarItem.Units.valueOf(item.units.toString().toUpperCase()))
                }
                cartoBuilder.scaleBar(scaleBarItem)
            } else if (itemType.equalsIgnoreCase("grid")) {
                GridItem gridItem = new GridItem(item.x, item.y, item.width, item.height)
                if (item.size) {
                    gridItem.size(item.size as int)
                }
                if (item.strokeWidth) {
                    gridItem.strokeWidth(item.strokeWidth as float)
                }
                if (item.strokeColor) {
                    gridItem.strokeColor(new Color(item.strokeColor).asColor())
                }
                cartoBuilder.grid(gridItem)
            } else if (itemType.equalsIgnoreCase("table")) {
                TableItem tableItem = new TableItem(item.x, item.y, item.width, item.height)
                tableItem.columns(item.columns.collect { it })
                item.rows.each { Map row ->
                    tableItem.row(row)
                }
                if (item.columnRowStyle) {
                    setRowStyle(tableItem.columnRowStyle, item.columnRowStyle)
                }
                if (item.evenRowStyle) {
                    setRowStyle(tableItem.evenRowStyle, item.evenRowStyle)
                }
                if (item.oddRowStyle) {
                    setRowStyle(tableItem.oddRowStyle, item.oddRowStyle)
                }
                cartoBuilder.table(tableItem)
            } else if (itemType.equalsIgnoreCase("legend")) {
                LegendItem legendItem = new LegendItem(item.x, item.y, item.width, item.height)
                legendItem.addMap(mapItems[item.map].map)
                if (item.backgroundColor) {
                    legendItem.backgroundColor(new Color(item.backgroundColor).asColor())
                }
                if (item.title) {
                    legendItem.title(item.title)
                }
                if (item.titleFont) {
                    legendItem.titleFont(getFont(item.titleFont))
                }
                if (item.titleColor) {
                    legendItem.titleColor(new Color(item.titleColor).asColor())
                }
                if (item.textFont) {
                    legendItem.textFont(getFont(item.textFont))
                }
                if (item.textColor) {
                    legendItem.textColor(new Color(item.textColor).asColor())
                }
                if (item.numberFormat) {
                    legendItem.numberFormat(item.numberFormat)
                }
                if (item.legendEntryWidth) {
                    legendItem.legendEntryWidth(item.legendEntryWidth as int)
                }
                if (item.legendEntryHeight) {
                    legendItem.legendEntryHeight(item.legendEntryHeight as int)
                }
                if (item.gapBetweenEntries) {
                    legendItem.gapBetweenEntries(item.gapBetweenEntries as int)
                }
                cartoBuilder.legend(legendItem)
            }
        }
        cartoBuilder
    }

    private Font getFont(Map fontItem) {
        String name = fontItem.name
        int style = Font.PLAIN
        if (fontItem.style.equalsIgnoreCase("bold")) {
            style = Font.BOLD
        } else if (fontItem.style.equalsIgnoreCase("italic")) {
            style = Font.ITALIC
        }
        int size = fontItem.size.toString().toInteger()
        new Font(name, style, size)
    }

    private void setRowStyle(TableItem.RowStyle rowStyle, Map item) {
        if (item.backgroundColor) {
            rowStyle.backGroundColor = new Color(item.backgroundColor).asColor()
        }
        if (item.font) {
            rowStyle.font = getFont(item.font as Map)
        }
        if (item.textColor) {
            rowStyle.textColor = new Color(item.textColor).asColor()
        }
        if (item.strokeColor) {
            rowStyle.strokeColor = new Color(item.strokeColor).asColor()
        }
    }

}
