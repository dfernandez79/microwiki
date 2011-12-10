package microwiki.pages.markdown

class MarkdownPageSpecification extends spock.lang.Specification {
    def "Infer page title from first header"() {
        when:
        def page = new MarkdownPage('test.md'.toURI(), pageSource(), 'UTF-8')

        then:
        page.title == 'Title'
    }

    def "When no page title could be infered use the file name as title"() {
        when:
        def page = new MarkdownPage('test.md'.toURI(), pageSourceWithoutHeader(), 'UTF-8')

        then:
        page.title == 'test.md'
    }

    def pageSource() {
        [getText: {encoding ->
            '''
Title
------
This is a text'''
        }]
    }

    def pageSourceWithoutHeader() {
        [getText: {encoding -> 'Text' }]
    }
}