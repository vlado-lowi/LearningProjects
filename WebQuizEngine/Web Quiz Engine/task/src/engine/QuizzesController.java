package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.List;

@RestController
public class QuizzesController {

    @Autowired
    private QuizCRUDRepository quizCRUD;

    public QuizzesController() {
    }

    @PostMapping(value = "/api/quizzes")
    public Quiz addQuiz(@Valid @RequestBody Quiz quiz) {
        Quiz savedQuiz = quizCRUD.save(quiz);
        return savedQuiz;
    }

    @PostMapping(value = "/api/quizzes/{id}/solve")
    public Response solveQuiz(@PathVariable int id, @RequestBody Answer answer) {
        for (Quiz quiz : quizCRUD.findAll()) {
            if (quiz.getId() == id) {
                return new Response(quiz.getAnswer(), answer.getAnswer());
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
    }

    @GetMapping(value = "/api/quizzes")
    public List<Quiz> getAllQuizzes(){
        return quizCRUD.findAll();
    }

    @GetMapping(value = "/api/quizzes/{id}")
    public Quiz getQuiz(@PathVariable int id) {
        for (Quiz quiz : quizCRUD.findAll()) {
            if (quiz.getId() == id) {
                return quiz;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
    }
}
