# Java Encrypted Chat Application (Internship Project with Elevate Labs)

## Overview
This project was developed as part of my **2-week internship phase with Elevate Labs**. It is a **real-time peer-to-peer chat system** built using **Java, Socket Programming, Threads, and JavaFX**. The application includes **AES-based encryption**, **private messaging**, **typing indicators**, and **automatic guest nickname assignment**.

## Internship Objective
**Objective:** Create a real-time peer-to-peer chat system.  
**Tools:** Java, Socket Programming, Threads

**Mini Guide (as provided):**
1. Implement `ServerSocket` to handle clients.  
2. Create `ClientHandler` using threads.  
3. Design a simple text-based GUI with JavaFX.  
4. Allow group and private messaging.  
5. Add user nicknames and connection logs.  
6. Implement basic message encryption.

## Features
- **AES Encryption:** All messages are encrypted for secure communication.
- **Typing Indicator:** Shows when a user is typing in real-time.
- **Private Chat:** Ability to send private messages to selected users.
- **Auto Guest Names:** Users without nicknames are automatically assigned `Guest1`, `Guest2`, etc.
- **User List:** Displays all currently connected users.
- **GUI Client:** A JavaFX-based chat interface with modern chat bubbles.

## Project Structure
- **Server.java:** Handles client connections, broadcasts messages, and manages user lists.
- **Client.java:** GUI client application built with JavaFX, connects to the server.
- **AESEncryption.java:** Provides AES-based message encryption/decryption.
- **PrivateChatManager.java:** Manages private chat windows and sessions.
- **PrivateChatWindow.java:** GUI for private chat between two users.

## Requirements
- **Java 11 or above**
- **JavaFX SDK** (required for GUI)
- **IDE:** IntelliJ IDEA / Eclipse / VS Code (or any Java IDE)

## How to Run
### 1. Start the Server
Compile and run the `Server.java` file:
```bash
javac Server.java
java Server
```
The server will start on **port 12345**.

### 2. Start the Client
Compile and run the `Client.java` file:
```bash
javac Client.java
java Client
```
- Enter a nickname when prompted.
- If no nickname is given, the system assigns a guest name (`Guest1`, `Guest2`, etc.).

### 3. Start Multiple Clients
Run multiple instances of `Client.java` to simulate multiple users chatting in real-time.

## Private Messaging
- To send a private message, click on a username from the user list. 
- A **Private Chat Window** will open where you can chat securely.

## Typing Indicator
- When a user is typing, other users see a "User is typing..." message in real-time.

## Future Enhancements
- File transfer support
- Emojis and stickers
- Message history with database storage

## Author
Developed as part of a **Java Networking and Cryptography project**.

---


## Screenshots
<img width="1050" height="238" alt="image" src="https://github.com/user-attachments/assets/ebf3f273-6b7c-4931-b239-3b3974071138" />

<img width="328" height="275" alt="image" src="https://github.com/user-attachments/assets/06d5fa4e-5c93-4205-8376-63cb70a4d5e1" />

<img width="894" height="714" alt="image" src="https://github.com/user-attachments/assets/10ffe798-9ee5-4749-9666-5b15163c0149" />

<img width="905" height="715" alt="image" src="https://github.com/user-attachments/assets/be59e884-30fd-4609-8b91-41a1801f6731" />

<img width="900" height="322" alt="image" src="https://github.com/user-attachments/assets/ff144a92-1f21-4a2a-b61a-270d659e4c53" />

<img width="897" height="716" alt="image" src="https://github.com/user-attachments/assets/c86f983e-0520-4a70-9524-5b5f3902a512" />

<img width="471" height="635" alt="image" src="https://github.com/user-attachments/assets/759ac688-1b26-4304-8685-d0921a650069" />

<img width="479" height="635" alt="image" src="https://github.com/user-attachments/assets/caec8b03-0836-480c-a264-8f99ed2e9458" />






