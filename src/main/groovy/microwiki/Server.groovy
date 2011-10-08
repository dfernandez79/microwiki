package microwiki

import org.eclipse.jetty.server.Server as JettyServer

import microwiki.pages.MarkdownPage

import microwiki.pages.PageTemplate
import microwiki.pages.TemplateAdapter
import microwiki.servlets.PageServlet
import microwiki.servlets.ReadonlyPageServlet
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource
import microwiki.pages.PageProvider
import javax.servlet.http.HttpServlet
import microwiki.pages.MarkdownPageProvider

class Server {
    public static final int DEFAULT_PORT = 9999
    public static final String DEFAULT_ENCODING = 'UTF-8'

    public static final Map<String, PageTemplate> DEFAULT_TEMPLATES = [
            display: template('display.html'),
            edit: template('edit.html'),
            create: template('create.html'),
            read: template('read.html')
    ]

    private static template(String resource) {
        TemplateAdapter.using(Server.getResource("templates/$resource"))
    }

    public static void main(String[] args) {
        CliBuilder cli = commandLineOptions()

        def options = cli.parse(args)
        if (options == null || options.help) {
            cli.usage()
        } else {
            startWith(options)
        }
    }

    private static def startWith(OptionAccessor options) {
        def port = options.port ?: DEFAULT_PORT
        def docroot = options.arguments().isEmpty() ?  new File('.').canonicalPath : options.arguments().get(0)
        def encoding = options.encoding ?: DEFAULT_ENCODING

        println 'uwiki'
        println "Port: $port"
        println "Document root: $docroot"
        println "Encoding: $encoding"

        new Server(
                Resource.newResource(docroot),
                options.readonly ?: false,
                new MarkdownPageProvider(new File(docroot), encoding),
                createTemplatesFrom(options),
                port).start()
    }

    private static LinkedHashMap createTemplatesFrom(OptionAccessor options) {
        def templates = [:]
        templates.putAll(DEFAULT_TEMPLATES)
        setupTemplate(templates, options.getOptionValue('dt'), 'display', 'Display template')
        setupTemplate(templates, options.getOptionValue('et'), 'edit', 'Edit template')
        setupTemplate(templates, options.getOptionValue('ct'), 'create', 'Create template')
        setupTemplate(templates, options.getOptionValue('rt'), 'read', 'Read template')
        return templates
    }

    private static CliBuilder commandLineOptions() {
        def cli = new CliBuilder(usage: 'uwiki [options] <docroot>', header: 'Options:')
        cli.p(longOpt: 'port', args: 1, argName: 'port', "The port used to listen for HTTP request ($DEFAULT_PORT by default)")
        cli.dt(longOpt: 'display-template', args: 1, argName: 'path', 'Template used to display pages')
        cli.et(longOpt: 'edit-template', args: 1, argName: 'path', 'Template used to edit pages')
        cli.ct(longOpt: 'create-template', args: 1, argName: 'path', 'Template used to create pages')
        cli.rt(longOpt: 'read-template', args: 1, argName: 'path', 'Template used to display pages in the read only mode')
        cli.r(longOpt: 'readonly', 'Starts the server in read only mode (page edit is not allowed)')
        cli.e(longOpt: 'encoding', args: 1, argName: 'encoding', "Encoding used to read the wiki files ($DEFAULT_ENCODING by default)")
        cli._(longOpt: 'help', 'Displays this message')
        return cli
    }

    private static void setupTemplate(Map<String, PageTemplate> templates, String option, String templateName, String msg) {
        if (option != null) {
            templates.put(templateName, TemplateAdapter.using(new File(option)))
            println "$msg: $option"
        }
    }

    private final JettyServer server
    private final PageProvider pageProvider
    private final Map<String, PageTemplate> templates
    private final Resource docRoot
    private final boolean readonly

    Server(Resource docRoot, boolean readonly, PageProvider pageProvider,
           Map<String, PageTemplate> templates, Integer port) {

        this.docRoot = docRoot
        this.readonly = readonly
        this.pageProvider = pageProvider
        this.templates = templates
        server = new JettyServer(port)
        initializeServerHandlers(server)
    }

    private def initializeServerHandlers(org.eclipse.jetty.server.Server server) {
        def servletContextHandler = new ServletContextHandler();
        servletContextHandler.baseResource = docRoot
        servletContextHandler.contextPath = '/';
        servletContextHandler.addServlet(new ServletHolder(pageServlet()), '*.md');

        def docRootResourceHandler = new ResourceHandler(
                directoriesListed: true,
                baseResource: docRoot)

        def fallbackResourceHandler = new ResourceHandler(
                directoriesListed: false,
                baseResource: Resource.newClassPathResource('/microwiki/static'))

        server.handler = new HandlerList(handlers: [
                servletContextHandler,
                docRootResourceHandler,
                fallbackResourceHandler,
                new DefaultHandler()])
    }

    private HttpServlet pageServlet() {
        if (readonly) {
            return new ReadonlyPageServlet(pageProvider, templates)
        } else {
            return new PageServlet(pageProvider, templates)
        }
    }

    void start() {
        server.start()
    }

    void stop() {
        server.stop()
    }
}