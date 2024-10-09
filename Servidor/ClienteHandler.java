package Servidor;

import java.net.Socket;

import java.io.*;
import java.util.*;

public class ClienteHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private static Set<ClienteHandler> clientHandlers = new HashSet<>();

    public ClienteHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // Registro do nome do cliente
            username = in.readLine(); // O cliente envia apenas seu nome
            
            synchronized (clientHandlers) {
                clientHandlers.add(this);
                System.out.println("Cliente registrado : " + username);                
            }

            String message;
            while ((message = in.readLine()) != null) {
                for(ClienteHandler ch: clientHandlers) {
                    System.out.println(ch.username);
                }
                System.out.println("Recebeu..." + message);
                processMessage(message); // Processa mensagens no formato destinatario:mensagem
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Limpeza
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (clientHandlers) {
                clientHandlers.remove(this);
            }
        }
    }

    private void processMessage(String message) {
        
        String[] parts = message.split(":", 2); // Espera-se "destinatario:mensagem"
        if (parts.length == 2) {
            String recipient = parts[0].trim();
            String msg = parts[1].trim();
            sendMessageToClient(recipient, msg);
        }
    }

    private void sendMessageToClient(String recipient, String msg) {
        synchronized (clientHandlers) {
            for (ClienteHandler handler : clientHandlers) {
                if (handler.username.equals(recipient)) {
                    handler.out.println(username + ": " + msg); // Apenas o destinatário recebe
                    return; // Mensagem enviada, sai do loop
                }
            }
            // Se o destinatário não for encontrado
            out.println("Usuário " + recipient + " não encontrado.");
        }
    }
}
