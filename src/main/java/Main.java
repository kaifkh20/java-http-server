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

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    
    try {
      serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept(); // Wait for connection from client.
      System.out.println("accepted new connection");
      
      InputStream is = clientSocket.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      
      // ArrayList<String[]> list = new ArrayList<>();

      // String firstLine = br.readLine();

      // String path = firstLine.split(" ")[1];

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
      // System.out.println("reaching out of loop");
      // System.err.println(");
      // for(String[] x:list){
      //   for (String str : x) {
      //     System.out.print(str+" ");
      //   }System.out.println();
      // }
      // System.out.println();;

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
      OutputStream os = clientSocket.getOutputStream();
      os.write(message.getBytes());
      // System.out.println("Response sent"+message);
      os.flush();
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
