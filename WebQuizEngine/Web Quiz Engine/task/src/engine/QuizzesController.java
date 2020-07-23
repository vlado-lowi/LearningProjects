package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class QuizzesController {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompletionsRepository completionsRepo;
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
    public Response solveQuiz(@PathVariable Long id, @RequestBody Answer answer, HttpServletRequest request) {
        var quizFromRepo = quizRepository.findById(id);
        if (quizFromRepo.isPresent()) {
            Response response = new Response(quizFromRepo.get().getAnswer(), answer.getAnswer());
            String authorization = request.getHeader("Authorization");
            if (authorization == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            String username = HelperMethods.getUsernameFromAuthorizationString(authorization);
            if (username != null && response.isSuccess()) {
                User user = userRepository.findByEmail(username);
                System.out.println("found user");
                if(user != null) {
                    System.out.println("user is registered");
                    Completions completion = new Completions();
                    completion.setQuizId(id);
                    completion.setCompletedAt(new Timestamp(System.currentTimeMillis()));
//                    user.getCompletions().add(completion);
                    completion.setUser(user);
                    completionsRepo.save(completion);
                    System.out.println("saved completion");
                }
            }
            return response;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found.");
        }
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
    public ResponseBody<Quiz> getAllQuizzes(@RequestParam(name = "page", defaultValue = "0") int pageNumber){
        int pageSize = 10;
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        Page<Quiz> pagedResult = quizRepository.findAll(paging);

        if(pagedResult.hasContent()){
            return new ResponseBody<>(pagedResult.getContent());
        } else {
            return new ResponseBody<>(new ArrayList<Quiz>());
        }
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

    @GetMapping(value = "/api/quizzes/completed")
    public ResponseBody<Completions> getCompletedQuizzes(@RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                 @RequestHeader("Authorization") String authorization) {
        int pageSize = 10;
        String username = HelperMethods.getUsernameFromAuthorizationString(authorization);
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        List<Completions> usersCompletedQuizzes = completionsRepo.findAllByUserId(user.getId());
        Collections.sort(usersCompletedQuizzes, new Comparator<Completions>() {
            @Override
            public int compare(Completions obj1, Completions obj2) {
                return obj1.getCompletedAt().compareTo(obj2.getCompletedAt());
            }
        });
        Collections.reverse(usersCompletedQuizzes);
        return new ResponseBody<>(usersCompletedQuizzes
                .stream()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toCollection(ArrayList::new)));
//        Pageable paging = PageRequest.of(pageNumber, pageSize);
//        System.out.println("Querying DB");
//        Page<Completions> pagedResult = completionsRepo.findAllByUserId(user.getId(), paging);
//        System.out.println("Got result");
//        if (pagedResult.hasContent()){
//            return new ResponseBody<>(pagedResult.getContent());
//        } else {
//            return new ResponseBody<>(new ArrayList<Completions>());
//        }
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
