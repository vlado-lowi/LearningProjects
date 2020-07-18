package engine;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Quizzes {
    private ArrayList<QuizNoAnswer> qWithoutAnswer = new ArrayList<>();
    private ArrayList<QuizWithAnswer> quizWithAnswer = new ArrayList<>();

    public Quizzes() {
    }

    public ArrayList<QuizNoAnswer> getqWithoutAnswer() {
        return qWithoutAnswer;
    }

    public void setqWithoutAnswer(ArrayList<QuizNoAnswer> qWithoutAnswer) {
        this.qWithoutAnswer = qWithoutAnswer;
    }

    public ArrayList<QuizWithAnswer> getQuizWithAnswer() {
        return quizWithAnswer;
    }

    public void setQuizWithAnswer(ArrayList<QuizWithAnswer> quizWithAnswer) {
        this.quizWithAnswer = quizWithAnswer;
    }
}
