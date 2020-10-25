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
    public static ArrayList<ClientHandler> arr = new ArrayList<ClientHandler>();
    public static boolean canUpdate=true;
    public static int count;
    public static int numMessages = 0;
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

            //create file if no other sockets (which means no file)
            if(arr.isEmpty())
            {
                chatLog = new File("sy_chat.txt");
                chatLog.createNewFile();
                //create filewriter
                sya_TCPServerMT.log = new FileWriter("sy_chat.txt");
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
            arr.add(handler);

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
    public PrintWriter out;
    public int index;
    public Object lock;
    private static long start, finish;
    public ClientHandler(Socket s, String name)
    {
        // set up the socket
        client = s;
        user=name;
        this.index=sya_TCPServerMT.count;
        sya_TCPServerMT.count++;
        lock = new Object();

        //start the timer
        start = System.nanoTime();
        try
        {
            // Set up input and output streams for socket
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream())); 
            this.out = new PrintWriter(client.getOutputStream(),true); 
        }
        catch(IOException e){ e.printStackTrace(); }
    }

    // overwrite the method 'run' of the Runnable interface
    public void run()
    {
        Scanner backlog;
        //new join bookkeeping
        try
        {
            //output backlog of chat
            backlog = new Scanner(sya_TCPServerMT.chatLog);
            String log;
            while(backlog.hasNextLine())
            {
                log = backlog.nextLine();
                this.out.println(log);
                this.out.flush();
            }   
            
            //join announcement
            for(int i=0; i<sya_TCPServerMT.arr.size();i++)
            {
                ClientHandler temp = sya_TCPServerMT.arr.get(i);
                if(temp.index!=this.index)
                {
                    temp.out.println(user + " has joined the chat!");   //broadcasting back
                    temp.out.flush();
                }
            }
            sya_TCPServerMT.log.write(user + " has joined the chat!\n");
            sya_TCPServerMT.log.flush();

        }
        catch(Exception e){System.out.println(e);}

        // Receive and process the incoming data 
        try
        {
            String message = this.in.readLine(); 
            while (!message.equals("DONE"))
            {
                while(!sya_TCPServerMT.canUpdate)
                {
                    //see if canUpdate
                    synchronized(this.lock)
                    {
                        try
                        {   
                            this.lock.wait();
                        }
                        catch(Exception e){System.out.println(e);}//+" @122");}
                    }   
                }
                sya_TCPServerMT.canUpdate=false;   
                System.out.println(user + ": "+ message);
                sya_TCPServerMT.log.write(user + ": "+ message+"\n");
                sya_TCPServerMT.log.flush();
                sya_TCPServerMT.numMessages ++;
                //end of synch
                sya_TCPServerMT.canUpdate=true;
                synchronized(this.lock){this.lock.notifyAll();}

                //cycle and broadcast to !this.out
                for(int i=0; i<sya_TCPServerMT.arr.size();i++)
                {
                    ClientHandler temp = sya_TCPServerMT.arr.get(i);
                    if(temp.index!=this.index)
                    {
                        temp.out.print(user + ": "+ message+"\n");   //broadcasting back
                        temp.out.flush();
                    }
                }
                message = this.in.readLine();
                
            }

            // Send a report back and close the connection
            this.out.println("--Information Received From the Server==");
            this.out.flush();
            Scanner file = new Scanner(sya_TCPServerMT.chatLog);
            while(file.hasNextLine())
            {
                message = file.nextLine();
                this.out.println(message);
                this.out.flush();
            }
            this.out.println("Server received " + sya_TCPServerMT.numMessages + " messages total");
            this.out.flush();
 
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
            this.out.println("Length of session: "+(int)hours+"::"+(int)minutes+"::"+(int)seconds+"::"+(int)milliseconds);
            this.out.flush();

            //departure announcement
            for(int i=0; i<sya_TCPServerMT.arr.size();i++)
            {
                ClientHandler temp = sya_TCPServerMT.arr.get(i);
                if(temp.index!=this.index)
                {
                    temp.out.println(user + " has left the chat.");   //broadcasting back
                    temp.out.flush();
                }
            }
            System.out.println(this.user+" has left the chat.");

            //actual departure
            sya_TCPServerMT.arr.remove(sya_TCPServerMT.arr.indexOf(this));
            sya_TCPServerMT.count--;

            if(sya_TCPServerMT.arr.isEmpty())
            {
                System.out.println("Server is empty, clearing logs..."); //debugging statement but I like it there
                out.close();
                file.close();
                sya_TCPServerMT.chatLog.delete();
            }

        }
        catch(IOException e){ e.printStackTrace(); }
        finally
        {
            try
            {
                sya_TCPServerMT.log.write(this.user+" has left the chat.\n");
                sya_TCPServerMT.log.flush();
                if(sya_TCPServerMT.arr.isEmpty())
                    sya_TCPServerMT.log.close();
                this.client.close(); 
            }
            catch(IOException e)
            {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
       }
   }
}