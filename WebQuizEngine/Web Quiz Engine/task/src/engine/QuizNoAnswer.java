package engine;

import java.util.ArrayList;

public class QuizNoAnswer {
    private String title;
    private String text;
    private ArrayList<String> options = new ArrayList<>();
    private int id;

    public QuizNoAnswer(QuizWithAnswer quiz) {
        this.title = quiz.getTitle();
        this.text = quiz.getText();
        this.options = quiz.getOptions();
        this.id = quiz.getId();
    }

    public QuizNoAnswer(String title, String text, ArrayList<String> options) {
        this.title = title;
        this.text = text;
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }
}
