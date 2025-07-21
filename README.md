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
