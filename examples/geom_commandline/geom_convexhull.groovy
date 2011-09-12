def cli = new CliBuilder(usage: 'geoscript-groovy geom_convexhull.groovy')
cli.h(longOpt: 'help', 'Show usage information and quit')
def opt = cli.parse(args)
if(!opt) return
if (opt.h) cli.usage()
else println geoscript.geom.Geometry.fromWKT(System.in.text).convexHull.wkt
