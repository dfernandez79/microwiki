package microwiki

import microwiki.storage.PageNotFoundException
import microwiki.storage.impl.FilesystemPageStorage

class FilesystemPageStorageSpecification extends spock.lang.Specification {
    private static File directory
    private static def storage

    def setupSpec() {
         directory = prepareTestDirectory()
         storage = new FilesystemPageStorage(directory)
    }

    def cleanupSpec() {
        directory.deleteDir()
        storage = null
        directory = null
    }

    def "return a page from the filesystem"() {
        expect:
        storage.pageAt('/test').uri == new File(directory, 'test.md').toURI()
        storage.pageAt('/test').source == 'Test page'
    }

    def "save page"() {
        setup:
        storage.savePage('other', 'Other contents')

        expect:
        storage.pageAt('/other').source == 'Other contents'

        cleanup:
        storage.removePageAt('/other')
    }

    def "unexistent page throws page not found exception"() {
        when:
        storage.pageAt('unexistent')

        then:
        PageNotFoundException e = thrown()
        e.message == "Page 'unexistent' not found"
        e.path == 'unexistent'
        e.cause instanceof FileNotFoundException
    }

    def "throw exception if created with a non-directory root"() {
        setup:
        def root = new File('/unexistent')

        when:
        new FilesystemPageStorage(root)

        then:
        IllegalArgumentException e = thrown()
        e.message == "The file $root is not a directory"
    }

    def "throw exception when trying to remove unexistent page"() {
        when:
        storage.removePageAt('xxx')

        then:
        PageNotFoundException e = thrown()
        e.message == "Page 'xxx' not found"
        e.path == 'xxx'
        e.cause instanceof FileNotFoundException
    }

    def "throw exception if page remove fails"() {
        // TODO
    }

    def "throw exception if page save fails"() {
        // TODO
    }

    private File prepareTestDirectory() {
        def path = System.getProperty("java.io.tmpdir") + "/unitest"
        new File(path).mkdirs()
        def tempDir = new File(path)
        new File(tempDir, 'test.md').text = 'Test page'
        return tempDir
    }
}
