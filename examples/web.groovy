import com.sun.grizzly.http.embed.GrizzlyWebServer
import com.sun.grizzly.http.servlet.ServletAdapter

@Grab(group='com.sun.grizzly', module='grizzly-servlet-webserver', version='1.9.10')
def start() {
    println("Starting web server...")
    def server = new GrizzlyWebServer(8080, "web")
    def servlet = new ServletAdapter()
    servlet.contextPath = "/geoscript"
    servlet.servletInstance = new groovy.servlet.GroovyServlet()
    server.addGrizzlyAdapter(servlet, ["/geoscript"] as String[])
    server.start()
}
start()
