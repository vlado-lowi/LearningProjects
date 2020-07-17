package engine;

import java.util.ArrayList;

public class Quiz {
    private String title;
    private String text;
    private ArrayList<String> options = new ArrayList<>();

    public Quiz() {
        title = "Tazka otazka!!!";
        text = "Kolko je 2 * 2 ?";
        options.add("Fuuha nekonecno ? ");
        options.add("9");
        options.add("4");
        options.add("Vyplo ma! Moc silna matika....");
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}
