package hr.k33zo.hanabi.utils;

import hr.k33zo.hanabi.model.ConfigurationKey;
import hr.k33zo.hanabi.model.ConfigurationReader;
import hr.k33zo.hanabi.model.GameState;
import hr.k33zo.hanabi.model.NetworkConfiguration;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {
    public static void sendGameStateToServer(GameState gameState){
        Integer serverPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.SERVER_PORT);
        String host = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);
        try (Socket clientSocket = new Socket(host, serverPort)){
            System.err.printf("Client is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendGameStateToClient(GameState gameState){
        String host = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);
        Integer clientPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.CLIENT_PORT);
        try (Socket clientSocket = new Socket(host, clientPort)){
            System.err.printf("Client is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequest(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.println("Game state sent to server");
        String configrmationMessage = (String)ois.readObject();
        System.out.println("Confirmation message: " + configrmationMessage);
    }
}
