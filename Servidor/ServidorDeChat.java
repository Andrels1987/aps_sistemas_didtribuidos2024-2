package Servidor;

import java.io.*;
import java.net.*;

public class ServidorDeChat {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Servidor de chat iniciado...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClienteHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}

