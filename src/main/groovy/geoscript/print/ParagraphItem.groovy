package geoscript.print

import java.awt.Graphics
import java.awt.Font
import java.awt.Color
import java.text.AttributedString
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextAttribute

/**
 * The ParagraphItem can display paragraphs of text.
 * @author Jared Erickson
 */
class ParagraphItem extends Item {

    /**
     * The text
     */
    String text

    /**
     * The text Color
     */
    Color color = Color.BLACK

    /**
     * The text Font
     */
    Font font = new Font("Default", Font.PLAIN, 12)

    /**
     * Draw the ParagraphItem
     * @param g The Graphics
     */
    void draw(Graphics g) {
        g.color = color
        g.font = font

        def attributedString = new AttributedString(text)
        attributedString.addAttribute(TextAttribute.FONT, font)
        def paragraph = attributedString.iterator
        int paragraphStart = paragraph.beginIndex
        int paragraphEnd = paragraph.endIndex
        def context = g.fontRenderContext
        def lineMeasurer = new LineBreakMeasurer(paragraph, context)
        float breakWidth = width as float
        float drawPosY = y as float
        lineMeasurer.position = paragraphStart

        while(lineMeasurer.position < paragraphEnd) {
            def layout = lineMeasurer.nextLayout(breakWidth)
            float drawPosX = layout.isLeftToRight() ? x : breakWidth - layout.advance
            drawPosY += layout.ascent
            layout.draw(g, drawPosX, drawPosY)
            drawPosY += layout.descent + layout.leading
        }
    }

}

