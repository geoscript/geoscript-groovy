package geoscript.layer.io

import geoscript.geom.Bounds
import geoscript.layer.Grid
import geoscript.layer.Pyramid
import geoscript.proj.Projection

/**
 * Read a Pyramid from a CSV String.
 * @author Jared Erickson
 */
class CsvPyramidReader implements PyramidReader {

    /**
     * Read a Pyramid from a CSV String.
     * @param csv A CSV String.
     * @return A Pyramid
     */
    @Override
    Pyramid read(String csv) {
        Projection proj
        Bounds bounds
        List<Grid> grids = []
        Pyramid.Origin origin
        int tileWidth
        int tileHeight
        String NEW_LINE = System.getProperty("line.separator")
        csv.split(NEW_LINE).eachWithIndex { String line, int i ->
            if (i == 0) {
                proj = new Projection(line)
            } else if (i == 1) {
                List parts = line.split(",")
                bounds = new Bounds(parts[0] as double, parts[1] as double, parts[2] as double, parts[3] as double,
                        parts.size() > 4 ? new Projection(parts[4]) : proj)
            } else if (i == 2) {
                origin = Pyramid.Origin.valueOf(line)
            } else if (i == 3) {
                List parts = line.split(",")
                tileWidth = parts[0] as int
                tileHeight = parts[1] as int
            } else if (i > 3) {
                List parts = line.split(",")
                Grid grid = new Grid(
                        parts[0] as long,
                        parts[1] as long,
                        parts[2] as long,
                        parts[3] as double,
                        parts[4] as double
                )
                grids.add(grid)
            }
        }
        new Pyramid(proj: proj, bounds: bounds, grids: grids, origin: origin, tileWidth: tileWidth, tileHeight: tileHeight)
    }
}
