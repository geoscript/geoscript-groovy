html.html {
    head {
        title("GeoScript Web Server")
    }
    body {
        h1("GeoScript Web Server")
        ul {
            li {
                a(href:"map.groovy", "Toy WMS with OpenLayers")
            }
            li {
                a(href:"buffer.groovy?geom=POINT (111 45.7)&d=10", "Buffer")
            }
            li {
                a(href:"draw.groovy?geom=LINESTRING(0 0, 10 10)", "Draw")
            }
            li {
                a(href:"plot.groovy?geom=LINESTRING(0 0, 10 10)", "Plot")
            }
        }

    }
}
