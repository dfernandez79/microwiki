package microwiki.pages

import groovy.text.GStringTemplateEngine
import groovy.text.Template

class TemplateAdapter implements PageTemplate {
    static PageTemplate using(source) {
        using(source, Collections.emptyMap());
    }

    static PageTemplate using(source, Map context) {
        new TemplateAdapter(new GStringTemplateEngine().createTemplate(source), context)
    }

    private final Template template
    private final Map templateSpecificContext

    TemplateAdapter(Template template, Map templateSpecificContext) {
        this.template = template
        this.templateSpecificContext = templateSpecificContext
    }

    @Override
    Writable applyWith(PageDisplayContext context) {
        template.make(templateSpecificContext + context.asMap())
    }
}