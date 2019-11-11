package geoscript.layer

import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Bounds
import geoscript.workspace.Memory
import geoscript.workspace.Workspace
import org.geotools.data.FeatureSource
import org.geotools.data.simple.SimpleFeatureSource
import org.geotools.grid.GridElement
import org.geotools.grid.GridFeatureBuilder
import org.geotools.grid.Grids
import org.geotools.grid.Lines
import org.geotools.grid.hexagon.HexagonOrientation
import org.geotools.grid.hexagon.Hexagons
import org.geotools.grid.oblong.Oblongs
import org.geotools.grid.ortholine.LineOrientation
import org.geotools.grid.ortholine.OrthoLineDef

/**
 * Create graticule or grid Layers.
 * <p><blockquote><pre>
 * Layer layer = Graticule.createHexagons(new Bounds(0,0,50,50), 5, 1, "angled")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Graticule {

    /**
     * Creates a square polygon graticule.
     * @param options The optional named parameters:
     * <ul>
     *     <li> workspace = The Workspace (defaults to Memory)</li>
     *     <li> layer = The new layer name (defaults to graticule)</li>
     *     <li> schema = The Schema (defaults to Polygon geometry and integer id attribute)</li>
     *     <li> setAttributes = A Closure that takes a GridElement and Map of attributes which you can use to set
     *     custom attributes</li>
     *     <li> createFeature = A Closure that takes a GridElement and return a boolean value whether the Feature should
     *     be created or not</li>
     * </ul>
     * @param bounds The Bounds
     * @param length The square cell width and height
     * @param spacing The vertex spacing (-1 for non densified)
     * @return A Layer
     */
    static Layer createSquares(Map options = [:], Bounds bounds, double length, double spacing) {
        SimpleFeatureSource fs = Grids.createSquareGrid(bounds.env, length, spacing, createGridFeatureBuilder("Polygon", options))
        createLayer(fs, options)
    }

    /**
     * Creates a oval polygon graticule.
     * @param options The optional named parameters:
     * <ul>
     *     <li> workspace = The Workspace (defaults to Memory)</li>
     *     <li> layer = The new layer name (defaults to graticule)</li>
     *     <li> schema = The Schema (defaults to Polygon geometry and integer id attribute)</li>
     *     <li> setAttributes = A Closure that takes a GridElement and Map of attributes which you can use to set
     *     custom attributes</li>
     *     <li> createFeature = A Closure that takes a GridElement and return a boolean value whether the Feature should
     *     be created or not</li>
     * </ul>
     * @param bounds The Bounds
     * @param length The square cell width and height
     * @return A Layer
     */
    static Layer createOvals(Map options = [:], Bounds bounds, double length) {
        SimpleFeatureSource fs = Grids.createOvalGrid(bounds.env, length, createGridFeatureBuilder("Polygon", options))
        createLayer(fs, options)
    }

    /**
     * Creates a hexagon polygon graticule.
     * @param options The optional named parameters:
     * <ul>
     *     <li> workspace = The Workspace (defaults to Memory)</li>
     *     <li> layer = The new layer name (defaults to graticule)</li>
     *     <li> schema = The Schema (defaults to Polygon geometry and integer id attribute)</li>
     *     <li> setAttributes = A Closure that takes a GridElement and Map of attributes which you can use to set
     *     custom attributes</li>
     *     <li> createFeature = A Closure that takes a GridElement and return a boolean value whether the Feature should
     *     be created or not</li>
     * </ul>
     * @param bounds The Bounds
     * @param length The hexagon cell width and height
     * @param spacing The vertex spacing (-1 for non densified)
     * @param orientation The cell orientation (flat or angled)
     * @return A Layer
     */
    static Layer createHexagons(Map options = [:], Bounds bounds, double length, double spacing, String orientation) {
        HexagonOrientation hexagonOrientation = HexagonOrientation.FLAT
        if (orientation.equalsIgnoreCase("flat")) {
            hexagonOrientation = HexagonOrientation.FLAT
        } else if (orientation.equalsIgnoreCase("angled")) {
            hexagonOrientation = HexagonOrientation.ANGLED
        }
        SimpleFeatureSource fs = Hexagons.createGrid(bounds.env, length, spacing, hexagonOrientation, createGridFeatureBuilder("Polygon", options))
        createLayer(fs, options)
    }

    /**
     * Creates a rectangular polygon graticule
     * @param options The optional named parameters:
     * <ul>
     *     <li> workspace = The Workspace (defaults to Memory)</li>
     *     <li> layer = The new layer name (defaults to graticule)</li>
     *     <li> schema = The Schema (defaults to Polygon geometry and integer id attribute)</li>
     *     <li> setAttributes = A Closure that takes a GridElement and Map of attributes which you can use to set
     *     custom attributes</li>
     *     <li> createFeature = A Closure that takes a GridElement and return a boolean value whether the Feature should
     *     be created or not</li>
     * </ul>
     * @param bounds The Bounds
     * @param width The cell width
     * @param height The cell height
     * @param spacing The vertex spacing (-1 for non densified)
     * @return A Layer
     */
    static Layer createRectangles(Map options = [:], Bounds bounds, double width, double height, double spacing) {
        FeatureSource fs = Oblongs.createGrid(bounds.env, width, height, spacing, createGridFeatureBuilder("Polygon", options))
        createLayer(fs, options)
    }

    /**
     * Creates a line based graticule
     * @param options The optional named parameters:
     * <ul>
     *     <li> workspace = The Workspace (defaults to Memory)</li>
     *     <li> layer = The new layer name (defaults to graticule)</li>
     *     <li> schema = The Schema (defaults to Polygon geometry and integer id attribute)</li>
     *     <li> setAttributes = A Closure that takes a GridElement and Map of attributes which you can use to set
     *     custom attributes</li>
     *     <li> createFeature = A Closure that takes a GridElement and return a boolean value whether the Feature should
     *     be created or not</li>
     * </ul>
     * @param bounds The Bounds
     * @param lineDefs A List of line definitions.  Each line definition is a map with the following properties:
     * orientation (vertical or horizontal), level, and spacing.
     * @param spacing The vertex spacing (-1 for non densified)
     * @return A Layer
     */
    static Layer createLines(Map options = [:], Bounds bounds, List<Map> lineDefs, double spacing) {
        List lineDefinitions = lineDefs.collect { Map values ->
            String lineOrientationStr = values.get("orientation","vertical")
            LineOrientation lineOrientation = lineOrientationStr.equalsIgnoreCase("vertical") ?
                    LineOrientation.VERTICAL : LineOrientation.HORIZONTAL
            int level = values.get("level")
            int lineSpacing = values.get("spacing")
            new OrthoLineDef(lineOrientation, level, lineSpacing)
        }
        SimpleFeatureSource fs = Lines.createOrthoLines(bounds.env, lineDefinitions, spacing, createGridFeatureBuilder("LineString", options))
        createLayer(fs, options)
    }

    private static createLayer(FeatureSource fs, Map options) {
        String layerName = options.get("layer", options.schema ? options.schema.name : "graticule")
        Workspace workspace = options.get("workspace", new Memory())
        Layer layer = workspace.create(layerName, new Schema(fs.schema).fields)
        layer.add(fs.features)
        layer
    }

    private static GridFeatureBuilder createGridFeatureBuilder(String geometryType, Map options) {
        Schema schema = options.get("schema", new Schema("grid", [
                new Field("geom", geometryType),
                new Field("id", "int")
        ]))
        Closure setAttributes = options.get("setAttributes")
        Closure createFeature = options.get("createFeature")
        new GridFeatureBuilder(schema.featureType) {
            private int id = 0
            @Override
            void setAttributes(GridElement gridElement, Map<String, Object> attributes) {
                if (setAttributes) {
                    setAttributes.call(gridElement, attributes)
                } else {
                    attributes.put("id", Integer.valueOf(++this.id))
                }
            }
            @Override
            boolean getCreateFeature(GridElement el) {
                if (createFeature) {
                    createFeature.call(el)
                } else {
                    super.getCreateFeature(el)
                }
            }
        }
    }
}
