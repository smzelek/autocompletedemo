import javafx.scene.control.Button;

public class WordSuggestionButton extends Button {

    WordSuggestionButton()
    {
        super();
        setStyle("-fx-background-radius: 0px;");
        setMaxWidth(Double.MAX_VALUE);
        setFocusTraversable(false);
    }
}
