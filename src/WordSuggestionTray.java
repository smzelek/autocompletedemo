import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import sun.misc.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 3/3/2017.
 */

//Word Suggestion Tray has 3 buttons
//extends Hbox
//has update method that is called by onKeyReleased of TA
//uses regex and populates its 3 buttons
//sets managed true to those that it fills

//if a word suggestion button is clicked
//update must be called on the Word Tray

class Node {
    String prefix;
    boolean isWord = false;
    ArrayList<Node> children = new ArrayList<>();
}

public class WordSuggestionTray extends HBox {

    private WordSuggestionButton suggestion1;
    private WordSuggestionButton suggestion2;
    private WordSuggestionButton suggestion3;

    private WordSuggestionButton[] suggestions;

    private Pattern p = Pattern.compile("(\\S+)$");
    private TextArea messageText;

    private ArrayList<Node> trie;
    private final int MIN_WORD_LENGTH = 1;
    private final String dictionaryFileName = "src/dictionary.txt";

    public WordSuggestionTray (TextArea ta)
    {
        messageText = ta;
        createTrie();

        suggestion1 = new WordSuggestionButton();
        suggestion1.setOnMouseClicked((MouseEvent mouseEvent) -> {
            messageText.setText(messageText.getText().replaceAll("(\\S+)$", suggestion1.getText()));
            messageText.positionCaret(messageText.getText().length());
            update();
        });

        suggestion2 = new WordSuggestionButton();
        suggestion2.setOnMouseClicked((MouseEvent mouseEvent) -> {
            messageText.setText(messageText.getText().replaceAll("(\\S+)$", suggestion2.getText()));
            messageText.positionCaret(messageText.getText().length());
            update();
        });

        suggestion3 = new WordSuggestionButton();
        suggestion3.setOnMouseClicked((MouseEvent mouseEvent) -> {
            messageText.setText(messageText.getText().replaceAll("(\\S+)$", suggestion3.getText()));
            messageText.positionCaret(messageText.getText().length());
            update();
        });

        suggestions = new WordSuggestionButton[] {suggestion1, suggestion2, suggestion3};

        setAlignment(Pos.BOTTOM_CENTER);
        setPrefWidth(Double.MAX_VALUE);
        getChildren().addAll(suggestion1, suggestion2, suggestion3);
        setHgrow(suggestion1, Priority.ALWAYS);
        setHgrow(suggestion2, Priority.ALWAYS);
        setHgrow(suggestion3, Priority.ALWAYS);
        hideSuggestions();
    }

    public void update()
    {
        hideSuggestions();
        Matcher m = p.matcher(messageText.getText());
        if (m.find()) {
            ArrayList<String> completions = getWordCompletions(m.group());
            for (int i = 0; i < completions.size(); ++i){
                suggestions[i].setVisible(true);
                suggestions[i].setManaged(true);
                suggestions[i].setText(completions.get(i));
            }
        }
    }

    public void hideSuggestions(){
        for (int i = 0; i < suggestions.length; ++i){
            suggestions[i].setVisible(false);
            suggestions[i].setManaged(false);
            suggestions[i].setText("");
        }
    }

    private ArrayList<String> getWordCompletions(String incompleteWord){

        ArrayList<String> threeCompletions = new ArrayList<>();

        ArrayList<Node> subtrie = trie;
        for (int i = 0; i < incompleteWord.length(); ++i){
            Node n = getNodeByLetter(subtrie, incompleteWord.charAt(i));
            if (n == null){
                return threeCompletions; //flag: word is not a prefix of anything in dictionary
            }
            subtrie = n.children;
        }

        Queue<Node> followingLetters = new LinkedList<>();
        for (Node n : subtrie){
            followingLetters.add(n);
        }

        while (!followingLetters.isEmpty())
        {
            Node currentPrefix = followingLetters.poll();
            if (currentPrefix.isWord){
                threeCompletions.add(currentPrefix.prefix);
            }
            if (threeCompletions.size() == 3)
            {
                return threeCompletions;
            }
            for (Node n : currentPrefix.children){
                followingLetters.add(n);
            }
        }

        return threeCompletions;

        //subtrie should now point to children of prefix formed by incomplete word
        //do BFS to find first 3 results
        //if find result, add to list
        //if list size == 3 or no options left, return
        //recurse down the trie
    }


    private void createTrie(){
        ArrayList<String> dictionary = new ArrayList<>();

        Scanner scan;
        try {
            InputStream in = getClass().getResourceAsStream("/dictionary.txt");
            scan = new Scanner(in);
        } catch (Exception e){
            System.err.println(e);
            return;
        }

        while (scan.hasNext()) {
            dictionary.add(scan.next());
        }
        scan.close();

        trie = new ArrayList<>();

        for (String s : dictionary) {
            if (s.length() < MIN_WORD_LENGTH)
                continue;

            ArrayList<Node> currentPrefix = trie;
            String prefix = "";

            for (int i = 0; i < s.length(); ++i) {
                prefix += s.charAt(i);
                Node n = getNodeByLetter(currentPrefix, s.charAt(i));

                if (n == null) {
                    Node currentLetterNode = new Node();
                    currentLetterNode.prefix = prefix;

                    if (i == s.length() - 1) {
                        currentLetterNode.isWord = true;
                    }

                    currentPrefix.add(currentLetterNode);
                    currentPrefix = currentLetterNode.children;
                } else {
                    if (i == s.length() - 1) {
                        n.isWord = true;
                    }

                    currentPrefix = n.children;
                }
            }
        }
    }

    private static Node getNodeByLetter(ArrayList<Node> nodes, char letterToFind) {
        for (Node n : nodes) {
            if (n.prefix.charAt(n.prefix.length() - 1) == letterToFind) {
                return n;
            }
        }
        return null;
    }
}
