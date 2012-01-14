package microwiki.servlets

import groovy.io.FileType
import java.util.regex.Pattern
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.servlets.view.ViewTemplate

class DirectoryListingFilter implements Filter {
    private Pattern fileNamePattern
    private File baseDir
    private ViewTemplate directoryListingTemplate

    DirectoryListingFilter(List<String> extensions, File baseDir, ViewTemplate directoryListingTemplate) {
        this.fileNamePattern = createPatternFrom(extensions)
        this.baseDir = baseDir
        this.directoryListingTemplate = directoryListingTemplate
    }

    private Pattern createPatternFrom(List<String> extensions) {
        String regex = (extensions.collect { '^.*\\.' + it }).join('|')

        Pattern.compile(regex)
    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request
        HttpServletResponse httpResponse = (HttpServletResponse) response
        File file = fileFor(httpRequest)

        if (file != null && file.directory && httpRequest.method == 'GET') {
            showFiles(file, httpResponse)
        } else if (file != null && !file.exists() && httpRequest.method == 'POST') {
            createDirectory(file, httpResponse)
        } else {
            chain.doFilter(request, response)
        }
    }

    private void showFiles(File directory, HttpServletResponse resp) {
        List<File> files = []
        directory.eachDir { files << it }
        directory.eachFileMatch(FileType.ANY, fileNamePattern) { files << it }
        directoryListingTemplate.applyWith([files: files]).writeTo(resp.writer)
    }

    private File fileFor(HttpServletRequest req) {
        String subDirPath = req.servletPath.substring(1)
        File subDir = new File(baseDir, subDirPath)
        subDir.canonicalPath.startsWith(baseDir.canonicalPath) ? subDir : null
    }

    private void createDirectory(File directory, HttpServletResponse resp) {
        directory.mkdir()
        showFiles(directory, resp)
    }

    @Override
    void destroy() {
        // Do nothing - required by the Filter interface
    }

    @Override
    void init(javax.servlet.FilterConfig filterConfig) {
        // Do nothing - required by the Filter interface
    }
}