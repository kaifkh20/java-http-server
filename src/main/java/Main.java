import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.*;

class Response{
  String message;
  byte[] encoded_word;

  public Response(String message,byte[]encoded_word){
    this.message = message;
    this.encoded_word = encoded_word;
  }
}

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
      Response message = Main.performOperation(is,dirName);

      OutputStream os = clientSocket.getOutputStream();

      if(message.encoded_word!=null){
        os.write(message.message.getBytes());
        os.write(message.encoded_word);
      }else{
        os.write(message.message.getBytes());
        // os.flush();
      }
      os.flush();
      os.close();

        
      // m.performOperation(i)
    }catch(IOException e){
      System.out.println("ERROR: "+e);
    }
  }
}

public class Main {

  // static String bytestoHex(byte[] bArray){
  //   StringBuilder br = new StringBuilder();
  //   for(byte b:bArray){
  //       String hex = Integer.toHexString(0xff & b);
  //       if(hex.length()==0){
  //         br.append('0');
  //       }
  //       br.append(hex);
  //   }
  //   return br.toString();
  // }

  static byte[] compressString(String str){
    if(str==null || str.length()==0){
      return null;
    }
    try{
      ByteArrayOutputStream byteArrayOs = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOs);
      gzip.write(str.getBytes());
      gzip.close();
      byte[] barr =  byteArrayOs.toByteArray();
      return barr;
      // return byteArrayOs.toString();
      // return Base64.getEncoder().encodeToString(barr);
      // return bytestoHex(byteArrayOs.toByteArray());
    }catch(IOException err){
      System.out.println("ERROR: "+err);
      // return str;
    }
    return null;
  }
  
  static Response performOperation(InputStream is,String dirName) throws IOException{

      BufferedReader br = new BufferedReader(new InputStreamReader(is));


      String line;
      HashMap<String,String> values = new HashMap<>();
      StringBuilder  sb = new StringBuilder();
      while((line = br.readLine()) != null && !line.isEmpty()){
        // System.err.println(line);
        StringBuilder sb_temp = new StringBuilder();
        String[] l = line.split(" ");
        for(int i=1;i<l.length;i++){
          sb_temp.append(l[i]);
        }
        // System.out.println();
        values.put(l[0],sb_temp.toString());        // System.out.println(l[0]+" "+l[1]);
        // sc.next();
      }

      // for(String x : values.keySet()){clear
      //   if(x.equals("Accept-Encoding")){
      //     System.out.println(true);
      //   }
      //   System.out.println(x+" "+values.get(x));
      // }
      System.out.println(values.get("Accept-Encoding:"));
      if(values.containsKey("POST")){

      int c_l = Integer.parseInt(values.get("Content-Length:"));
      while(c_l-->0){
        int c = br.read();
        sb.append((char)c);
      }
      values.put("Body", sb.toString());
      }


      // if(values.containsKey("POST")){
      //   values.put("Body", br.readLine());
      // }

      // System.out.println("REACHING HERE");
      // for (String key:values.keySet()){
      //   System.out.println(key+" "+values.get(key));
      // }


      String message = "";
      String path = "";
      if(values.containsKey("GET")){
        path = values.get("GET");
        
      }else if(values.containsKey("POST")){
        path = values.get("POST");
      }
      path = path.split("H")[0];
      System.out.println(path);
      // System.out.println(path);
      // System.out.println(request[1]);
      if (path.equals("/")) {
        message = "HTTP/1.1 200 OK\r\n\r\n";
      } else if(path.startsWith("/echo")){
          // System.err.println("reaching here");
          String word = path.split("/")[2];
          message = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+ word.length() +"\r\n\r\n"+word;

          if(values.containsKey("Accept-Encoding:")){
            // System.out.println(values.get("Accept-Encoding:"));
            if(values.get("Accept-Encoding:").contains("gzip")){
              byte[] resp_word = compressString(word);
              message = "HTTP/1.1 200 OK\r\nContent-Encoding: "+"gzip"+"\r\n"+"Content-Type: text/plain\r\nContent-Length: "+ resp_word.length +"\r\n\r\n";
              return new Response(message, resp_word);
            }
          }          



          // System.out.println(word);
      }
      else if(path.startsWith("/user-agent")){
        String word = values.get("User-Agent:");
        // System.out.println("reching here user agent");
        // System.out.println(word);
        message = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+ word.length() +"\r\n\r\n"+word;
      }
      else if(path.startsWith("/files") && values.containsKey("GET") ){
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
      }else if(path.startsWith("/files") && values.containsKey("POST")){
        String word = path.split("/")[2];    
        try{
              File file = new File(dirName+"/"+word);
              if(!file.exists()){
                if(!file.getParentFile().exists()){
                  System.out.println(file.getParent());
                  file.getParentFile().createNewFile();
                }
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                fw.write(values.get("Body"));
                fw.close();
              }else if(file.exists()){
                FileWriter fw = new FileWriter(file);
                fw.append(values.get("Body"));
                fw.close();
              }
              message = "HTTP/1.1 201 Created\r\n\r\n";
            }catch(IOException e){
                System.out.println("ERROR :"+e);
            }
      }
      else {
        message = "HTTP/1.1 404 Not Found\r\n\r\n";
      }
      System.out.println(message);
      return new Response(message, null);
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
