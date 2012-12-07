def cli = new CliBuilder(usage: 'geoscript-groovy geom_buffer.groovy -d')
cli.d(longOpt: 'distance', 'buffer distance', args:1)
cli.h(longOpt: 'help', 'Show usage information and quit')
def opt = cli.parse(args)
if(!opt) return
if (opt.h || !opt.d) cli.usage()
else println geoscript.geom.Geometry.fromWKT(System.in.text).buffer(opt.d as double).wkt
