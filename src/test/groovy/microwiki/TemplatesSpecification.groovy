package microwiki

import microwiki.pages.Page
import microwiki.pages.PageTemplate
import microwiki.pages.Templates

class TemplatesSpecification extends spock.lang.Specification {
    private static PageTemplate TEST_TEMPLATE = { Page p -> "Hello $p" } as PageTemplate

    def "If a template is not configured the default is used"() {
        expect:
        templatesWithout(templateName)."$templateName" == expected

        where:
        templateName | expected
        'display'    | Templates.DEFAULT_DISPLAY_TEMPLATE
        'edit'       | Templates.DEFAULT_EDIT_TEMPLATE
        'create'     | Templates.DEFAULT_CREATE_TEMPLATE
        'read'       | Templates.DEFAULT_READ_TEMPLATE
    }

    private Templates templatesWithout(String templateName) {
        def config = [
                create: TEST_TEMPLATE,
                edit: TEST_TEMPLATE,
                display: TEST_TEMPLATE,
                read: TEST_TEMPLATE
        ]
        config.remove(templateName)
        new Templates(config)
    }
}
