package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
public class QuizzesController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public QuizzesController() {
    }

    @PostMapping(value = "/api/quizzes")
    public Quiz addQuiz(@Valid @RequestBody Quiz quiz , HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if ( authorization == null) {
            return quizRepository.save(quiz);
        }
        String username = HelperMethods.getUsernameFromAuthorizationString(authorization);
        User user = userRepository.findByEmail(username);
        user.getQuizzes().add(quiz);
        Quiz savedQuiz = quizRepository.save(quiz);
        return savedQuiz;
    }

    @PostMapping(value = "/api/quizzes/{id}/solve")
    public Response solveQuiz(@PathVariable int id, @RequestBody Answer answer) {
        for (Quiz quiz : quizRepository.findAll()) {
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid JSON body format.");
        }

        try{
            email = (String)emailObject;
            password = (String)passwordObject;
        } catch (ClassCastException e) {
            System.err.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid JSON body format.");
        }

        if (HelperMethods.validateEmail(email) == false || password.length() < 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid email format or password is not at least 5 chars long.");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Email %s is already in use.", email));
        }

        User user = new User();
        user.setEmail(email);
        user.setPwHash(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @GetMapping(value = "/api/quizzes")
    public List<Quiz> getAllQuizzes(){
        return quizRepository.findAll();
    }

    @GetMapping(value = "/api/quizzes/{id}")
    public Quiz getQuiz(@PathVariable Long id) {
        var quiz = quizRepository.findById(id);
        if (quiz.isPresent()) {
            return quiz.get();
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
        }
    }

    @DeleteMapping(value = "/api/quizzes/{id}")
    public void deleteQuiz(@PathVariable Long id, @RequestHeader("Authorization") String authorization) {
        var quiz = quizRepository.findById(id);
        if (quiz.isPresent()) {
            String username = HelperMethods.getUsernameFromAuthorizationString(authorization);
            User user = userRepository.findByEmail(username);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete quizzes that you created.");
            } else {
                if (user.getQuizzes().contains(quiz.get())) {
                    quizRepository.delete(quiz.get());
                    throw new ResponseStatusException(HttpStatus.NO_CONTENT);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete quizzes that you created.");
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
        }
    }
}
