package microwiki

class Microwiki {
    private def outputWriter = MarkdownHtmlWriter.class

    String htmlAt(String path) {
        Writer output = new StringWriter()
        write(path, output)
        return output.toString()
    }

    void write(String path, Writer destination) {
        outputWriter.on(destination).write pageAt(path)
    }

    private WikiPage pageAt(String path) {
        return new WikiPage(null, "Main")
    }
}
