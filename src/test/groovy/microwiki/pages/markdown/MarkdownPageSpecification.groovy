package microwiki.pages.markdown

class MarkdownPageSpecification {
    def "Infer page title from first header"() {
        when:
        def page = new MarkdownPage('test.md'.toURI(), pageSource(), 'UTF-8')

        then:
        page.title == 'Title'
    }

    def pageSource() {
        [getText: {encoding ->
            '''
Title
------
This is a text''' }]
    }
}