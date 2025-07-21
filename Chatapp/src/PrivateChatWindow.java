import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.io.PrintWriter;

public class PrivateChatWindow {
    private final String targetUser;
    private final PrintWriter out;
    private final VBox messageBox = new VBox(5);
    private final ScrollPane scrollPane = new ScrollPane();
    private Stage stage;

    public PrivateChatWindow(String targetUser, PrintWriter out) {
        this.targetUser = targetUser;
        this.out = out;
        openWindow();
    }

    private void openWindow() {
        stage = new Stage();
        stage.setTitle("Private Chat - " + targetUser);

        messageBox.setPadding(new Insets(10));

        scrollPane.setContent(messageBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        TextField inputField = new TextField();
        inputField.setPromptText("Message " + targetUser + "...");
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String msg = inputField.getText().trim();
                if (!msg.isEmpty()) {
                    out.println("@" + targetUser + " " + msg);
                    appendMessage("Me", msg);
                    inputField.clear();
                }
            }
        });

        VBox layout = new VBox(10, scrollPane, inputField);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 320, 400);
        stage.setScene(scene);
        stage.show();

        // Remove reference from manager on close
        stage.setOnCloseRequest(e -> {
            PrivateChatManager.getInstance().removeChat(targetUser);
        });
    }

    public void appendMessage(String sender, String message) {
        Platform.runLater(() -> {
            HBox bubble = new HBox();
            bubble.setAlignment(sender.equals("Me") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            Label label = new Label((sender.equals("Me") ? "Me: " : sender + ": ") + message);
            label.setWrapText(true);
            label.setMaxWidth(200);
            label.setPadding(new Insets(8));
            label.setStyle("-fx-background-radius: 12; -fx-background-color: " +
                    (sender.equals("Me") ? "#DCF8C6;" : "#E6E6E6;") +
                    " -fx-font-size: 13px;");

            bubble.getChildren().add(label);
            messageBox.getChildren().add(bubble);

            scrollToBottom();
        });
    }

    public void appendMessage(String message) {
        appendMessage(targetUser, message);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }

    public void focus() {
        if (stage != null) {
            Platform.runLater(stage::toFront);
        }
    }
    
    public void appendRawMessage(String rawMessage) {
        Platform.runLater(() -> {
            HBox bubble = new HBox();
            boolean isSelf = rawMessage.startsWith("Me:");
            bubble.setAlignment(isSelf ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            Label label = new Label(rawMessage);
            label.setWrapText(true);
            label.setMaxWidth(200);
            label.setPadding(new Insets(8));
            label.setStyle("-fx-background-radius: 12; -fx-background-color: " +
                    (isSelf ? "#DCF8C6;" : "#E6E6E6;") +
                    " -fx-font-size: 13px;");

            bubble.getChildren().add(label);
            messageBox.getChildren().add(bubble);

            scrollToBottom();
        });
    }

}
