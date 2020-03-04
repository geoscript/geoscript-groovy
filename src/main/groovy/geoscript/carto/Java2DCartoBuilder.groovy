package geoscript.carto

import geoscript.render.Map

import javax.imageio.ImageIO
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextAttribute
import java.awt.geom.GeneralPath
import java.text.AttributedString
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class Java2DCartoBuilder implements CartoBuilder {

    protected final Graphics2D graphics

    protected final PageSize pageSize

    Java2DCartoBuilder(Graphics2D graphics, PageSize pageSize) {
        this.graphics = graphics
        this.pageSize = pageSize
    }

    @Override
    CartoBuilder dateText(DateTextItem dateTextItem) {
        if (dateTextItem.date == null) dateTextItem.date = new Date()
        graphics.color = dateTextItem.color
        graphics.font = dateTextItem.font
        def formatter = new SimpleDateFormat(dateTextItem.format)
        drawString(formatter.format(dateTextItem.date), dateTextItem.getRectangle(), dateTextItem.horizontalAlign, dateTextItem.verticalAlign)
        this
    }

    @Override
    CartoBuilder image(ImageItem imageItem) {
        def image = ImageIO.read(imageItem.path)
        graphics.drawImage(image, imageItem.x, imageItem.y, imageItem.width, imageItem.height, null)
        this
    }

    @Override
    CartoBuilder line(LineItem lineItem) {
        graphics.color = lineItem.strokeColor
        graphics.stroke = new BasicStroke(lineItem.strokeWidth)
        graphics.drawLine(lineItem.x,lineItem.y,lineItem.x+lineItem.width,lineItem.y+lineItem.height)
        this
    }

    @Override
    CartoBuilder map(MapItem mapItem) {
        mapItem.map.width = mapItem.width
        mapItem.map.height = mapItem.height
        graphics.drawImage(mapItem.map.renderToImage(), mapItem.x, mapItem.y, null)
        this
    }

    @Override
    CartoBuilder northArrow(NorthArrowItem northArrowItem) {

        int x = northArrowItem.x
        int y = northArrowItem.y
        int width = northArrowItem.width
        int height = northArrowItem.height
        int strokeWidth = northArrowItem.strokeWidth
        Color fillColor1 = northArrowItem.fillColor1
        Color fillColor2 = northArrowItem.fillColor2
        Color strokeColor1 = northArrowItem.strokeColor1
        Color strokeColor2 = northArrowItem.strokeColor2

        def path1 = new GeneralPath()
        path1.moveTo((x + width/2) as double, y)
        path1.lineTo(x, y + height)
        path1.lineTo((x + width / 2) as double, (y + height * 3/4) as double)
        path1.closePath()

        def path2 = new GeneralPath()
        path2.moveTo((x + width/2) as double, y)
        path2.lineTo(x + width, y + height)
        path2.lineTo((x + width/2) as double, (y + height * 3/4) as double)
        path2.closePath()

        graphics.stroke = new BasicStroke(strokeWidth)

        graphics.color = fillColor1
        graphics.fill(path1)
        graphics.color = strokeColor1
        graphics.draw(path1)

        graphics.color = fillColor2
        graphics.fill(path2)
        graphics.color = strokeColor2
        graphics.draw(path2)

        this
    }

    @Override
    CartoBuilder text(TextItem textItem) {
        graphics.color = textItem.color
        graphics.font = textItem.font
        drawString(textItem.text, textItem.getRectangle(), textItem.horizontalAlign, textItem.verticalAlign)
        this
    }

    @Override
    CartoBuilder paragraph(ParagraphItem paragraphItem) {

        int x = paragraphItem.x
        int y = paragraphItem.y
        int width = paragraphItem.width
        int height = paragraphItem.height
        String text = paragraphItem.text
        Font font = paragraphItem.font
        Color color = paragraphItem.color

        graphics.color = color
        graphics.font = font

        def attributedString = new AttributedString(text)
        attributedString.addAttribute(TextAttribute.FONT, font)
        def paragraph = attributedString.iterator
        int paragraphStart = paragraph.beginIndex
        int paragraphEnd = paragraph.endIndex
        def context = graphics.fontRenderContext
        def lineMeasurer = new LineBreakMeasurer(paragraph, context)
        float breakWidth = width as float
        float drawPosY = y as float
        lineMeasurer.position = paragraphStart

        while(lineMeasurer.position < paragraphEnd) {
            def layout = lineMeasurer.nextLayout(breakWidth)
            float drawPosX = layout.isLeftToRight() ? x : breakWidth - layout.advance
            drawPosY += layout.ascent
            layout.draw(graphics, drawPosX, drawPosY)
            drawPosY += layout.descent + layout.leading
        }
        this
    }

    @Override
    CartoBuilder rectangle(RectangleItem rectangleItem) {

        int x = rectangleItem.x
        int y = rectangleItem.y
        int width = rectangleItem.width
        int height = rectangleItem.height
        Color strokeColor = rectangleItem.strokeColor
        Color fillColor = rectangleItem.fillColor
        int strokeWidth = rectangleItem.strokeWidth

        if (fillColor) {
            graphics.color = fillColor
            graphics.fillRect(x, y, width, height)
        }
        graphics.color = strokeColor
        graphics.stroke = new BasicStroke(strokeWidth)
        graphics.drawRect(x,y,width,height)

        this
    }

    @Override
    CartoBuilder grid(GridItem gridItem) {
        graphics.color = gridItem.strokeColor
        graphics.stroke = new BasicStroke(gridItem.strokeWidth)
        int numberWide = pageSize.width / gridItem.size
        int numberHigh = pageSize.height / gridItem.size
        (0..numberWide).each { int c ->
            int x = gridItem.x + gridItem.size * c
            (0..numberHigh).each { int r ->
                int y = gridItem.y + gridItem.size * r
                graphics.drawRect(x,y, gridItem.size, gridItem.size)
            }
        }
        this
    }

    @Override
    CartoBuilder scaleText(ScaleTextItem scaleTextItem) {

        Font font = scaleTextItem.font
        Color color = scaleTextItem.color
        String format = scaleTextItem.format
        Map map = scaleTextItem.map

        graphics.color = color
        graphics.font = font
        def formatter = new DecimalFormat(format)
        drawString("${scaleTextItem.prefixText}1:${formatter.format(map.scaleDenominator)}", scaleTextItem.getRectangle(), scaleTextItem.horizontalAlign, scaleTextItem.verticalAlign)

        this
    }

    private void drawString(String text, Rectangle rectangle, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign) {
        FontMetrics fontMetrics = graphics.getFontMetrics(graphics.font)
        int x
        if (horizontalAlign == HorizontalAlign.LEFT) {
            x = rectangle.x
        } else if (horizontalAlign == HorizontalAlign.CENTER) {
            x = rectangle.x + (rectangle.width - fontMetrics.stringWidth(text)) / 2
        } else if (horizontalAlign == HorizontalAlign.RIGHT) {
            x = rectangle.x +  rectangle.width - fontMetrics.stringWidth(text)
        }
        int y
        if (verticalAlign == VerticalAlign.TOP) {
            y = rectangle.y + fontMetrics.height - fontMetrics.descent
        } else if (verticalAlign == VerticalAlign.MIDDLE) {
            y = rectangle.y + ((rectangle.height - fontMetrics.height) / 2) + fontMetrics.height - fontMetrics.descent
        } else if (verticalAlign == VerticalAlign.BOTTOM) {
            y = rectangle.y + rectangle.height - fontMetrics.descent
        }
        // graphics.drawRect(rectangle.x as int, rectangle.y as int, rectangle.width as int, rectangle.height as int)
        // graphics.drawRect(x, y - fontMetrics.height + fontMetrics.descent, fontMetrics.stringWidth(text), fontMetrics.height)
        graphics.drawString(text, x, y)
    }



    @Override
    void build(OutputStream outputStream) {
        build()
    }
}
