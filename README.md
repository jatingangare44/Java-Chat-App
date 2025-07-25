# Java Chat Application

This is a **Java-based Chat Application** that supports both public and private messaging features between clients connected to a central server. The chat messages are encrypted using AES encryption for enhanced privacy.

## Features

- **Multi-client support** via socket programming.
- **Private chat windows** for one-on-one communication.
- **AES encryption** of messages.
- GUI implemented using **JavaFX**.
- Timestamps and formatted message display.
- Auto-scroll and smooth chat interactions.

## Components

### 🔹 `Server.java`
- Handles multiple clients using multithreading.
- Broadcasts messages to all clients.
- Manages client connections and disconnections.

### 🔹 `Client.java`
- Connects to the server.
- Provides a JavaFX-based GUI for public chatting.
- Allows users to initiate private chats.

### 🔹 `PrivateChatWindow.java`
- Creates a new JavaFX window for private one-on-one communication.
- Supports sending and receiving encrypted messages.

### 🔹 `PrivateChatManager.java`
- Singleton manager to handle unique private chat windows.
- Prevents multiple windows for the same user pair.

### 🔹 `AESEncryption.java`
- Utility class to encrypt and decrypt chat messages using AES algorithm.

## Requirements

- Java 8 or above.
- JavaFX SDK (for GUI components).

## How to Run

### 1. Start the Server
Compile and run `Server.java`:
```bash
javac Server.java
java Server
```
### 2. Start the Client
Compile and run `Client.java`:
```bash
javac Client.java
java Client
```

## Screenshots
<img width="1050" height="238" alt="image" src="https://github.com/user-attachments/assets/ebf3f273-6b7c-4931-b239-3b3974071138" />

<img width="328" height="275" alt="image" src="https://github.com/user-attachments/assets/06d5fa4e-5c93-4205-8376-63cb70a4d5e1" />

<img width="894" height="714" alt="image" src="https://github.com/user-attachments/assets/10ffe798-9ee5-4749-9666-5b15163c0149" />

<img width="905" height="715" alt="image" src="https://github.com/user-attachments/assets/be59e884-30fd-4609-8b91-41a1801f6731" />

<img width="900" height="322" alt="image" src="https://github.com/user-attachments/assets/ff144a92-1f21-4a2a-b61a-270d659e4c53" />

<img width="897" height="716" alt="image" src="https://github.com/user-attachments/assets/c86f983e-0520-4a70-9524-5b5f3902a512" />

<img width="471" height="635" alt="image" src="https://github.com/user-attachments/assets/759ac688-1b26-4304-8685-d0921a650069" />

<img width="479" height="635" alt="image" src="https://github.com/user-attachments/assets/caec8b03-0836-480c-a264-8f99ed2e9458" />






