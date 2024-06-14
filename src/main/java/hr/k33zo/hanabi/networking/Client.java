package hr.k33zo.hanabi.networking;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        sendRequest();
    }

    private static void sendRequest() {
        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)){
            System.err.printf("Client is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequest(clientSocket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendPrimitiveRequest(Socket clientSocket) throws IOException {
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

        System.out.print("Insert sentence: ");
        dos.writeUTF(readSentence(System.in));
        System.out.printf("Number of vowels: %d%n", dis.readInt());
    }

    private static void sendSerializableRequest(Socket client) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(new Country("Croatia", 1));
        System.out.printf("Moved to: %s%n", ois.readObject());
    }

    private static String readSentence(InputStream in) {
        try(Scanner scanner = new Scanner(in)) {
            return scanner.nextLine();
        }
    }
}
