package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
public class QuizzesController {

    @Autowired
    private QuizCRUDRepository quizCRUD;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    @PostMapping(value = "/api/register")
    public void register(@RequestBody Map<String,Object> registrationData) {
        Object emailObject = registrationData.get("email");
        Object passwordObject = registrationData.get("password");
        String email;
        String password;

        if (emailObject == null || passwordObject == null) {
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST));
        }

        try{
            email = (String)emailObject;
            password = (String)passwordObject;
        } catch (ClassCastException e) {
            System.err.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (EmailValidator.validateEmail(email) == false || password.length() < 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(email);
        user.setPwHash(passwordEncoder.encode(password));
        // todo finish registration its not working at all as of now....

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
