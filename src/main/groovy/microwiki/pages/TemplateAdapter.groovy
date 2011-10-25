package microwiki.pages

import groovy.text.GStringTemplateEngine
import groovy.text.Template

class TemplateAdapter implements PageTemplate {
    static PageTemplate using(source) {
        using(source, Collections.emptyMap());
    }

    static PageTemplate using(source, Map context) {
        String desc = 'Template created from reader'
        if ([File.class, URL.class].any { source?.class == it }) {
            desc = source.toString()
        } else if (source instanceof String) {
            desc = 'Template from source "' + source[0..<Math.min(source.size(), 30)] + '..."'
        }
        new TemplateAdapter(desc, new GStringTemplateEngine().createTemplate(source), context)
    }

    private final description
    private final Template template
    private final Map templateSpecificContext

    TemplateAdapter(String description, Template template, Map templateSpecificContext) {
        this.description = description
        this.template = template
        this.templateSpecificContext = templateSpecificContext
    }

    @Override
    Writable applyWith(PageDisplayContext context) {
        template.make(templateSpecificContext + context.asMap())
    }

    @Override
    String toString() {
        return description + (templateSpecificContext.keySet().empty ? '' : " context: $templateSpecificContext")
    }


}