def cli = new CliBuilder(usage: 'geoscript-groovy geom_transform.groovy -s EPSG:4326 -t EPSG:2927')
cli.s(longOpt: 'source', 'The source projection', args:1)
cli.t(longOpt: 'target', 'The target projection', args:1)
cli.h(longOpt: 'help', 'Show usage information and quit')
def opt = cli.parse(args)
if(!opt) return
if (opt.h || !opt.s || !opt.t) cli.usage()
else {
   def g = geoscript.geom.Geometry.fromWKT(System.in.text)
   println geoscript.proj.Projection.transform(g, new geoscript.proj.Projection(opt.s), new geoscript.proj.Projection(opt.t)).wkt
}
