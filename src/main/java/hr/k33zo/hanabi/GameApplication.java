package hr.k33zo.hanabi;

import hr.k33zo.hanabi.chat.service.RemoteChatService;
import hr.k33zo.hanabi.chat.service.RemoteChatServiceImpl;
import hr.k33zo.hanabi.controller.GameController;
import hr.k33zo.hanabi.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static hr.k33zo.hanabi.model.NetworkConfiguration.*;

public class GameApplication extends Application {

    public static RoleName loggedInRoleName;
    public static GameController gameController;
    public static RemoteChatService remoteChatService;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 750);
        gameController = fxmlLoader.getController();
        stage.setTitle(loggedInRoleName.name());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String inputRoleName = args[0];

        for(RoleName rn : RoleName.values()) {
            if(rn.name().equals(inputRoleName)) {
                loggedInRoleName = rn;
                break;
            }
        }

        new Thread(Application::launch).start();

        if (loggedInRoleName == RoleName.SERVER) {
            startRmiChatServer();
            acceptRequestsAsServer();
        }
        else if(loggedInRoleName == RoleName.CLIENT){
            startRmiRemoteChatClient();
            acceptRequestsAsClient();

        }

    }




    private static void acceptRequestsAsServer() {
        Integer serverPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.SERVER_PORT);
        try (ServerSocket serverSocket = new ServerSocket(serverPort)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());;
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptRequestsAsClient() {
        Integer clientPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.CLIENT_PORT);
        try (ServerSocket serverSocket = new ServerSocket(clientPort)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());;
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            GameState gameState = (GameState) ois.readObject();


            Platform.runLater(()->gameController.updateGameState(gameState));

            System.out.printf("Game state received");
            oos.writeObject("Game state received confirmation!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void startRmiChatServer(){
        try {
            Integer rmiPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.RMI_PORT);
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            remoteChatService = new RemoteChatServiceImpl();
            Integer randomPortHint = ConfigurationReader.getInstance().readIntegerValueForKey(
                    ConfigurationKey.RANDOM_PORT_HINT);
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteChatService, randomPortHint);
            //RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteChatService, NetworkConfiguration.RANDOM_PORT_HINT);
            registry.rebind(RemoteChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void startRmiRemoteChatClient(){
        String host = ConfigurationReader.getInstance().readStringValueForKey(ConfigurationKey.HOST);
        Integer rmiPort = ConfigurationReader.getInstance().readIntegerValueForKey(ConfigurationKey.RMI_PORT);
        try {
            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            remoteChatService = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_CHAT_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

}