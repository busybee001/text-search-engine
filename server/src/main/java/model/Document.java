package model;

public class Document {
    private final String name;
    private final String content;

    public Document(String name, String content) {
        this.name    = name;
        this.content = content;
    }

    public String getName()    { return name; }
    public String getContent() { return content; }
}