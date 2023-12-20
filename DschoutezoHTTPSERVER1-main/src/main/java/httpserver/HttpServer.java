package httpserver;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void start() {
        int port = 8000;
        try {
            this.serverSocket = new ServerSocket(port); // porta del browser
            System.out.println("Server in ascolto sulla porta " + port);
            InnerHttpServer handler = new InnerHttpServer();

            while (true) {
                this.clientSocket = serverSocket.accept();

                handler.handleRequest(clientSocket);
            }
        } catch (Exception e) {
        }
    }

    private class InnerHttpServer extends Thread {

        private void handleRequest(Socket clientSocket) {
            try {
                BufferedReader inputstream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

                String requestString = inputstream.readLine();
                System.out.println(requestString);

                // controllo della request

                String[] reqSplitted = requestString.split(" ");
                // reqSplitted[0];// GET
                // reqSplitted[1]; // /
                // reqSplitted[2]; // HTTP/1.1

                if (requestString != null && reqSplitted[1].equals("/")) {

                    sendResponse(outputStream, "HTTP/1.1 200 OK", "text/html", readFile("text.html"));
                    System.out.println("Ho finito di inviare la pagina HTML");

                }
                if (requestString != null && reqSplitted[1].equals("/myStyle.css")) {

                    sendResponse(outputStream, "HTTP/1.1 200 OK", "text/css", readFile("myStyle.css"));
                    System.out.println("file CSS inviato con successo");

                }
                if (requestString != null && reqSplitted[1].equals("/fileJS.js")) {

                    sendResponse(outputStream, "HTTP/1.1 200 OK", "text/javascript", readFile("fileJS.js"));
                    System.out.println("file javascript inviato con successo");

                }  
                if(requestString != null && reqSplitted[1].equals("/file.json")){
                    sendResponse(outputStream, "HTTP/1.1 200 OK", "text/json", readFile("file.json"));
                    System.out.println("file json inviato con successo");
                }
                if(requestString != null && reqSplitted[1].equals("/images/nbaLogo.png")){
                    sendImageResponse(outputStream, "HTTP/1.1 200 OK", "image/png","images/nbaLogo.png" , readImage("images/nbaLogo.png"), readImage("images/lakers.jpg").length);
                    System.out.println("Invio immagine completato");
                }
                if(requestString != null && reqSplitted[1].equals("/favicon.ico")){
                    sendImageResponse(outputStream, "HTTP/1.1 200 OK", "image/png","images/nbaLogo.png" , readImage("images/nbaLogo.png"), readImage("images/lakers.jpg").length);
                    System.out.println("Invio immagine completato");
                }
                else {
                    sendResponse(outputStream, "HTTP/1.1 404 Bad Request", "text/html", readFile("bad_request.html"));
                    System.out.println("Invio pagina BAD REQUEST");
                }

                clientSocket.close();
            } catch (Exception e) {
            }
        }

        private String readFile(String filename) {
            StringBuilder content = new StringBuilder(); /*
                                                          * StringBuilder è un oggetto dinamico che
                                                          * consente di espandere il numero di caratteri nella stringa
                                                          * incapsulata
                                                          */
            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n"); // ogni volta che viene letta una riga la concatena e va a capo
                }

            } catch (Exception e) {
                System.out.println("Non fa");
            }

            return content.toString();
        }


        private byte[] readImage(String filename){
            try{

                Path path = Paths.get(filename);

                Files.readAllBytes(path);

                return Files.readAllBytes(path);
            
            
            }catch(Exception e){
                return new byte[0];
            }
            
         }

        private void sendImageResponse(DataOutputStream outputStream , String status, String contentType , String filename ,  byte[] content , int length){
            try {
                String response = status + "\r\n" +
                                  "Content-type: " + contentType + "\r\n" +
                                   "Content-length: " + length + "\r\n" +
                                    "\r\n";// riga vuota che separa gli headers dal body
                     
                outputStream.writeBytes(response);
                int lenghtread;
                FileInputStream reader = new FileInputStream(filename);
                while ((lenghtread = reader.read(content)) > 0) {
                        outputStream.write(content , 0 , lenghtread);
                }
                outputStream.write(content);
                reader.close();
                outputStream.flush();

            } catch (Exception e) {
              
            }
        } 

        
        private void sendResponse(DataOutputStream outputStream, String status, String contentType, String content) {
            try {
                String response = status + "\r\n" +
                        "Content-type: " + contentType + "\r\n" +
                        "Content-length: " + content.getBytes().length + "\r\n" +
                        "\r\n" + // riga vuota che separa gli headers dal body
                        content;
                outputStream.writeBytes(response);
                outputStream.flush();
            } catch (Exception e) {
            }
        }
    }
}
