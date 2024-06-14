package hr.k33zo.hanabi.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final String HOST = "localhost";
    public static final int PORT = 1989;

    public static void main(String[] args) {
        acceptRequests();
    }

    private static void acceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());

                //new Thread(() ->  processPrimitiveClient(clientSocket)).start();
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processPrimitiveClient(Socket clientSocket) {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            String message = dis.readUTF();
            System.out.printf("Server received: %s%n", message);
            dos.writeInt(countVowels(message));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            Country country = (Country)ois.readObject();
            System.out.printf("The client lives in %s%n", country);
            country.setName("Ireland");
            oos.writeObject(country);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static int countVowels(String message) {
        return message.toLowerCase().replaceAll("[^aeiou]", "").length();
    }
}