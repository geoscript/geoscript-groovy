Geometry Commandline
====================
Use GeoScript Groovy to perform geometry operations using unix pipes.

Buffer a Point and Calculate the Envelope
-----------------------------------------
echo "POINT (1 1)" | geoscript-groovy geom_buffer.groovy -d 10 | geoscript-groovy geom_envelope.groovy

Create an image of the previous operation
-----------------------------------------
echo "POINT (1 1)" | geoscript-groovy geom_buffer.groovy -d 10 | geoscript-groovy geom_envelope.groovy | geoscript-groovy geom_image.groovy -w 500 -h 500  -f image.png

Create a plot of the previous operation
---------------------------------------
echo "POINT (1 1)" | geoscript-groovy geom_buffer.groovy -d 10 | geoscript-groovy geom_envelope.groovy | geoscript-groovy geom_plot.groovy -w 500 -h 500  -f image.png

Calculate the centroid of a polygon
-----------------------------------
echo "POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0))" | geoscript-groovy geom_centroid.groovy

Calculate the convex hull of 3 points
-------------------------------------
echo "MULTIPOINT ((0 0), (1 1), (0 1))" | geoscript-groovy geom_convexhull.groovy | geoscript-groovy geom_plot.groovy -w 500 -h 500 -f image.png

Reproject a geometry
--------------------
echo "POINT (1147379.90 655919.74)" | geoscript-groovy geom_transform.groovy -s EPSG:2927 -t EPSG:4326 

Convert a geometry to GeoJSON
-----------------------------
echo "POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0))" | geoscript-groovy geom_centroid.groovy | geoscript-groovy geom_to_geojson.groovy

Convert geometry from GeoJSON and then perform a buffer
-------------------------------------------------------
echo '{ "type": "Point", "coordinates": [5.0, 5.0] }' | geoscript-groovy geom_from_geojson.groovy | geoscript-groovy geom_centroid.groovy
