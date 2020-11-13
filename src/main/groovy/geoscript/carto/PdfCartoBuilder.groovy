package geoscript.carto

import com.lowagie.text.Document
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfWriter
import org.apache.commons.io.IOUtils

import java.awt.Graphics2D

/**
 * Build a cartographic document as a PDF document
 * @author Jared Erickson
 */
class PdfCartoBuilder implements CartoBuilder {

    private final Document document

    private final Java2DCartoBuilder java2DCartoBuilder

    private final ByteArrayOutputStream outputStream

    /**
     * Create a PdfCartoBuilder with a landscape letter page size
     */
    PdfCartoBuilder() {
        this(PageSize.LETTER_LANDSCAPE)
    }

    /**
     * Create a PdfCartoBuilder with the given PageSize
     * @param pageSize The PageSize
     */
    PdfCartoBuilder(PageSize pageSize) {
        this.document = new Document(new Rectangle(pageSize.width as float, pageSize.height as float))
        this.outputStream = new ByteArrayOutputStream()
        PdfWriter writer = PdfWriter.getInstance(document, outputStream)
        document.open()
        PdfContentByte cb = writer.getDirectContent()
        Graphics2D g2 = cb.createGraphicsShapes(document.pageSize.width, document.pageSize.height)
        this.java2DCartoBuilder = new Java2DCartoBuilder(g2, pageSize)
    }

    @Override
    CartoBuilder dateText(DateTextItem dateTextItem) {
        java2DCartoBuilder.dateText(dateTextItem)
        this
    }

    @Override
    CartoBuilder image(ImageItem imageItem) {
        java2DCartoBuilder.image(imageItem)
        this
    }

    @Override
    CartoBuilder line(LineItem lineItem) {
        java2DCartoBuilder.line(lineItem)
        this
    }

    @Override
    CartoBuilder map(MapItem mapItem) {
        java2DCartoBuilder.map(mapItem)
        this
    }

    @Override
    CartoBuilder northArrow(NorthArrowItem northArrowItem) {
        java2DCartoBuilder.northArrow(northArrowItem)
        this
    }

    @Override
    CartoBuilder text(TextItem textItem) {
        java2DCartoBuilder.text(textItem)
        this
    }

    @Override
    CartoBuilder paragraph(ParagraphItem paragraphItem) {
        java2DCartoBuilder.paragraph(paragraphItem)
        this
    }

    @Override
    CartoBuilder rectangle(RectangleItem rectangleItem) {
        java2DCartoBuilder.rectangle(rectangleItem)
        this
    }

    @Override
    CartoBuilder scaleText(ScaleTextItem scaleTextItem) {
        java2DCartoBuilder.scaleText(scaleTextItem)
        this
    }

    @Override
    CartoBuilder scaleBar(ScaleBarItem scaleBarItem) {
        java2DCartoBuilder.scaleBar(scaleBarItem)
        this
    }

    @Override
    CartoBuilder grid(GridItem gridItem) {
        java2DCartoBuilder.grid(gridItem)
        this
    }

    @Override
    CartoBuilder overViewMap(OverviewMapItem overviewMapItem) {
        java2DCartoBuilder.overViewMap(overviewMapItem)
        this
    }

    @Override
    CartoBuilder table(TableItem tableItem) {
        java2DCartoBuilder.table(tableItem)
        this
    }

    @Override
    CartoBuilder legend(LegendItem legendItem) {
        java2DCartoBuilder.legend(legendItem)
        this
    }

    @Override
    void build(OutputStream outputStream) {
        java2DCartoBuilder.graphics.dispose()
        document.close()
        this.outputStream.writeTo(outputStream)
    }

}
