import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
      String line = br.readLine();
      String []request = line.split(" ",0);

      System.out.println(line);

      OutputStream os = clientSocket.getOutputStream();
      System.out.println(request[1]);
      if (request[1].equals("/")) {
        os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
      } else if(request[1].contains("/echo")){
          String word = request[1].split("/")[1];
          os.write(("HTTP/1.1 200 OK\r\n Content-Type: text/plain\r\n"+"Content-Length:"+ word.length() +"\r\n\r\n"+word).getBytes());
      }
      else {
        os.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
      }
      
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
