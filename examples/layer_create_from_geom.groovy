str = "MULTIPOINT (1 1, 2 2)"
new geoscript.workspace.Directory('.').create(new geoscript.feature.Schema("random", [['the_geom','Point','EPSG:4326']])).add(geoscript.geom.Geometry.fromWKT(str).geometries.collect{it})