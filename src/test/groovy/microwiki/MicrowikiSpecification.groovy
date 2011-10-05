package microwiki

import microwiki.storage.impl.FilesystemPageStorage

class MicrowikiSpecification extends spock.lang.Specification {
    private static TestResources resources
    private static Microwiki wiki

    def setupSpec() {
         resources = new TestResources().file('index.md', 'Main')
         wiki = new Microwiki(storage: new FilesystemPageStorage(resources.tempDirectory))
    }

    def cleanupSpec() {
        resources.cleanup()
        resources = null
    }

    def "the default page points to the index page"() {
        expect:
        wiki.htmlToDisplay('').toString() == wiki.htmlToDisplay(wiki.defaultPageName).toString()
    }

    def "render HTML with wiki command links"() {
        expect:
        wiki.htmlToDisplay('index').toString().indexOf(link) > 0

        where:
        link << [
                '<a href="/edit/index">Edit</a>'
        ]
    }

    def "The storage cannot be null"() {
        when:
        new Microwiki(storage:null)

        then:
        IllegalArgumentException e = thrown()
        e.message == 'The wiki storage cannot be null'
    }
}
