package microwiki

import org.eclipse.jetty.server.Server as JettyServer

import javax.servlet.http.HttpServlet
import microwiki.config.Config
import microwiki.pages.PageProvider
import microwiki.pages.markdown.MarkdownPageProvider
import microwiki.search.lucene.LuceneSearchStrategy
import microwiki.servlets.PageServlet
import microwiki.servlets.ReadonlyPageServlet
import microwiki.servlets.SearchServlet
import org.apache.commons.vfs2.impl.DefaultFileMonitor
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource

class Server {
    static final int DEFAULT_PORT = 9999
    static final String DEFAULT_ENCODING = 'UTF-8'

    final File docRoot
    final Config config
    final PageProvider provider

    private final JettyServer server
    private final DefaultFileMonitor fileChangeMonitor

    Server(File docRoot, Config config) {
        this.docRoot = docRoot
        this.config = config

        server = new JettyServer(config.server.port)
        provider = createPageProvider()
        initializeServerHandlers()

        if (config.server.monitorFileChanges) {
            fileChangeMonitor = new DefaultFileMonitor(new FileListenerAdapter(provider))
        } else {
            fileChangeMonitor = null
        }
    }

    private HttpServlet createPageServlet() {
        if (config.server.readOnly) {
            new ReadonlyPageServlet(provider, config.templates)
        } else {
            new PageServlet(provider, config.templates)
        }
    }

    private initializeServerHandlers() {
        def docRootResource = Resource.newResource(docRoot)

        def servletContextHandler = new ServletContextHandler()
        servletContextHandler.with {
            baseResource = docRootResource
            contextPath = '/'
            addServlet(new ServletHolder(createPageServlet()), '*.md')
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

    private PageProvider createPageProvider() {
        if (config.search.enabled) {
            createPageProviderWithPageSearch()
        } else {
            new MarkdownPageProvider(docRoot, config.server.encoding)
        }
    }

    private PageProvider createPageProviderWithPageSearch() {
        LuceneSearchStrategy luceneSearchStrategy = new LuceneSearchStrategy()
        PageProvider newProvider = new MarkdownPageProvider(docRoot, config.server.encoding, luceneSearchStrategy)
        luceneSearchStrategy.createSearchIndexWithPagesFrom(newProvider)
        newProvider
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