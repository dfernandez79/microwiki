package microwiki.servlets

import microwiki.TempDirectory
import javax.servlet.http.HttpServletRequest

class DirectoryListingServletSpecification extends ServletSpecification {
    private static File baseDir
    private DirectoryListingServlet directoryListingServlet

    def setup() {
        directoryListingServlet = new DirectoryListingServlet(Arrays.asList('md', 'txt'), baseDir)
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
        directoryListingServlet.doGet(requestFor('/'), response)
        
        then:
        responseOutput.toString().trim() == 'file1.md file2.txt'
    }
    
    def "Create a sub-directory"() {
        when:
        directoryListingServlet.doPost(createDirectoryRequest('/subdir'), response)

        then:
        new File(baseDir, 'subdir').directory
    }
    
    private HttpServletRequest createDirectoryRequest(String path) {
        requestFor(path, [createDirectory: 'true'])
    }
    
    def "List the contents of a sub-directory"() {
        setup:
        new File(baseDir, 'subdir').mkdir()
        new File(new File(baseDir, 'subdir'), 'test.txt').text = 'Hello'

        when:
        directoryListingServlet.doGet(requestFor('/subdir'), response)

        then:
        responseOutput.toString().trim() == 'test.txt'
    }
    
    def "Don't allow navigation outside the base directory"() {
        // TODO
    }

    def "Use the template to display results"() {
        // TODO
    }
}
