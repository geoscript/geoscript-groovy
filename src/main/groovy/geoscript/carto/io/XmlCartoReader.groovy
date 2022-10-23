package geoscript.carto.io

import geoscript.carto.CartoBuilder
import geoscript.carto.CartoFactories
import geoscript.carto.DateTextItem
import geoscript.carto.GridItem
import geoscript.carto.HorizontalAlign
import geoscript.carto.ImageItem
import geoscript.carto.LegendItem
import geoscript.carto.LineItem
import geoscript.carto.MapItem
import geoscript.carto.NorthArrowItem
import geoscript.carto.NorthArrowStyle
import geoscript.carto.OverviewMapItem
import geoscript.carto.PageSize
import geoscript.carto.ParagraphItem
import geoscript.carto.RectangleItem
import geoscript.carto.ScaleBarItem
import geoscript.carto.ScaleTextItem
import geoscript.carto.TableItem
import geoscript.carto.TextItem
import geoscript.carto.VerticalAlign
import geoscript.filter.Color
import geoscript.render.io.XmlMapReader
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult

import java.awt.Font
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Read a CartoBuilder from an XML Document
 * @author Jared Erickson
 */
class XmlCartoReader implements CartoReader {

    @Override
    String getName() {
        "xml"
    }

    @Override
    CartoBuilder read(String str) {
        XmlSlurper xmlSlurper = new XmlSlurper()
        def xml = xmlSlurper.parseText(str)
        XmlMapReader mapReader = new XmlMapReader()
        Map mapItems = [:]
        CartoBuilder cartoBuilder = CartoFactories.findByName(xml.type.text())
            .create(new PageSize(getInt(xml.width.text()), getInt(xml.height.text())))
        xml.items.children().each { GPathResult item ->
            String itemType = item.type.text()
            if (itemType.equalsIgnoreCase("rectangle")) {
                RectangleItem rectangleItem = new RectangleItem(
                    getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text())
                )
                if (!item.strokeColor.isEmpty()) {
                    rectangleItem.strokeColor(getColor(item.strokeColor.text()))
                }
                if (!item.fillColor.isEmpty()) {
                    rectangleItem.fillColor(getColor(item.fillColor.text()))
                }
                if (!item.strokeWidth.isEmpty()) {
                    rectangleItem.strokeWidth(getFloat(item.strokeWidth.text()))
                }
                cartoBuilder.rectangle(rectangleItem)
            } else if (itemType.equalsIgnoreCase("dateText")) {
                DateTextItem dateTextItem = new DateTextItem(
                    getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text())
                )
                if (!item.format.isEmpty()) {
                    dateTextItem.format(item.format.text())
                }
                if (!item.date.isEmpty()) {
                    DateFormat dateFormat = new SimpleDateFormat(dateTextItem.format)
                    dateTextItem.date(dateFormat.parse(item.date.text()))
                }
                if (!item.color.isEmpty()) {
                    dateTextItem.color(getColor(item.color.text()))
                }
                if (!item.font.isEmpty()) {
                    dateTextItem.font(getFont(item.font))
                }
                if (!item.horizontalAlign.isEmpty()) {
                    dateTextItem.horizontalAlign(getHorizontalAlign(item.horizontalAlign.text()))
                }
                if (!item.verticalAlign.isEmpty()) {
                    dateTextItem.verticalAlign(getVerticalAlign(item.verticalAlign.text()))
                }
                cartoBuilder.dateText(dateTextItem)
            } else if (itemType.equalsIgnoreCase("image")) {
                ImageItem imageItem = new ImageItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                imageItem.path(new File(item.path.text()).absoluteFile)
                cartoBuilder.image(imageItem)
            } else if (itemType.equalsIgnoreCase("line")) {
                LineItem lineItem = new LineItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                if (!item.strokeColor.isEmpty()) {
                    lineItem.strokeColor(new Color(item.strokeColor.text()).asColor())
                }
                if (!item.strokeWidth.isEmpty()) {
                    lineItem.strokeWidth(getFloat(item.strokeWidth.text()))
                }
                cartoBuilder.line(lineItem)
            } else if (itemType.equalsIgnoreCase("map")) {
                MapItem mapItem = new MapItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                mapItem.map(mapReader.read(item))
                mapItems[item.name.text()] = mapItem
                cartoBuilder.map(mapItem)
            } else if (itemType.equalsIgnoreCase("overViewMap")) {
                OverviewMapItem overviewMapItem = new OverviewMapItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                overviewMapItem.overviewMap(mapReader.read(item))
                overviewMapItem.linkedMap(mapItems[item.linkedMap.text()].map)
                if (!item.zoomIntoBounds.isEmpty()) {
                    overviewMapItem.zoomIntoBounds(getBoolean(item.zoomIntoBounds.text()))
                }
                if (!item.scaleFactor.isEmpty()) {
                    overviewMapItem.scaleFactor(getDouble(item.scaleFactor.text()))
                }
                cartoBuilder.overViewMap(overviewMapItem)
            } else if (itemType.equalsIgnoreCase("northArrow")) {
                NorthArrowItem northArrowItem = new NorthArrowItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                if (!item.style.isEmpty()) {
                    northArrowItem.style(NorthArrowStyle.valueOf(item.style.text().trim()))
                }
                if (!item.fillColor1.isEmpty()) {
                    northArrowItem.fillColor1(getColor(item.fillColor1.text()))
                }
                if (!item.fillColor2.isEmpty()) {
                    northArrowItem.fillColor2(getColor(item.fillColor2.text()))
                }
                if (!item.strokeColor1.isEmpty()) {
                    northArrowItem.strokeColor1(getColor(item.strokeColor1.text()))
                }
                if (!item.strokeColor2.isEmpty()) {
                    northArrowItem.strokeColor2(getColor(item.strokeColor2.text()))
                }
                if (!item.strokeWidth.isEmpty()) {
                    northArrowItem.strokeWidth(getFloat(item.strokeWidth.text()))
                }
                if (!item.drawText.isEmpty()) {
                    northArrowItem.drawText(getBoolean(item.drawText.text()))
                }
                if (!item.font.isEmpty()) {
                    northArrowItem.font(getFont(item.font))
                }
                if (!item.textColor.isEmpty()) {
                    northArrowItem.textColor(getColor(item.textColor.text()))
                }
                cartoBuilder.northArrow(northArrowItem)
            } else if (itemType.equalsIgnoreCase("text")) {
                TextItem textItem = new TextItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                textItem.text(item.text.text())
                if (!item.color.isEmpty()) {
                    textItem.color(getColor(item.color.text()))
                }
                if (!item.font.isEmpty()) {
                    textItem.font(getFont(item.font))
                }
                if (!item.horizontalAlign.isEmpty()) {
                    textItem.horizontalAlign(getHorizontalAlign(item.horizontalAlign.text()))
                }
                if (!item.verticalAlign.isEmpty()) {
                    textItem.verticalAlign(getVerticalAlign(item.verticalAlign.text()))
                }
                cartoBuilder.text(textItem)
            } else if (itemType.equalsIgnoreCase("paragraph")) {
                ParagraphItem paragraphItem = new ParagraphItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                paragraphItem.text(item.text.text())
                if (!item.color.isEmpty()) {
                    paragraphItem.color(getColor(item.color.text()))
                }
                if (!item.font.isEmpty()) {
                    paragraphItem.font(getFont(item.font))
                }
                cartoBuilder.paragraph(paragraphItem)
            } else if (itemType.equalsIgnoreCase("scaleText")) {
                ScaleTextItem scaleTextItem = new ScaleTextItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                scaleTextItem.map(mapItems[item.map].map)
                if (!item.format.isEmpty()) {
                    scaleTextItem.format(item.format.text())
                }
                if (!item.prefixText.isEmpty()) {
                    scaleTextItem.prefixText(item.prefixText.text())
                }
                if (!item.color.isEmpty()) {
                    scaleTextItem.color(getColor(item.color.text()))
                }
                if (!item.font.isEmpty()) {
                    scaleTextItem.font(getFont(item.font))
                }
                if (!item.horizontalAlign.isEmpty()) {
                    scaleTextItem.horizontalAlign(getHorizontalAlign(item.horizontalAlign.text()))
                }
                if (!item.verticalAlign.isEmpty()) {
                    scaleTextItem.verticalAlign(getVerticalAlign(item.verticalAlign.text()))
                }
                cartoBuilder.scaleText(scaleTextItem)
            } else if (itemType.equalsIgnoreCase("scaleBar")) {
                ScaleBarItem scaleBarItem = new ScaleBarItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                scaleBarItem.map(mapItems[item.map.text()].map)
                if (!item.strokeColor.isEmpty()) {
                    scaleBarItem.strokeColor(getColor(item.strokeColor.text()))
                }
                if (!item.fillColor.isEmpty()) {
                    scaleBarItem.fillColor(getColor(item.fillColor.text()))
                }
                if (!item.barStrokeColor.isEmpty()) {
                    scaleBarItem.barStrokeColor(getColor(item.barStrokeColor.text()))
                }
                if (!item.font.isEmpty()) {
                    scaleBarItem.font(getFont(item.font))
                }
                if (!item.textColor.isEmpty()) {
                    scaleBarItem.textColor(getColor(item.textColor.text()))
                }
                if (!item.strokeWidth.isEmpty()) {
                    scaleBarItem.strokeWidth(getFloat(item.strokeWidth.text()))
                }
                if (!item.barStrokeWidth.isEmpty()) {
                    scaleBarItem.barStrokeWidth(getFloat(item.barStrokeWidth.text()))
                }
                if (!item.border.isEmpty()) {
                    scaleBarItem.border(getInt(item.border.text()))
                }
                if (!item.units.isEmpty()) {
                    scaleBarItem.units(ScaleBarItem.Units.valueOf(item.units.text().toUpperCase()))
                }
                cartoBuilder.scaleBar(scaleBarItem)
            } else if (itemType.equalsIgnoreCase("grid")) {
                GridItem gridItem = new GridItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                if (!item.size.isEmpty()) {
                    gridItem.size(getInt(item.size.text()))
                }
                if (!item.strokeWidth.isEmpty()) {
                    gridItem.strokeWidth(getFloat(item.strokeWidth.text()))
                }
                if (!item.strokeColor.isEmpty()) {
                    gridItem.strokeColor(getColor(item.strokeColor.text()))
                }
                cartoBuilder.grid(gridItem)
            } else if (itemType.equalsIgnoreCase("table")) {
                TableItem tableItem = new TableItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                tableItem.columns(item.columns.children().collect { it.text() })
                item.rows.children().each { GPathResult rowResult ->
                    Map row = [:]
                    rowResult.children().each { row[it.name()] = it.text() }
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
                LegendItem legendItem = new LegendItem(getInt(item.x.text()), getInt(item.y.text()), getInt(item.width.text()), getInt(item.height.text()))
                legendItem.addMap(mapItems[item.map.text()].map)
                if (!item.backgroundColor.isEmpty()) {
                    legendItem.backgroundColor(getColor(item.backgroundColor.text()))
                }
                if (!item.title.isEmpty()) {
                    legendItem.title(item.title.text())
                }
                if (!item.titleFont.isEmpty()) {
                    legendItem.titleFont(getFont(item.titleFont))
                }
                if (!item.titleColor.isEmpty()) {
                    legendItem.titleColor(getColor(item.titleColor.text()))
                }
                if (!item.textFont.isEmpty()) {
                    legendItem.textFont(getFont(item.textFont))
                }
                if (!item.textColor.isEmpty()) {
                    legendItem.textColor(getColor(item.textColor.text()))
                }
                if (!item.numberFormat.isEmpty()) {
                    legendItem.numberFormat(item.numberFormat.text())
                }
                if (!item.legendEntryWidth.isEmpty()) {
                    legendItem.legendEntryWidth(getInt(item.legendEntryWidth.text()))
                }
                if (!item.legendEntryHeight.isEmpty()) {
                    legendItem.legendEntryHeight(getInt(item.legendEntryHeight.text()))
                }
                if (!item.gapBetweenEntries.isEmpty()) {
                    legendItem.gapBetweenEntries(getInt(item.gapBetweenEntries.text()))
                }
                cartoBuilder.legend(legendItem)
            }

        }
        cartoBuilder
    }

    private int getInt(String str) {
        Integer.parseInt(str)
    }

    private float getFloat(String str) {
        Float.parseFloat(str)
    }

    private float getDouble(String str) {
        Double.parseDouble(str)
    }

    private boolean getBoolean(String str) {
        Boolean.parseBoolean(str)
    }

    private java.awt.Color getColor(String str) {
        new Color(str).asColor()
    }

    private Font getFont(GPathResult item) {
        String name = item.name.text()
        int style = Font.PLAIN
        if (item.style.text().equalsIgnoreCase("bold")) {
            style = Font.BOLD
        } else if (item.style.text().equalsIgnoreCase("italic")) {
            style = Font.ITALIC
        }
        int size = getInt(item.size.text())
        new Font(name, style, size)
    }

    private HorizontalAlign getHorizontalAlign(String str) {
        HorizontalAlign.valueOf(str.toUpperCase())
    }

    private VerticalAlign getVerticalAlign(String str) {
        VerticalAlign.valueOf(str.toUpperCase())
    }

    private void setRowStyle(TableItem.RowStyle rowStyle, GPathResult item) {
        if (!item.backgroundColor.isEmpty()) {
            rowStyle.backGroundColor = new Color(item.backgroundColor.text()).asColor()
        }
        if (!item.font.isEmpty()) {
            rowStyle.font = getFont(item.font)
        }
        if (!item.textColor.isEmpty()) {
            rowStyle.textColor = new Color(item.textColor.text()).asColor()
        }
        if (!item.strokeColor.isEmpty()) {
            rowStyle.strokeColor = new Color(item.strokeColor.text()).asColor()
        }
    }

}
