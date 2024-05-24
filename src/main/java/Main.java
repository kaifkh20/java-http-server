import java.io.BufferedReader;
import java.io.File;
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
  private final String dirName;
  public ThreadClient(Socket clSocket,String dString){
    this.clientSocket = clSocket;
    this.dirName = dString;
  }
  public void run(){
    try{
      InputStream is = clientSocket.getInputStream();
      String message = Main.performOperation(is,dirName);

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
  
  static String performOperation(InputStream is,String dirName) throws IOException{

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
      else if(path.startsWith("/files")){
        String word = path.split("/")[2];
        try{
          File file = new File(dirName+"/"+word);
          Scanner sc = new Scanner(file);
          String content = "";
          if(file.exists()){
            while(sc.hasNextLine()){
              content+=sc.nextLine();
            }
            sc.close();
            message = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: "+ content.length() +"\r\n\r\n"+content;
        }}catch(IOException e){
          System.out.println("ERROR :"+e);
          message = "HTTP/1.1 404 Not Found\r\n\r\n";
        }  
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
      
        String dirName="";
      
        if(args.length>0){
          dirName = args[1];
        }

        while (true) {
          clientSocket = serverSocket.accept(); // Wait for connection from client.
          ThreadClient th = new ThreadClient(clientSocket,dirName);
          th.start();
          System.out.println("accepted new connection");

        }
        
        
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
  }
}
