import java.io.PrintWriter;
import java.util.*;

public class PrivateChatManager {
    private static PrivateChatManager instance;
    private final Map<String, PrivateChatWindow> privateChats = new HashMap<>();
    private final Map<String, List<String>> chatHistory = new HashMap<>(); // 🆕 History storage
    private PrintWriter out;

    private PrivateChatManager() {}

    public static PrivateChatManager getInstance() {
        if (instance == null) {
            instance = new PrivateChatManager();
        }
        return instance;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public void openChat(String username) {
        PrivateChatWindow chat = privateChats.get(username);
        if (chat == null) {
            chat = new PrivateChatWindow(username, out);
            privateChats.put(username, chat);

            // 🆕 Load previous messages into the window
            List<String> messages = chatHistory.get(username);
            if (messages != null) {
                for (String m : messages) {
                    chat.appendRawMessage(m);
                }
            }
        } else {
            chat.focus();
        }
    }

    public void receivePrivateMessage(String sender, String message) {
        openChat(sender);
        privateChats.get(sender).appendMessage(sender, message);
        storeMessage(sender, sender + ": " + message); // 🆕 Save to memory
    }

    public void removeChat(String username) {
        privateChats.remove(username);
    }

    public void storeMessage(String user, String fullMsg) {
        chatHistory.computeIfAbsent(user, k -> new ArrayList<>()).add(fullMsg);
    }

    public PrivateChatWindow getChat(String username) {
        return privateChats.get(username);
    }
}
