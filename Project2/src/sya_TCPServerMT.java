// Multi-threaded Server program
// File name: TCPServerMT.java
// Programmer: Sarah Yaw


import java.io.*;
import java.net.*;
import java.util.*;

public class sya_TCPServerMT
{
    private static ServerSocket servSock;
    public static File chatLog;
    public static FileWriter log;
    public static void main(String[] args)
    {
        System.out.println("Opening port...\n");
        try
        {
            // Create a server object
            servSock = new ServerSocket(Integer.parseInt(args[0])); 
        }
        catch(IOException e)
        {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }
        do { run(); }while (true);
    }

    private static void run()
    {
        Socket link = null; 
        try
        {
            // Put the server into a waiting state
            link = servSock.accept();

            //create file
            //if(arr.size()==1)
            {
                chatLog = new File("sy_chat.txt");
                chatLog.createNewFile();
            }

            // Set up input and output streams for socket
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(),true);

            // print local host name
            String host = InetAddress.getLocalHost().getHostName();     
       
            //take in username here to print below
            String user = in.readLine();
            System.out.println(user + " has estabished a connection to " + host);

            // Create a thread to handle this connection
            ClientHandler handler = new ClientHandler(link, user);

            // start serving this connection
            handler.start(); 
        }
        catch(IOException e){ e.printStackTrace(); }
    }
}

class ClientHandler extends Thread
{
    private Socket client;
    private String user;
    private BufferedReader in;
    private PrintWriter out;
    private static long start, finish;
    public ClientHandler(Socket s, String name)
    {
        // set up the socket
        client = s;
        user=name;
        //start the timer
        start = System.nanoTime();
        try
        {
            // Set up input and output streams for socket
            in = new BufferedReader(new InputStreamReader(client.getInputStream())); 
            out = new PrintWriter(client.getOutputStream(),true); 
        }
        catch(IOException e){ e.printStackTrace(); }
    }

    // overwrite the method 'run' of the Runnable interface
    public void run()
    {
        // Receive and process the incoming data 
        int numMessages = 0;
        try
        {
            String message = in.readLine(); 
            //create filewriter
            sya_TCPServerMT.log = new FileWriter("sy_chat.txt");

            while (!message.equals("DONE"))
            {
                System.out.println(user + ": "+ message);
                sya_TCPServerMT.log.write(user + ": "+ message+"\n");
                sya_TCPServerMT.log.flush();
                numMessages ++;
                //out.println(user + ": "+ message+"\n");   //broadcasting back
                //out.flush();
                message = in.readLine();
            }
            sya_TCPServerMT.log.close();

            // Send a report back and close the connection
            out.println("--Information Received From the Server==");
            out.flush();
            Scanner file = new Scanner(sya_TCPServerMT.chatLog);
                System.out.println();
            while(file.hasNextLine())
            {
                message = file.nextLine();
                out.println(message);
                out.flush();
                System.out.println(message);
            }
            out.println("Server received " + numMessages + " messages");
            out.flush();
 
            //get the end value of timer
            finish = System.nanoTime();
            double milliseconds,seconds,minutes,hours,val=finish-start;
                hours=Math.floor(val/(36*Math.pow(10, 11)));
                val=val%(36*Math.pow(10, 12));
                minutes=Math.floor(val/(6*Math.pow(10, 10)));
                val=val%(6*Math.pow(10, 10));
                seconds=Math.floor(val/(1*Math.pow(10, 9)));
                val=val%(1*Math.pow(10, 9));
                milliseconds=Math.floor(val/(1*Math.pow(10, 6)));
            out.println("Length of session: "+(int)hours+"::"+(int)minutes+"::"+(int)seconds+"::"+(int)milliseconds);
            out.flush();
            out.close();
            
            //if(arr.size==0)
            {
                file.close();
                sya_TCPServerMT.chatLog.delete();
            }
            //else{ other nodes see
            //out.println(this.user+" has left the chat.");}
        }
        catch(IOException e){ e.printStackTrace(); }
        finally
        {
            try
            {
                System.out.println("!!!!! Closing connection... !!!!!" );
                client.close(); 
            }
            catch(IOException e)
            {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
       }
   }
}