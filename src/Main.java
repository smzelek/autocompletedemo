import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        VBox root = new VBox();
        StackPane displayArea = new StackPane();

        ImageView textingView = new ImageView(new Image(getClass().getResourceAsStream("img/blanktextscreen.jpg")));
        textingView.fitWidthProperty().bind(root.widthProperty());
        textingView.setPreserveRatio(true);

        TextArea textArea = new TextArea();
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setPrefWidth(1);
        textArea.setFont(Font.font("Trebuchet MS", 14));

        WordSuggestionTray suggestionTray = new WordSuggestionTray(textArea);

        textArea.setOnKeyReleased((KeyEvent keyEvent) -> {
            suggestionTray.update();
        });
        textArea.setWrapText(true);

        Button sendMsg = new Button("Send");
        sendMsg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        sendMsg.setStyle("-fx-background-radius: 5px;-fx-background-color: forestgreen; -fx-text-fill: floralwhite;");

        HBox sendingArea = new HBox();
        sendingArea.setSpacing(5);
        sendingArea.setPadding(new Insets(5));
        sendingArea.setStyle("-fx-background-color: darkgray;");
        sendingArea.setAlignment(Pos.CENTER_LEFT);
        sendingArea.getChildren().addAll(textArea, sendMsg);
        sendingArea.setHgrow(textArea, Priority.ALWAYS);
        sendingArea.setMaxWidth(275);

        displayArea.getChildren().addAll(textingView, suggestionTray);

        root.getChildren().addAll(displayArea, sendingArea);

        primaryStage.setTitle("Texting App");
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root, 275, 400));
        primaryStage.setWidth(275);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
