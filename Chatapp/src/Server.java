import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 12345;
    private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final AtomicInteger guestCounter = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String nickname;

                while (true) {
                    writer.println("Enter your nickname:");
                    nickname = reader.readLine();

                    if (nickname == null) {
                        clientSocket.close();
                        return;
                    }

                    nickname = nickname.trim();

                    if (nickname.isEmpty()) {
                        nickname = assignGuestName();
                        writer.println("OK " + nickname);
                        break;
                    } else if (clients.containsKey(nickname)) {
                        writer.println("Nickname already in use. Try another or leave blank for auto guest.");
                    } else {
                        writer.println("OK " + nickname); 
                        break;
                    }
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket, nickname);
                clients.put(nickname, clientHandler);
                new Thread(clientHandler).start();

                broadcast(nickname + " joined the chat.", null);
                sendUserListToAll();
                System.out.println(nickname + " connected.");
            }
        }
    }

    private static String assignGuestName() {
        String name;
        do {
            name = "guest" + guestCounter.getAndIncrement();
        } while (clients.containsKey(name));
        return name;
    }

    public static void broadcast(String message, String sender) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equals(sender)) {
                entry.getValue().sendMessage(encrypt(message));
            }
        }
    }

    public static void privateMessage(String message, String receiver, String sender) {
        if (clients.containsKey(receiver)) {
            clients.get(receiver).sendMessage(encrypt("(Private) " + sender + ": " + message));
        }
    }

    public static void removeClient(String nickname) {
        clients.remove(nickname);
        broadcast(nickname + " left the chat.", null);
        sendUserListToAll();
        System.out.println(nickname + " disconnected.");
    }

    public static void sendUserListToAll() {
        StringBuilder userList = new StringBuilder("/userlist ");
        for (String user : clients.keySet()) {
            userList.append(user).append(",");
        }
        // Remove trailing comma
        if (userList.length() > 10) {
            userList.setLength(userList.length() - 1);
        }
        String encrypted = encrypt(userList.toString());
        for (ClientHandler handler : clients.values()) {
            handler.sendMessage(encrypted);
        }
    }

    public static String encrypt(String message) {
        return AESEncryption.encrypt(message);
    }

    public static String decrypt(String message) {
        return AESEncryption.decrypt(message);
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nickname;

        public ClientHandler(Socket socket, String nickname) throws IOException {
            this.socket = socket;
            this.nickname = nickname;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/typing ")) {
                        Server.broadcast("[TYPING]" + nickname, nickname);
                        continue;
                    }
                    if (message.startsWith("/typingdone ")) {
                        Server.broadcast("[TYPING_END]" + nickname, nickname);
                        continue;
                    }
                    if (message.trim().equalsIgnoreCase("/quit")) {
                        break;
                    }
                    if (message.startsWith("@")) {
                        int spaceIndex = message.indexOf(' ');
                        if (spaceIndex != -1) {
                            String target = message.substring(1, spaceIndex);
                            String privateMsg = message.substring(spaceIndex + 1);
                            Server.privateMessage(privateMsg, target, nickname);
                        }
                    } else {
                        Server.broadcast(nickname + ": " + message, nickname);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    Server.removeClient(nickname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String msg) {
            out.println(msg);
        }
    }
}
