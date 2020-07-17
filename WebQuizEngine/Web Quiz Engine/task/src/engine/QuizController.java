package engine;

import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
public class QuizController {

    public QuizController(){
    }

    @GetMapping(path = "/api/quiz")
    public Quiz getQuiz(){
        return new Quiz();
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
