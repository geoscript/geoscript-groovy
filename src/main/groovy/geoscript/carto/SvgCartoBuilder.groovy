package geoscript.carto

import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document

/**
 * Build a cartographic document as a SVG document
 * @author Jared Erickson
 */
class SvgCartoBuilder implements CartoBuilder {

    private final SVGGraphics2D g

    private final Java2DCartoBuilder java2DCartoBuilder

    /**
     * Create a SvgCartoBuilder with a landscape letter page size
     */
    SvgCartoBuilder() {
        this(PageSize.LETTER_LANDSCAPE)
    }

    /**
     * Create a SvgCartoBilder with the given PageSize
     * @param pageSize The PageSize
     */
    SvgCartoBuilder(PageSize pageSize) {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation()
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
        Document doc = impl.createDocument(svgNS, "svg", null)
        this.g = new SVGGraphics2D(doc)
        this.java2DCartoBuilder = new Java2DCartoBuilder(g, pageSize)
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
        g.stream(new OutputStreamWriter(outputStream))
        java2DCartoBuilder.graphics.dispose()
    }

}
