package microwiki

import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.server.Server as JettyServer

import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

import microwiki.pages.MarkdownPage
import groovy.text.GStringTemplateEngine
import groovy.text.Template

import microwiki.pages.PageFactory
import microwiki.pages.PageTemplate
import microwiki.servlets.PageServlet

class Server {
    private final JettyServer server
    private final PageFactory pageFactory
    private final Map<String, Template> templates

    public static final Map<String, Template> DEFAULT_TEMPLATES = new HashMap<String, Template>()
    static {
        GStringTemplateEngine engine = new GStringTemplateEngine()
        DEFAULT_TEMPLATES.display = engine.createTemplate(Server.getResource('templates/display.html'))
    }

    public static void main(String[] args) {
        new Server(9999).start()
    }

    Server(Integer port) {
        this(MarkdownPage.factoryUsing('UTF-8'), DEFAULT_TEMPLATES, port)
    }

    Server(PageFactory pageFactory, Map<String, Template>templates, Integer port) {
        this.pageFactory = pageFactory
        this.templates = templates
        server = new JettyServer(port)
        initializeServerHandlers(server)
    }

    private def initializeServerHandlers(org.eclipse.jetty.server.Server server) {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.baseResource = Resource.newResource("/Users/diegof/prueba")
        servletContextHandler.contextPath = '/';
        servletContextHandler.addServlet(pageDisplayServlet(), '*.md');

        ResourceHandler resourceHandler = new ResourceHandler(
                directoriesListed: true,
                baseResource: Resource.newClassPathResource('/microwiki/static'))

        server.handler = new HandlerList(handlers: [resourceHandler, servletContextHandler, new DefaultHandler()])
    }

    private ServletHolder pageDisplayServlet() {
        return new ServletHolder(new PageServlet(pageFactory, this.displayTemplate))
    }

    private  PageTemplate getDisplayTemplate() {
        ({ page -> templates.display.make([page: page]) }) as PageTemplate
    }

    void start() {
        server.start()
    }

    void stop() {
        server.stop()
    }
}
