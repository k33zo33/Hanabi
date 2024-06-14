package hr.k33zo.hanabi.chat.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteChatService extends Remote {

    String REMOTE_CHAT_OBJECT_NAME = "hr.k33zo.rmi.chat.service";
    void sendMessage(String chatMessage) throws RemoteException;

    List<String> getAllChatMessages() throws RemoteException;


}
