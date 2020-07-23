package engine;

import java.util.List;

public class ResponseBody <T> {
    private List<T> content;

    public ResponseBody(List<T> content) {
        this.content = content;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
