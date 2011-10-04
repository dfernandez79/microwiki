package microwiki

class MicrowikiSpecification extends spock.lang.Specification {
    def "the / path points to the default page"() {
        setup:
        def wiki = new Microwiki()

        expect:
        wiki.htmlAt('/') == htmlPageWithText('Main')
    }

    def String htmlPageWithText(String text) {
        "<!doctype html><html><body><p>$text</p></body></html>"
    }
}
