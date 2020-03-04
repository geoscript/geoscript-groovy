package geoscript.carto

class ImageItem extends Item {

    Object path

    ImageItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    ImageItem path(File file) {
        this.path = file
        this
    }

    @Override
    String toString() {
        "ImageItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, path = ${path})"
    }

}
