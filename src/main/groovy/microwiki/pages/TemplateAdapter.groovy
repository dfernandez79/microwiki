package microwiki.pages

import groovy.text.GStringTemplateEngine
import groovy.text.Template

class TemplateAdapter implements PageTemplate {
    public static PageTemplate using(source) {
        return new TemplateAdapter(new GStringTemplateEngine().createTemplate(source))
    }

    private final Template template

    TemplateAdapter(Template template) {
        this.template = template
    }

    @Override
    Writable applyWith(PageDisplayContext context) {
        return template.make(context.asMap())
    }
}
