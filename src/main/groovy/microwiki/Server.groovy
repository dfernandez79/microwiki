package microwiki

import org.eclipse.jetty.server.Server as JettyServer

import javax.servlet.http.HttpServlet
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource

class Server {
    static final int DEFAULT_PORT = 9999
    static final String DEFAULT_ENCODING = 'UTF-8'

    private final JettyServer server
    private final HttpServlet pageServlet
    private final Resource docRootResource

    Server(File docRoot, HttpServlet pageServlet, Integer port) {
        this(Resource.newResource(docRoot), pageServlet, port)
    }

    Server(Resource docRootResource, HttpServlet pageServlet, Integer port) {
        this.docRootResource = docRootResource
        this.pageServlet = pageServlet
        server = new JettyServer(port)
        initializeServerHandlers(server)
    }

    private initializeServerHandlers(org.eclipse.jetty.server.Server server) {
        def servletContextHandler = new ServletContextHandler()
        servletContextHandler.baseResource = docRootResource
        servletContextHandler.contextPath = '/'
        servletContextHandler.addServlet(new ServletHolder(pageServlet), '*.md')

        def docRootResourceHandler = new ResourceHandler(
                directoriesListed: true,
                baseResource: docRootResource)

        def fallbackResourceHandler = new ResourceHandler(
                directoriesListed: false,
                baseResource: Resource.newClassPathResource('/microwiki/static'))

        server.handler = new HandlerList(handlers: [
                servletContextHandler,
                docRootResourceHandler,
                fallbackResourceHandler,
                new DefaultHandler()])
    }

    void start() {
        server.start()
    }

    void stop() {
        server.stop()
    }

    void join() {
        server.join()
    }
}