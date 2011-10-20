package microwiki

class TempDirectory {
    static File create() {
        def tempFile = File.createTempFile('microwiki', '.test')
        File dir = new File(tempFile.parent, 'microwiki-testresources')
        tempFile.delete()
        if (dir.exists() && dir.directory) {
            dir.deleteDir()
        }
        assert dir.mkdir()
        dir
    }
}
