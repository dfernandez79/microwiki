package microwiki.pages

import groovy.text.Template
import groovy.text.GStringTemplateEngine

class TemplateAdapter implements PageTemplate {
    public static PageTemplate  using(URL url) {
        return new TemplateAdapter(new GStringTemplateEngine().createTemplate(url))
    }
    private final Template template

    TemplateAdapter(template) {
        this.template = template
    }

    @Override
    Writable applyTo(Page page) {
        return template.make(page: page)
    }
}
