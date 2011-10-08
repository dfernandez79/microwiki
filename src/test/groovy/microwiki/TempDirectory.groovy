package microwiki

class TempDirectory {
    static File create() {
        def tempFile = File.createTempFile('microwiki', '.test')
        File dir = new File(tempFile.parent, 'microwiki-testresources')
        tempFile.delete()
        assert dir.mkdir()
        return dir
    }
}
