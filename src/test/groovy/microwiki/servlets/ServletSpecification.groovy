package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class ServletSpecification extends spock.lang.Specification {
    protected HttpServletResponse response
    protected StringWriter responseOutput

    def setup() {
        int status = 200
        response = Mock()
        response.contentType = 'text/html'
        response.characterEncoding = 'UTF-8'
        responseOutput = new StringWriter()
        response.writer >> { new PrintWriter(responseOutput) }
        response.reset() >> { responseOutput.buffer.length = 0 }
        response.sendError(_, _) >> { int sc, String msg -> status = sc}
        response.status >> { status }
    }

    protected HttpServletRequest requestFor(String path, Map<String, String> parameters = null) {
        HttpServletRequest req = Mock()
        req.method >> 'GET'
        req.servletPath >> path
        if (parameters != null) {
            req.getParameter(_ as String) >> { parameters.get(it.get(0)) }
        }
        req
    }
}
