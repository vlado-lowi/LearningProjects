package engine;

import org.springframework.web.bind.annotation.*;

@RestController
public class QuizController {

    public QuizController(){
    }

    @GetMapping(path = "/api/quiz")
    public QuizWithAnswer getQuiz(){
        return new QuizWithAnswer();
    }

    @PostMapping(path = "/api/quiz")
    public Response solveQuiz(@RequestParam("answer") int answer) {
        if (answer == 2) {
            return new Response(true);
        } else {
            return new Response(false);
        }
    }

}
