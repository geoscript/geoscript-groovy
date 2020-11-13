package geoscript.carto

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * Build a cartographic document as an Image
 * @author Jared Erickson
 */
class ImageCartoBuilder implements CartoBuilder {

    private final Java2DCartoBuilder java2DCartoBuilder

    private final BufferedImage image

    private final ImageType imageType

    /**
     * Create a ImageCartoBuilder
     * @param pageSize The PageSize
     * @param imageType The ImageType
     */
    ImageCartoBuilder(PageSize pageSize, ImageType imageType) {
        this.imageType = imageType
        this.image = new BufferedImage(pageSize.width, pageSize.height, imageType.bufferedImageType)
        Graphics2D g = image.createGraphics()
        g.renderingHints = [
                (RenderingHints.KEY_ANTIALIASING)     : RenderingHints.VALUE_ANTIALIAS_ON,
                (RenderingHints.KEY_TEXT_ANTIALIASING): RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        ]
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
        ImageIO.write(image, imageType.name, outputStream)
    }

    /**
     * Get the cartographic document as a BufferedImage
     * @return A BufferedImage
     */
    BufferedImage getImage() {
        image
    }

    enum ImageType {

        PNG ("PNG", "image/png", BufferedImage.TYPE_INT_ARGB),
        JPEG("JPEG", "image/jpeg", BufferedImage.TYPE_INT_RGB)

        String name
        String mimeType
        int bufferedImageType

        ImageType(String name, String mimeType, int imageType) {
            this.name = name
            this.mimeType = mimeType
            this.bufferedImageType = imageType
        }

    }

}
