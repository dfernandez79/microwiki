package microwiki.cli

import java.awt.Desktop
import java.awt.Desktop.Action
import javax.servlet.http.HttpServlet
import microwiki.pages.PageTemplate
import microwiki.pages.TemplateAdapter
import microwiki.pages.markdown.MarkdownPageProvider
import microwiki.servlets.PageServlet
import microwiki.servlets.ReadonlyPageServlet
import org.eclipse.jetty.util.resource.Resource
import microwiki.Server
import microwiki.Templates

class Launcher {
    private static final Closure DEFAULT_DISPLAY_PAGE_ON_BROWSER_ACTION = { URI uri ->
        if (Desktop.desktopSupported && Desktop.desktop.isSupported(Action.BROWSE)) {
            Desktop.desktop.browse(uri)
        }
    }

    private static final List<String> WELCOME_FILES = ['README.md', 'index.md']

    private final String[] args
    private final Closure displayPageOnBrowserAction

    static void main(String[] args) {
        try {
            Server server = new Launcher(DEFAULT_DISPLAY_PAGE_ON_BROWSER_ACTION, args).startServer();
            server.startAndJoin()
        } catch (IllegalArgumentException e) {
            println "ERROR: $e.message"
        }
    }

    Launcher(Closure displayPageOnBrowserAction, String... args) {
        this.args = args
        this.displayPageOnBrowserAction = displayPageOnBrowserAction
    }

    Server startServer() {
        CliBuilder commandLineParser = newCommandLineParser()
        def options = commandLineParser.parse(args)
        if (options == null) {
            return null
        } else if (options.help) {
            commandLineParser.usage()
            return null
        }

        startServerWith(options)
    }

    private startServerWith(OptionAccessor options) {
        int port = options.getOptionObject('port')?.intValue() ?: Server.DEFAULT_PORT
        File docRoot = options.arguments().isEmpty() ? resolveDocRoot(currentDirectory()) : resolveDocRoot(options.arguments().get(0))
        def encoding = options.getOptionValue('encoding') ?: Server.DEFAULT_ENCODING

        println 'uwiki'
        println "Port: $port"
        println "Document root: $docRoot"
        println "Encoding: $encoding"

        def server = new Server(
                Resource.newResource(docRoot),
                createPageServlet(options.readonly ?: false, docRoot, encoding, createTemplatesFrom(options)),
                port)
        server.start()

        displayPageOnBrowser(docRoot, port)

        server
    }

    String currentDirectory() {
        new File('.').canonicalPath
    }

    File resolveDocRoot(String path) {
        File currentDir = new File(path)
        assertValidDocRoot currentDir
        File docsSubDir = new File(currentDir, 'docs')

        if (anyWelcomeFileIn(currentDir)) {
            currentDir
        } else if (docsSubDir.directory && anyWelcomeFileIn(docsSubDir)) {
            docsSubDir
        } else {
            currentDir
        }
    }

    void assertValidDocRoot(File docRoot) {
        if (!(docRoot.directory && docRoot.canRead())) {
            throw new IllegalArgumentException("$docRoot is not an existing or readable directory")
        }
    }

    boolean anyWelcomeFileIn(File currentDir) {
        return WELCOME_FILES.any { existsAndIsFile(currentDir, it) }
    }

    private displayPageOnBrowser(File docRoot, int port) {
        def found = WELCOME_FILES.find { existsAndIsFile(docRoot, it) }

        if (found != null) {
            displayPageOnBrowserAction.call(new URI("http://localhost:$port/$found"))
        }
    }

    boolean existsAndIsFile(File docRoot, String fileName) {
        def file = new File(docRoot, fileName)
        return file.exists() && file.isFile()
    }

    private HttpServlet createPageServlet(boolean readonly, File docRoot, String encoding, Templates templates) {
        def provider = new MarkdownPageProvider(docRoot, encoding)
        (readonly) ? new ReadonlyPageServlet(provider, templates) : new PageServlet(provider, templates)
    }

    private Templates createTemplatesFrom(OptionAccessor options) {
        new Templates(
                display: template(options.getOptionValue('dt'), 'Display template'),
                edit: template(options.getOptionValue('et'), 'Edit template'),
                create: template(options.getOptionValue('ct'), 'Create template'),
                read: template(options.getOptionValue('rt'), 'Read template'))
    }

    private CliBuilder newCommandLineParser() {
        def cli = new CliBuilder(usage: 'uwiki [options] <docroot>', header: 'Options:')
        cli.with {
            p(longOpt: 'port', args: 1, argName: 'port', type: Number.class, "The port used to listen for HTTP request (${Server.DEFAULT_PORT} by default)")
            dt(longOpt: 'display-template', args: 1, argName: 'path', 'Template used to display pages')
            et(longOpt: 'edit-template', args: 1, argName: 'path', 'Template used to edit pages')
            ct(longOpt: 'create-template', args: 1, argName: 'path', 'Template used to create pages')
            rt(longOpt: 'read-template', args: 1, argName: 'path', 'Template used to display pages in the read only mode')
            r(longOpt: 'readonly', 'Starts the server in read only mode (page edit is not allowed)')
            e(longOpt: 'encoding', args: 1, argName: 'encoding', "Encoding used to read the wiki files (${Server.DEFAULT_ENCODING} by default)")
            _(longOpt: 'help', 'Displays this message')
        }
        cli
    }

    private PageTemplate template(String option, String msg) {
        if (option != null) {
            println "$msg: $option"
            return TemplateAdapter.using(new File(option))
        } else {
            return null
        }
    }
}