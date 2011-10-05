package microwiki.storage.impl

import microwiki.TestResources
import microwiki.storage.PageNotFoundException
import microwiki.storage.PageStorage
import microwiki.storage.RemovePageException
import microwiki.storage.SavePageException

class FilesystemPageStorageSpecification extends spock.lang.Specification {
    private static TestResources resources
    private static PageStorage storage

    def setupSpec() {
         resources = new TestResources().file('test.md', 'Test page')
         storage = new FilesystemPageStorage(resources.tempDirectory)
    }

    def cleanupSpec() {
        resources.cleanup()
        resources = null
    }

    def "return a page from the filesystem"() {
        expect:
        storage.pageNamed('test').source.toString() == 'Test page'
    }

    def "save a page"() {
        setup:
        storage.savePage('other', 'Other contents')

        expect:
        storage.pageNamed('other').source.toString() == 'Other contents'

        cleanup:
        storage.removePageNamed('other')
    }

    def "unexistent page throws page not found exception"() {
        when:
        storage.pageNamed('unexistent')

        then:
        PageNotFoundException e = thrown()
        e.message == "Page 'unexistent' not found"
        e.pageName == 'unexistent'
        e.cause instanceof FileNotFoundException
    }

    def "throw exception if created with a non-directory root"() {
        setup:
        def root = new File('unexistent')

        when:
        new FilesystemPageStorage(root)

        then:
        IllegalArgumentException e = thrown()
        e.message == "The file $root is not a directory"
    }

    def "throw exception when trying to remove unexistent page"() {
        when:
        storage.removePageNamed('xxx')

        then:
        PageNotFoundException e = thrown()
        e.message == "Page 'xxx' not found"
        e.pageName == 'xxx'
        e.cause instanceof FileNotFoundException
    }

    def "throw exception if page remove fails"() {
        setup:
        resources.tempDirectory.setReadOnly()

        when:
        storage.removePageNamed('test')

        then:
        RemovePageException e = thrown()
        e.message == "The page 'test' cannot be removed"

        cleanup:
        resources.tempDirectory.setWritable(true)
    }

    def "throw exception if page save fails"() {
        setup:
        resources.tempDirectory.setReadOnly()

        when:
        storage.savePage('other', 'Other contents')

        then:
        SavePageException e = thrown()
        e.message == "The page 'other' cannot be saved"

        cleanup:
        resources.tempDirectory.setWritable(true)
    }

    def "invalid page names"() {
        when:
        storage.savePage(name, 'Other contents')

        then:
        thrown(InvalidPageNameException)

        where:
        name << ['/other', 'o:ther', 'do..t']
    }
}
