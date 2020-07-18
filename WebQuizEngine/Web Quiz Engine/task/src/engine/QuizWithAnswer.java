package engine;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class QuizWithAnswer {
    private String title;
    private String text;
    private ArrayList<String> options = new ArrayList<>();
    private int answer;
    private int id;
    public QuizWithAnswer() {
    }

    public QuizWithAnswer(Quiz quiz, int id) {
        this.title = quiz.getTitle();
        this.text = quiz.getText();
        this.options = quiz.getOptions();
        this.answer = quiz.getAnswer();
        this.id = id;
    }

    public QuizWithAnswer(String title, String text, ArrayList<String> options, int answer, int id) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

}
