Geometry Commandline
--------------------
echo "POINT (1 1)" | geoscript-groovy geom_buffer.groovy -d 10 | geoscript-groovy geom_envelope.groovy

