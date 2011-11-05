package microwiki.servlets.view

import microwiki.pages.Page

import microwiki.servlets.view.Templates
import microwiki.servlets.view.ViewTemplate

class TemplatesSpecification extends spock.lang.Specification {
    private static final ViewTemplate TEST_TEMPLATE = { Page p -> "Hello $p" } as ViewTemplate

    def "If a template is not configured the default is used"() {
        expect:
        templatesWithout(templateName)."$templateName" == expected

        where:
        templateName | expected
        'display'    | Templates.DEFAULT_DISPLAY_TEMPLATE
        'edit'       | Templates.DEFAULT_EDIT_TEMPLATE
        'create'     | Templates.DEFAULT_CREATE_TEMPLATE
        'read'       | Templates.DEFAULT_READ_TEMPLATE
        'search'    | Templates.DEFAULT_SEARCH_TEMPLATE
    }

    private Templates templatesWithout(String templateName) {
        def config = [
                create: TEST_TEMPLATE,
                edit: TEST_TEMPLATE,
                display: TEST_TEMPLATE,
                read: TEST_TEMPLATE,
                search: TEST_TEMPLATE
        ]
        config.remove(templateName)
        new Templates(config)
    }
}
