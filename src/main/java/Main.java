import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
      
      OutputStream os = clientSocket.getOutputStream();
      ArrayList<String[]> list= new ArrayList<>();

      // String str = "

      while(true){
        String line = br.readLine();
        if (line==null){
          break;
        }
        String []request = line.split(" ",0);
        list.add(request);
      }

      // for(String[] x:list){
      //   for (String str : x) {
      //     System.out.print(str+" ");
      //   }System.out.println();
      // }
      // System.out.println(line);;

      String message = "";
      // System.out.println(request[1]);
      if (list.get(0)[1].equals("/")) {
        message = "HTTP/1.1 200 OK\r\n\r\n";
      } else if(list.get(0)[1].contains("/echo")){
          // System.err.println("reaching here");
          String word = list.get(0)[1].split("/")[2];
          // System.out.println(word);
          message = ("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+ word.length() +"\r\n\r\n"+word);
      }
      else if(list.get(0)[1].equals("/user-agent")){
        String word = list.get(2)[1];
        // System.out.println("reching here");
        // System.out.println(word);
        message = ("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+ word.length() +"\r\n\r\n"+word);
      }
      else {
        message = "HTTP/1.1 404 Not Found\r\n\r\n";
      }
      
      os.write(message.getBytes());
      System.out.println("Response sent");
      os.flush();
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
