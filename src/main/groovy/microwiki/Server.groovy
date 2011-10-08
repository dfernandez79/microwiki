package microwiki

import org.eclipse.jetty.server.Server as JettyServer

import javax.servlet.http.HttpServlet
import microwiki.pages.PageProvider
import microwiki.pages.PageTemplate
import microwiki.pages.TemplateAdapter
import microwiki.pages.markdown.MarkdownPageProvider
import microwiki.servlets.PageServlet
import microwiki.servlets.ReadonlyPageServlet
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource

class Server {
    public static final int DEFAULT_PORT = 9999
    public static final String DEFAULT_ENCODING = 'UTF-8'
    private static final CliBuilder CMD_LINE_PARSER = commandLineOptions()

    public static void main(String[] args) {
        def options = CMD_LINE_PARSER.parse(args)
        if (options == null || options.help) {
            showCommandLineHelp()
        } else {
            startWith(options)
        }
    }

    static void showCommandLineHelp() {
        CMD_LINE_PARSER.usage()
    }

    private static def startWith(OptionAccessor options) {
        def port = options.port ?: DEFAULT_PORT
        def docRoot = options.arguments().isEmpty() ? new File('.').canonicalPath : options.arguments().get(0)
        def encoding = options.encoding ?: DEFAULT_ENCODING

        println 'uwiki'
        println "Port: $port"
        println "Document root: $docRoot"
        println "Encoding: $encoding"

        new Server(
                Resource.newResource(docRoot),
                options.readonly ?: false,
                new MarkdownPageProvider(new File(docRoot), encoding),
                createTemplatesFrom(options),
                port).start()
    }

    private static Templates createTemplatesFrom(OptionAccessor options) {
        return new Templates(
                display: template(options.getOptionValue('dt'), 'Display template'),
                edit: template(options.getOptionValue('et'), 'Edit template'),
                create: template(options.getOptionValue('ct'), 'Create template'),
                read: template(options.getOptionValue('rt'), 'Read template'))
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

    private static PageTemplate template(String option, String msg) {
        if (option != null) {
            println "$msg: $option"
            return TemplateAdapter.using(new File(option))
        } else {
            return null
        }
    }

    private final JettyServer server
    private final PageProvider pageProvider
    private final Templates templates
    private final Resource docRoot
    private final boolean readonly

    Server(Resource docRoot) {
        this(docRoot, false, new MarkdownPageProvider(docRoot.file, 'UTF-8'), new Templates(), DEFAULT_PORT)
    }

    Server(Resource docRoot, boolean readonly, PageProvider pageProvider,
           Templates templates, Integer port) {

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

    PageProvider getPageProvider() {
        return pageProvider
    }

    Templates getTemplates() {
        return templates
    }
}