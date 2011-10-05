package microwiki.storage.impl

class SimplePageName {
    private static final Set<Character> NOT_ALLOWED = ['/', '\\', ':', '.']*.charAt(0).toSet()
    private String name

    SimplePageName(String name) {
        if (name.any { NOT_ALLOWED.contains it.charAt(0) }) throw new InvalidPageNameException(name, "The characters $NOT_ALLOWED are not allowed as page name")
        this.name = name
    }

    File toFile(File root, String extension) {
        return new File(root, "${name}.$extension")
    }
}
