package hr.k33zo.hanabi.utils;

import hr.k33zo.hanabi.model.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {
    public static void sendGameStateToServer(GameState gameState){
        String hostname = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);
        try (Socket clientSocket = new Socket(
                hostname,
                ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.SERVER_PORT))){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendGameStateToClient(GameState gameState){
        String hostname = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);
        try (Socket clientSocket = new Socket(
                hostname,
                ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.CLIENT_PORT))){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

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
