package engine;

import java.util.ArrayList;
import java.util.Objects;

public class Response {
    private boolean success;
    private String feedback;

    public Response(ArrayList<Integer> answerList1, ArrayList<Integer> answerList2) {
        boolean correctAnswer;
        if ( (answerList1 == null || answerList1.isEmpty()) &&
                (answerList2 == null || answerList2.isEmpty()) ) {
            correctAnswer = true;
        } else {
            correctAnswer = Objects.equals(answerList1, answerList2);
        }
        success = correctAnswer;
        feedback = correctAnswer ? "Congratulations, you're right!" :
                "Wrong answer! Please, try again.";
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
