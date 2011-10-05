package microwiki

class TestResources {
    public final File tempDirectory = createTempTestDirectory()

    public TestResources file(String name, String contents) {
        new File(tempDirectory, name).text = contents
        return this
    }

    private File createTempTestDirectory() {
        def path = System.getProperty("java.io.tmpdir") + "/unitest"
        def dir = new File(path)
        dir.mkdirs()
        return dir
    }

    public void cleanup() {
        tempDirectory.deleteDir()
    }
}
