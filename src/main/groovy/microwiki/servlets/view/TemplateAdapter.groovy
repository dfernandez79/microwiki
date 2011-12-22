package microwiki.servlets.view

import groovy.text.GStringTemplateEngine
import groovy.text.Template

class TemplateAdapter implements ViewTemplate {
    static ViewTemplate using(source, Map context = [:]) {
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
    Writable applyWith(Map context) {
        template.make(templateSpecificContext + context)
    }

    @Override
    String toString() {
        description + (templateSpecificContext.keySet().empty ? '' : " context: $templateSpecificContext")
    }
}