package geoscript.carto

import geoscript.render.Map

class MapItem extends Item {

    Map map

    MapItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    MapItem map(Map map) {
        this.map = map
        this
    }

    @Override
    String toString() {
        "MapItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, map = ${map})"
    }

}
