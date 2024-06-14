package hr.k33zo.hanabi.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HttpServer {

    // copy from http_server.txt
    private static final int PORT = 80;
    private static final String WELCOME_FILE = "index.html";
    private static final String EXT_HTML = ".html";
    private static final String EXT_JPG = ".jpg";

    public static void main(String[] args) {
        acceptRequests();
    }

    private static void acceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> processHttp(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processHttp(Socket clientSocket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String filename = WELCOME_FILE;

            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("GET")) {

                    filename = line.substring(line.indexOf("/") + 1, line.lastIndexOf(" "));
                    // enable friendly urls
                    // if filename has extension, leave it, otherwise apend ".html"
                    filename = filename.contains(".") ? filename : filename + EXT_HTML;
                    //System.out.println("filename: " + filename);
                    break; // at the end of debug

                }
            }

            sendResponse(clientSocket, new File(filename));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(Socket clientSocket, File file) throws IOException {
        // ensure default
        if (!file.exists()) {
            file = new File(WELCOME_FILE);
        }
        if (file.toString().endsWith(EXT_JPG)) {
            flushJpg(clientSocket, file);
        } else {
            flushText(clientSocket, file);
        }
    }

    private static void flushJpg(Socket clientSocket, File file) throws IOException {
        try (OutputStream os = new BufferedOutputStream(clientSocket.getOutputStream())) {
            os.write(Files.readAllBytes(file.toPath())); // path -> file and vice versa - io to nio good practice
            //os.flush();
        }
    }

    private static void flushText(Socket clientSocket, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            if (file.toString().endsWith(EXT_HTML)) {
                addHeader(file, bw);
            }
            addBody(file, bw);
            //bw.flush();
        }
    }


    private static void addHeader(File file, BufferedWriter bw) throws IOException {
        bw.write("HTTP/1.1 200 OK\n\r");
        bw.write("Date: " + ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "\n\r");
        bw.write("Server: MiliczasServant/2.2.14 (Win64)\n\r");
        bw.write(" Last-Modified: " + ZonedDateTime.now().minusDays(2).format(DateTimeFormatter.RFC_1123_DATE_TIME) + "\n\r");
        // very important
        bw.write("Content-Length: "  + file.length() + "\n\r");
        bw.write("Content-Type: "+ Files.probeContentType(file.toPath())+"; charset=utf-8\n\r");
        bw.write("Connection: Closed\n\r");
        // very important
        bw.write("\n\r");
    }

    private static void addBody(File file, BufferedWriter bw) throws IOException {
         for (String line : Files.readAllLines(file.toPath())) {
            bw.write(line + "\n\r");
        }
    }

}