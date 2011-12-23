package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import groovy.io.FileType
import java.util.regex.Pattern
import microwiki.servlets.view.ViewTemplate

class DirectoryListingServlet extends HttpServlet {
    private Pattern fileNamePattern
    private File baseDir
    private ViewTemplate directoryListingTemplate
    
    DirectoryListingServlet(List<String> extensions, File baseDir, ViewTemplate directoryListingTemplate) {
        this.fileNamePattern = createPatternFrom(extensions)
        this.baseDir = baseDir
        this.directoryListingTemplate = directoryListingTemplate
    }
    
    private Pattern createPatternFrom(List<String> extensions) {
        String regex = (extensions.collect { '^.*\\.' + it }).join('|')

        Pattern.compile(regex)
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<File> files = new LinkedList<File>();

            directoryFor(req).eachFileMatch(FileType.ANY, fileNamePattern) { files << it }

            directoryListingTemplate.applyWith([files: files])
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
        }
    }

    private File directoryFor(HttpServletRequest req) {
        String subDirPath = req.servletPath.substring(1);
        File subDir = new File(baseDir, subDirPath)
        if (!subDir.canonicalPath.startsWith(baseDir.canonicalPath))  {
            throw new IllegalArgumentException("$subDir is no a sub-directory of $baseDir")
        }
        subDir
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if ('true'.equalsIgnoreCase(req.getParameter('createDirectory'))) {
                directoryFor(req).mkdir()
            }
            doGet(req, resp)
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
        }
    }
}