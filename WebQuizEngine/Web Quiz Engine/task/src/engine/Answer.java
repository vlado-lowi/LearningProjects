package engine;

import java.util.ArrayList;
import java.util.Collections;

public class Answer {
    ArrayList<Integer> answer;

    public Answer() {
    }

    public ArrayList<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<Integer> answer) {
        Collections.sort(answer);
        this.answer = answer;
    }
}
