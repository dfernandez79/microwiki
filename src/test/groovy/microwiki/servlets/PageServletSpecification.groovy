package microwiki.servlets

class PageServletSpecification extends spock.lang.Specification {
    def displayTemplate
    def "When method is GET display the page"() {
        // TODO
    }

    def "When method is GET and the ?edit parameter is specified, display the edit page"() {
        // TODO
    }

    def "When method is POST, change the page contents"() {
        // TODO
    }

    def "If the file is not found, display the edit page with the new file action"() {
        // TODO
    }

    def "If the file is not found and the ?edit parameter is specified, display the edit page with the new file action"() {
        // TODO
    }

    def "When method is POST and the ?create parameter is specified, create a new file"() {
        // TODO
    }

    def "When method is POST and the ?create parameter is specified and the file exists, generate an error"() {
        // TODO
    }
}
