package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import groovy.io.FileType
import java.util.regex.Pattern

class DirectoryListingServlet extends HttpServlet {
    private Pattern fileNamePattern
    private File baseDir
    
    DirectoryListingServlet(List<String> extensions, File baseDir) {
        this.fileNamePattern = createPatternFrom(extensions)
        this.baseDir = baseDir
    }
    
    private Pattern createPatternFrom(List<String> extensions) {
        String regex = (extensions.collect { '^.*\\.' + it }).join('|')

        Pattern.compile(regex)
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        new File(baseDir, fileNameFrom(req)).eachFileMatch(FileType.ANY, fileNamePattern) { File f ->
            resp.writer.print "$f.name "
        }
    }

    private String fileNameFrom(HttpServletRequest req) {
        return req.servletPath.substring(1)
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        if ('true' == req.getParameter('createDirectory')) {
            new File(baseDir, fileNameFrom(req)).mkdir()
        }
        doGet(req, resp)
    }
}
