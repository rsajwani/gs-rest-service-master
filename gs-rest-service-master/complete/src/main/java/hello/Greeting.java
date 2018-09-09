package hello;

public class Greeting {

    private final long id;
    private final String content;
    private ShoeStepsMetaData metaData;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Greeting(long id, ShoeStepsMetaData metaData) {
        this.id = id;
        this.content = "";
        this.metaData = metaData;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public ShoeStepsMetaData getMetaData() { return metaData;}
}
