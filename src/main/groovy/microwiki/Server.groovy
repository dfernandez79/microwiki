package microwiki

import org.eclipse.jetty.server.Server as JettyServer

import javax.servlet.Filter
import javax.servlet.http.HttpServlet
import microwiki.config.Config
import microwiki.pages.PageProvider
import microwiki.pages.WritablePageProvider
import microwiki.pages.markdown.MarkdownPageProvider
import microwiki.search.PageSearchIndex
import microwiki.search.SearchResultsDisplayOptions
import microwiki.search.lucene.LucenePageSearchIndex
import microwiki.servlets.DirectoryListingFilter
import microwiki.servlets.PageServlet
import microwiki.servlets.ReadonlyPageServlet
import microwiki.servlets.SearchServlet
import org.apache.commons.vfs2.impl.DefaultFileMonitor
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.util.resource.ResourceCollection
import org.eclipse.jetty.servlet.*

/**
 * The microwiki web server. Once that it´s instantiated with the proper {@link Config configuration}, it
 * can be controlled by using the {@link Server#start} and {@link Server#stop} methods.
 * <p>
 * When the user starts the application from the command line, the starting point is the class
 * {@link microwiki.cli.Launcher}, which creates and starts an instance of this class.</p>
 *
 * @author dfernandez79
 */
class Server {
    final File docRoot
    final Config config
    final PageProvider provider

    private final JettyServer server
    private final DefaultFileMonitor fileChangeMonitor
    private final PageSearchIndex searchIndex

    Server(File docRoot, Config config) {
        this.docRoot = docRoot
        this.config = config

        provider = createPageProvider()

        if (config.search.enabled) {
            searchIndex = new LucenePageSearchIndex(provider)
            fileChangeMonitor = new DefaultFileMonitor(new FileListenerAdapter(searchIndex))
        } else {
            searchIndex = null
            fileChangeMonitor = null
        }

        server = new JettyServer(config.server.port)
        initializeServerHandlers()
    }


    private HttpServlet createPageServlet() {
        if (config.server.readOnly || !(provider instanceof WritablePageProvider)) {
            new ReadonlyPageServlet(provider, config.search.enabled, config.templates)
        } else {
            new PageServlet((WritablePageProvider) provider, config.search.enabled, config.templates)
        }
    }

    private initializeServerHandlers() {
        server.handler = new ServletContextHandler().with {
            baseResource = new ResourceCollection(
                    Resource.newResource(docRoot),
                    Resource.newClassPathResource('/microwiki/static'))

            contextPath = '/'

            addServlet(new ServletHolder(createPageServlet()), '*.md')
            if (config.search.enabled) {
                addServlet(new ServletHolder(createSearchServlet()), '/search')
            }

            def defaultServletHolder = new ServletHolder(new DefaultServlet())
            if (config.server.aliases) {
                defaultServletHolder.setInitParameter('aliases', 'true')
            }

            addServlet(defaultServletHolder, '/')

            def directoryListingFilterHolder = new FilterHolder(createDirectoryListingFilter())
            servletHandler.addFilter(directoryListingFilterHolder,
                    new FilterMapping(
                            filterName: directoryListingFilterHolder.name,
                            servletName: defaultServletHolder.name))

            it
        }
    }

    private SearchServlet createSearchServlet() {
        new SearchServlet(searchIndex, SearchResultsDisplayOptions.default, config.templates)
    }

    private Filter createDirectoryListingFilter() {
        new DirectoryListingFilter(['*.md'], docRoot, config.templates.directoryListing)
    }

    private PageProvider createPageProvider() {
        new MarkdownPageProvider(docRoot, config.server.encoding)
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