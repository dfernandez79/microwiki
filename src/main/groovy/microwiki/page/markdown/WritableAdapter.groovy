package microwiki.page.markdown

class WritableAdapter implements Writable {
    private final def object

    public static Writable adapt(object) {
        if (object instanceof Writable) {
            return object
        } else {
            return new WritableAdapter(object)
        }
    }

    WritableAdapter(object) {
        this.object = object
    }

    @Override
    Writer writeTo(Writer out) {
        out.write(object.toString())
        return out
    }

    @Override
    String toString() {
        return object.toString()
    }

    @Override
    int hashCode() {
        return object.hashCode()
    }

    @Override
    boolean equals(Object obj) {
        return obj instanceof WritableAdapter && obj.object == object
    }
}
