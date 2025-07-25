import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Client extends Application {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private VBox messageContainer;
    private ScrollPane scrollPane;
    private TextField inputField;
    private Label typingLabel;
    private String nickname;
    private ListView<String> userListView;

    private static final String TYPING = "[TYPING]";
    private static final String TYPING_END = "[TYPING_END]";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        socket = new Socket("localhost", 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        PrivateChatManager.getInstance().setOut(out);

        // Step 1: Nickname setup
        String line;
        while ((line = in.readLine()) != null) {
            if (line.trim().equalsIgnoreCase("Enter your nickname:")) {
                boolean accepted = false;
                while (!accepted) {
                    nickname = promptForNickname();
                    out.println(nickname);
                    String response = in.readLine();
                    if (response == null) return;

                    if (response.startsWith("OK")) {
                        String[] parts = response.split("\\s+", 2);
                        if (parts.length == 2) {
                            nickname = parts[1].trim();
                        }
                        accepted = true;
                    } else {
                        showError("Nickname Error", response);
                    }
                }
                break;
            }
        }

        messageContainer = new VBox(5);
        messageContainer.setPadding(new Insets(10));

        scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setStyle("-fx-background: #f9f9f9;");

        inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setPrefWidth(300);
        inputField.setStyle("-fx-background-radius: 20; -fx-padding: 5 10;");

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-background-radius: 20;");
        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        typingLabel = new Label();
        typingLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
        typingLabel.setTextFill(Color.GRAY);

        PauseTransition typingPause = new PauseTransition(Duration.seconds(2));
        final boolean[] typingSent = {false};

        inputField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                if (!typingSent[0]) {
                    out.println("/typing " + nickname);
                    typingSent[0] = true;
                }
                typingPause.playFromStart();
            }
        });

        typingPause.setOnFinished(e -> {
            out.println("/typingdone " + nickname);
            typingSent[0] = false;
        });

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setPadding(new Insets(5));
        inputBox.setAlignment(Pos.CENTER);

        VBox chatBox = new VBox(10, scrollPane, typingLabel, inputBox);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-background-color: #ffffff;");

        userListView = new ListView<>();
        userListView.setPrefWidth(120);
        userListView.getItems().addAll("You");

        VBox userListBox = new VBox(new Label("Online Users"), userListView);
        userListBox.setPrefWidth(130);
        userListBox.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 10;");
        userListBox.setAlignment(Pos.TOP_CENTER);

        userListView.setOnMouseClicked(event -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null && !selectedUser.equals(nickname)) {
                PrivateChatManager.getInstance().openChat(selectedUser);
                PrivateChatManager.getInstance().getChat(selectedUser).appendMessage("👋 Private chat started with " + selectedUser);
            }
        });

        HBox root = new HBox(10, userListBox, chatBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat - " + nickname);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try {
                out.println("/quit");
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    String finalMsg = msg;
                    try {
                        String decrypted = decrypt(finalMsg);

                        if (decrypted.startsWith(TYPING)) {
                            String user = decrypted.substring(TYPING.length()).trim();
                            if (!user.equals(nickname)) {
                                Platform.runLater(() -> typingLabel.setText(user + " is typing..."));
                            }
                        } else if (decrypted.startsWith(TYPING_END)) {
                            Platform.runLater(() -> typingLabel.setText(""));
                        } else if (decrypted.startsWith("/userlist ")) {
                            String[] users = decrypted.substring(10).split(",");
                            Platform.runLater(() -> userListView.getItems().setAll(users));
                        } else if (decrypted.startsWith("(Private)")) {
                            String[] parts = decrypted.split(":", 2);
                            if (parts.length >= 2) {
                                String privateSender = parts[0].replace("(Private)", "").trim();
                                String privateMsg = parts[1].trim();
                                Platform.runLater(() -> PrivateChatManager.getInstance().receivePrivateMessage(privateSender, privateMsg));
                            }
                        } else {
                            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
                            String[] parts = decrypted.split(":", 2);
                            String sender = parts[0].trim();
                            String content = parts.length > 1 ? parts[1].trim() : "";
                            Platform.runLater(() -> addChatBubble(sender, content, time));
                        }

                    } catch (IllegalArgumentException e) {
                        Platform.runLater(() -> addChatBubble("Server", finalMsg, ""));
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> addChatBubble("System", "Disconnected from server.", ""));
            }
        }).start();
    }

    private void addChatBubble(String sender, String message, String time) {
        HBox bubble = new HBox();
        bubble.setAlignment(sender.equals(nickname) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        String bgColor = sender.equals(nickname) ? "#DCF8C6" :
                         (sender.equals("Server") || sender.equals("System")) ? "#EEEEEE" : "#D0E8FF";

        Label label = new Label("[" + time + "] " + (sender.equals(nickname) ? "Me" : sender) + ": " + message);
        label.setWrapText(true);
        label.setMaxWidth(250);
        label.setPadding(new Insets(8));
        label.setStyle("-fx-background-radius: 12; -fx-font-size: 13px; -fx-background-color: " + bgColor + ";");

        bubble.getChildren().add(label);
        messageContainer.getChildren().add(bubble);

        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
            addChatBubble(nickname, message, time);
            inputField.clear();
        }
    }

    private String promptForNickname() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter your nickname (leave blank for guest):");
        return dialog.showAndWait().orElse("").trim(); 
    }

    private String decrypt(String message) {
        return AESEncryption.decrypt(message);
    }
}
