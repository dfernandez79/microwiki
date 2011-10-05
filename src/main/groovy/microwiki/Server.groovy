package microwiki

import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.server.Server as JettyServer

import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

import microwiki.servlets.PageDisplayServlet

class Server {
    private final JettyServer server
    private final Microwiki wiki

    Server(Microwiki wiki, Integer port) {
        server = new JettyServer(port)
        this.wiki = wiki

        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.contextPath = '/';
        servletContextHandler.addServlet(pageDisplayServlet(), '/*');

        ResourceHandler resourceHandler = new ResourceHandler(
                directoriesListed: false,
                baseResource: Resource.newClassPathResource('/microwiki/static'))

        server.handler = new HandlerList(handlers: [resourceHandler, servletContextHandler, new DefaultHandler() ])
    }

    private ServletHolder pageDisplayServlet() {
        return new ServletHolder(new PageDisplayServlet(wiki))
    }

    void start() {
        server.start()
    }

    void stop() {
        server.stop()
    }
}
