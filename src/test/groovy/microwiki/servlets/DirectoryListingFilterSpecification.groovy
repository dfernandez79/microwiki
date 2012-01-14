package microwiki.servlets

import groovy.text.GStringTemplateEngine
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.TempDirectory
import microwiki.servlets.view.ViewTemplate

class DirectoryListingFilterSpecification extends ServletSpecification {
    private static File baseDir
    private DirectoryListingFilter directoryListingFilter
    private ViewTemplate TEST_TEMPLATE = { new GStringTemplateEngine().createTemplate('${files.collect({it.name}).join(\' \')}').make it } as ViewTemplate
    private FilterChain filterChain

    def setup() {
        directoryListingFilter = new DirectoryListingFilter(Arrays.asList('md', 'txt'), baseDir, TEST_TEMPLATE)
        filterChain = Mock()
    }

    def setupSpec() {
        baseDir = TempDirectory.create()
        new File(baseDir, 'file1.md').text = 'Test file 1'
        new File(baseDir, 'file2.txt').text = 'Test file 2'
        new File(baseDir, 'file3.other').text = 'Test file 3'
    }

    def cleanupSpec() {
        baseDir?.deleteDir()
    }

    def "Display only the specified file extensions"() {
        when:
        directoryListingFilter.doFilter(requestFor('/'), response, filterChain)

        then:
        responseOutput.toString().trim() == 'file1.md file2.txt'
    }

    def "Create a sub-directory"() {
        when:
        directoryListingFilter.doFilter(createDirectoryRequest('/subdir'), response, filterChain)

        then:
        new File(baseDir, 'subdir').directory
    }

    private HttpServletRequest createDirectoryRequest(String path) {
        HttpServletRequest req = Mock()
        req.method >> 'POST'
        req.servletPath >> path
        req
    }

    def "List the contents of a sub-directory"() {
        setup:
        new File(baseDir, 'subdir').mkdir()
        new File(new File(baseDir, 'subdir'), 'test.txt').text = 'Hello'

        when:
        directoryListingFilter.doFilter(requestFor('/subdir'), response, filterChain)

        then:
        responseOutput.toString().trim() == 'test.txt'
    }

    def "Navigation outside the base directory is delegated to the filterChain"() {
        setup:
        filterChain.doFilter(_, _) >> { response.sendError(HttpServletResponse.SC_FORBIDDEN, 'Next in chain') }

        when:
        directoryListingFilter.doFilter(requestFor('/..'), response, filterChain)

        then:
        response.status == HttpServletResponse.SC_FORBIDDEN
    }

    def "Use the template to display results"() {
        setup:
        def template = { new GStringTemplateEngine().createTemplate('TEMPLATE').make it } as ViewTemplate
        def servlet = new DirectoryListingFilter(Arrays.asList('md', 'txt'), baseDir, template)

        when:
        servlet.doFilter(requestFor('/'), response, filterChain)

        then:
        responseOutput.toString() == 'TEMPLATE'
    }
}
