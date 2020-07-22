package engine;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Quiz{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private String title;
    @NotEmpty
    @Column(name = "question")
    private String text;

    @Size(min = 2)
    @NotNull
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "QuizID", nullable = false)
    private List<Options> options = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "QuizID", nullable = false)
    private List<CorrectOption> answer;


    public Quiz() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return id.equals(quiz.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getOptions() {

        return options.stream().map(Options::getOption).
                collect(Collectors.toCollection(ArrayList::new));
    }

    public void setOptions(ArrayList<String> options) {

        this.options = options.stream().map(e -> {
            Options option = new Options();
            option.setOption(e);
            return option;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Integer> getAnswer() {
        return answer.stream().map(CorrectOption::getCorrectOption).
                collect(Collectors.toCollection(ArrayList::new));
    }

    public void setAnswer(ArrayList<Integer> answer) {
        Collections.sort(answer);
        this.answer = answer.stream().map(e -> {
            CorrectOption correctOption = new CorrectOption();
            correctOption.setCorrectOption(e);
            return correctOption;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
