package geoscript.layer

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class RenderablesTest {

    @Test void getRenderablesFromStrings() {
        String dir = new File("src/test/resources").absolutePath
        List<Renderable> renderables = Renderables.getRenderables([
          // Shapefile without style
          "layertype=layer file=${dir}/states.shp",
          // Shapefile with SLD style
          "layertype=layer file=${dir}/states.shp style=${dir}/states.sld",
          // Shapefile with CSS style
          "layertype=layer file=${dir}/states.shp style=${dir}/states.css",
          // Shapefile with YSLD style
          "layertype=layer file=${dir}/states.shp style=${dir}/states.ysld",
          // Shapefile with Simple style
          "layertype=layer file=${dir}/states.shp style=${dir}/states.txt",
          // Raster without style
          "layertype=raster source=${dir}/raster.tif",
          // Raster with style
          "layertype=raster source=${dir}/raster.tif style=${dir}/raster.sld",
          // Tile with file
          "layertype=tile file=${dir}/states.mbtiles",
          // Tile with params
          "layertype=tile type=geopackage file=${dir}/states.gpkg",
          // Layer from Workspace
          "layertype=layer dbtype=geopkg database=${dir}/states.gpkg layername=united_states style=${dir}/states.sld",
          // CSV Layer with CSS Style
          "layertype=layer file=${dir}/states.csv layername=states layerprojection=EPSG:4326 style=${dir}/states.sld",
          // CSV Layer with Simple in line Style
          "layertype=layer file=${dir}/states.csv layername=states style='fill=#555555 fill-opacity=0.6 stroke=#555555 stroke-width=0.5'"
        ])
        assertEquals(12, renderables.size())
    }

}
