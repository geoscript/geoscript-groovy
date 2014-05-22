/**
 * Create a Layer from the GeoCommons Washington Landfill
 * Locations.
 * http://finder.geocommons.com/overlays/6086
 */
import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*
import geoscript.workspace.*

// Get a Directory Workspace because we want to create a shapefile
File dir = new File("countries")
if (!dir.exists()) {
  dir.mkdir()
}
println dir
Workspace workspace = new Directory(dir)

// The Landfills are in lat/lon and only contain a feature ID
Schema schema = new Schema("wa_landfills", [['the_geom','Point', 'EPSG:4326'],['id','Integer']])

// Create our empty Shapefile
Layer layer = workspace.create(schema)

// Read the URL
URL url = new URL("http://finder.geocommons.com/overlays/6086.csv").eachLine("UTF-8", {line,num ->
        // Skip the first two lines
        if (num > 2) {
            // Split the line by comma
            def parts = line.split(",")
            // Create a Point
            Point p = new Point(parts[1] as Double, parts[2] as Double)
            // Add a Feature to our Layer
            layer.add(schema.feature([p,num-2]))
        }
})
