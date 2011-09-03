Geometry Commandline
--------------------
echo "POINT (1 1)" | geoscript-groovy geom_buffer.groovy -d 10 | geoscript-groovy geom_envelope.groovy

echo "POINT (1 1)" | geoscript-groovy geom_buffer.groovy -d 10 | geoscript-groovy geom_envelope.groovy | geoscript-groovy geom_image.groovy -w 500 -h 500  -f image.png

echo "POINT (1 1)" | geoscript-groovy geom_buffer.groovy -d 10 | geoscript-groovy geom_envelope.groovy | geoscript-groovy geom_plot.groovy -w 500 -h 500  -f image.png

echo "POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0))" | geoscript-groovy geom_centroid.groovy
