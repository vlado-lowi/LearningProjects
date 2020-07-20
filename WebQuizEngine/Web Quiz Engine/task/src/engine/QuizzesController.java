package engine;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class QuizzesController {

    private ArrayList<Quiz> quizzes;
    private AtomicInteger atomicInteger;

    public QuizzesController() {
        this.quizzes = new ArrayList<>();
        atomicInteger = new AtomicInteger();
    }

    @PostMapping(value = "/api/quizzes")
    public Quiz addQuiz(@Valid @RequestBody Quiz quiz) {
        quiz.setId(atomicInteger.getAndIncrement());
        quizzes.add(quiz);
        return quizzes.get(quizzes.size() - 1);
    }

    @PostMapping(value = "/api/quizzes/{id}/solve")
    public Response solveQuiz(@PathVariable int id, @RequestBody Answer answer) {
        for (Quiz quiz : quizzes) {
            if (quiz.getId() == id) {

                return new Response(quiz.getAnswer(), answer.getAnswer());
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
    }

    @GetMapping(value = "/api/quizzes")
    public ArrayList<Quiz> getAllQuizzes(){
        return quizzes;
    }

    @GetMapping(value = "/api/quizzes/{id}")
    public Quiz getQuiz(@PathVariable int id) {
        for (Quiz quiz : quizzes) {
            if (quiz.getId() == id) {
                return quiz;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
    }
}
