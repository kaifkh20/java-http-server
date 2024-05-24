import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class ThreadClient extends Thread{
  private final Socket clientSocket;
  public ThreadClient(Socket clSocket){
    this.clientSocket = clSocket;
  }
  public void run(){
    try{
      InputStream is = clientSocket.getInputStream();
      String message = Main.performOperation(is);

      OutputStream os = clientSocket.getOutputStream();
      os.write(message.getBytes());
      os.flush();
        
      // m.performOperation(i)
    }catch(IOException e){
      System.out.println("ERROR: "+e);
    }
  }
}

public class Main {
  
  static String performOperation(InputStream is) throws IOException{

      BufferedReader br = new BufferedReader(new InputStreamReader(is));


      String line;
      HashMap<String,String> values = new HashMap<>();

      while((line = br.readLine()) != null && !line.isEmpty()){
        // System.err.println(line);

        String[] l = line.split(" ");
        values.put(l[0], l[1]);
        // sc.next();
      }

      for (String key:values.keySet()){
        System.out.println(key+" "+values.get(key));
      }


      String message = "";

      String path = values.get("GET");
      System.out.println(path);
      // System.out.println(request[1]);
      if (path.equals("/")) {
        message = "HTTP/1.1 200 OK\r\n\r\n";
      } else if(path.startsWith("/echo")){
          // System.err.println("reaching here");
          String word = path.split("/")[2];
          // System.out.println(word);
          message = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+ word.length() +"\r\n\r\n"+word;
      }
      else if(path.startsWith("/user-agent")){
        String word = values.get("User-Agent:");
        // System.out.println("reching here user agent");
        // System.out.println(word);
        message = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+ word.length() +"\r\n\r\n"+word;
      }
      else {
        message = "HTTP/1.1 404 Not Found\r\n\r\n";
      }

      return message;
      // OutputStream os = clientSocket.getOutputStream();
      // os.write(message.getBytes());
      // System.out.println("Response sent"+message);
      // os.flush();
  }
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");


      ServerSocket serverSocket = null;
      Socket clientSocket = null;
    
      try {
        serverSocket = new ServerSocket(4221);
        serverSocket.setReuseAddress(true);
        while (true) {
          clientSocket = serverSocket.accept(); // Wait for connection from client.
          ThreadClient th = new ThreadClient(clientSocket);
          th.start();
          System.out.println("accepted new connection");

        }
        
        
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
  }
}
