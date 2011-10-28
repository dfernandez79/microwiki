package microwiki

import org.eclipse.jetty.server.Server as JettyServer

import javax.servlet.http.HttpServlet
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource

import microwiki.config.Config
import microwiki.servlets.PageServlet
import microwiki.servlets.ReadonlyPageServlet
import microwiki.pages.markdown.MarkdownPageProvider
import microwiki.servlets.SearchServlet
import microwiki.search.lucene.LuceneSearchStrategy
import microwiki.search.NullPageSearchStrategy
import microwiki.search.PageSearchStrategy
import org.apache.commons.vfs2.VFS
import org.apache.commons.vfs2.impl.DefaultFileMonitor

class Server {
    static final int DEFAULT_PORT = 9999
    static final String DEFAULT_ENCODING = 'UTF-8'

    final File docRoot
    final Config config
    private final JettyServer server
    private DefaultFileMonitor fileChangeMonitor

    Server(File docRoot, Config config) {
        this.docRoot = docRoot
        this.config = config

        server = new JettyServer(config.server.port)
        initializeServerHandlers()
    }

    HttpServlet createPageServlet(MarkdownPageProvider provider) {
        if (config.server.readOnly) {
            new ReadonlyPageServlet(provider, config.templates)
        } else {
            new PageServlet(provider, config.templates)
        }
    }
    private initializeServerHandlers() {
        def docRootResource = Resource.newResource(docRoot)
        def provider = createPageProvider()

        def servletContextHandler = new ServletContextHandler()
        servletContextHandler.with {
            baseResource = docRootResource
            contextPath = '/'
            addServlet(new ServletHolder(createPageServlet(provider)), '*.md')
            if (provider.searchSupported) {
                addServlet(new ServletHolder(new SearchServlet(provider, config.templates)), '/search')
            }
        }

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

    MarkdownPageProvider createPageProvider() {
        PageSearchStrategy strategy
        if (config.search.enabled) {
            strategy = new LuceneSearchStrategy(docRoot, ~/.*\.md/)
            fileChangeMonitor = new DefaultFileMonitor(strategy)
            fileChangeMonitor.recursive = true
            fileChangeMonitor.addFile(VFS.manager.toFileObject(docRoot))
        } else {
            strategy = NullPageSearchStrategy.INSTANCE
        }
        return new MarkdownPageProvider(docRoot, config.server.encoding, strategy)
    }

    void start() {
        fileChangeMonitor?.start()
        server.start()
    }

    void stop() {
        fileChangeMonitor?.stop()
        server.stop()
    }

    void join() {
        server.join()
    }
}