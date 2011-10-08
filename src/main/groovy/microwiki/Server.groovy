package microwiki

import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.server.Server as JettyServer

import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

import microwiki.pages.MarkdownPage

import microwiki.pages.PageFactory
import microwiki.pages.PageTemplate
import microwiki.servlets.PageServlet
import microwiki.pages.TemplateAdapter

class Server {
    private final JettyServer server
    private final PageFactory pageFactory
    private final Map<String, PageTemplate> templates
    private final Resource docRoot

    private static final int DEFAULT_PORT = 9999
    public static final Map<String, PageTemplate> DEFAULT_TEMPLATES = [
            display: template('templates/display.html'),
            edit: template('templates/edit.html')
    ]

    private static template(String resourcePath) {
        TemplateAdapter.using(Server.getResource(resourcePath))
    }
    public static void main(String[] args) {
        new Server(Resource.newResource("/Users/diegof/prueba")).start()
    }

    Server(Resource docRoot) {
        this(docRoot, DEFAULT_PORT)
    }
    Server(Resource docRoot, Integer port) {
        this(docRoot, MarkdownPage.factoryUsing('UTF-8'), DEFAULT_TEMPLATES, port)
    }

    Server(Resource docRoot, PageFactory pageFactory, Map<String, PageTemplate>templates, Integer port) {
        this.docRoot = docRoot
        this.pageFactory = pageFactory
        this.templates = templates
        server = new JettyServer(port)
        initializeServerHandlers(server)
    }

    private def initializeServerHandlers(org.eclipse.jetty.server.Server server) {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.baseResource = docRoot
        servletContextHandler.contextPath = '/';
        servletContextHandler.addServlet(wikiServlet(), '*.md');

        ResourceHandler docRootResourceHandler = new ResourceHandler(
                directoriesListed: true,
                baseResource: docRoot)

        ResourceHandler fallbackResourceHandler = new ResourceHandler(
                directoriesListed: false,
                baseResource: Resource.newClassPathResource('/microwiki/static'))

        server.handler = new HandlerList(handlers: [
                servletContextHandler,
                docRootResourceHandler,
                fallbackResourceHandler,
                new DefaultHandler()])
    }

    private ServletHolder wikiServlet() {
        return new ServletHolder(new PageServlet(pageFactory, templates.display, templates.edit))
    }

    void start() {
        server.start()
    }

    void stop() {
        server.stop()
    }
}
