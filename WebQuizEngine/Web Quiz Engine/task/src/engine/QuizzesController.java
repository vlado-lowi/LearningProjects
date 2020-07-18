package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class QuizzesController {

    private Quizzes quizzes;
    private AtomicInteger atomicInteger;

    @Autowired
    public QuizzesController(Quizzes quizzes) {
        this.quizzes = quizzes;
        atomicInteger = new AtomicInteger();
    }

    @PostMapping(value = "/api/quizzes")
    public QuizNoAnswer addQuiz(@RequestBody Quiz quiz) {
        QuizWithAnswer newQuiz = new QuizWithAnswer(quiz, atomicInteger.getAndIncrement());
        quizzes.getQuizWithAnswer().add(newQuiz);
        quizzes.getqWithoutAnswer().add(new QuizNoAnswer(newQuiz));
        return quizzes.getqWithoutAnswer().get(newQuiz.getId());
    }

    @PostMapping(value = "/api/quizzes/{id}/solve")
    public Response solveQuiz(@PathVariable int id, @RequestParam int answer) {
        for (QuizWithAnswer quiz : quizzes.getQuizWithAnswer()) {
            if (quiz.getId() == id) {
                return new Response(quiz.getAnswer() == answer);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
    }

    @GetMapping(value = "/api/quizzes")
    public ArrayList<QuizNoAnswer> getAllQuizzes(){
        return quizzes.getqWithoutAnswer();
    }

    @GetMapping(value = "/api/quizzes/{id}")
    public QuizNoAnswer getQuiz(@PathVariable int id) {
        for (QuizNoAnswer quiz : quizzes.getqWithoutAnswer()) {
            if (quiz.getId() == id) {
                return quiz;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
    }
}
