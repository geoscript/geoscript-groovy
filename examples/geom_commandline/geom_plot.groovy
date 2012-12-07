def cli = new CliBuilder(usage: 'geoscript-groovy geom_plot.groovy -width 500 -height 500')
cli.f(longOpt: 'file', 'The image file', args:1)
cli.w(longOpt: 'width', 'The image width', args:1)
cli.h(longOpt: 'height', 'The image height', args: 1)
def opt = cli.parse(args)
if(!opt) return
if (!opt.w || !opt.h || !opt.f) cli.usage()
else {
    geoscript.viewer.Viewer.plotToFile(geoscript.geom.Geometry.fromWKT(System.in.text), [opt.w as int, opt.h as int], new File(opt.f))
}
