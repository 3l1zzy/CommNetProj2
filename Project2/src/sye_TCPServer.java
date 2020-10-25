// Multi-threaded Server program
// File name: TCPServerMT.java
// Programmer: Sarah Yaw


import java.io.*;
import java.net.*;
import java.util.*;

public class sye_TCPServer
{
    //initializing the server
    private static ServerSocket servSock;
    public static File chatLog;
    public static FileWriter log;
    public static ArrayList<ClientHandler> arr = new ArrayList<ClientHandler>();
    public static boolean canUpdate=true;
    public static int count;
    public static int numMessages = 0;
    public static void main(String[] args)
    {
        //opening port, setting up object, and running forever
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
        //server operation method
        Socket link = null; 
        try
        {
            // Put the server into a waiting state
            link = servSock.accept();

            //create file and filewriter if no other sockets (which means no file)
            if(arr.isEmpty())
            {
                chatLog = new File("sy_chat.txt");
                chatLog.createNewFile();
                sye_TCPServer.log = new FileWriter("sy_chat.txt");
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
        this.index=sye_TCPServer.count;
        sye_TCPServer.count++;
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
            //output backlog of chat to new joins
            backlog = new Scanner(sye_TCPServer.chatLog);
            String log;
            while(backlog.hasNextLine())
            {
                log = backlog.nextLine();
                this.out.println(log);
                this.out.flush();
            }   
            
            //join announcement to others
            for(int i=0; i<sye_TCPServer.arr.size();i++)
            {
                ClientHandler temp = sye_TCPServer.arr.get(i);
                if(temp.index!=this.index)
                {
                    temp.out.println(user + " has joined the chat!");
                    temp.out.flush();
                }
            }
            //join announcement in chatlog
            sye_TCPServer.log.write(user + " has joined the chat!\n");
            sye_TCPServer.log.flush();

        }
        catch(Exception e){System.out.println(e);}

        // Receive and process the incoming data 
        try
        {
            String message = this.in.readLine(); 
            while (!message.equals("DONE"))
            {
                while(!sye_TCPServer.canUpdate)
                {
                    //synchronization to prevent collisions between two clients posting at once
                    synchronized(this.lock)
                    {
                        try
                        {   
                            this.lock.wait();
                        }
                        catch(Exception e){System.out.println(e);}
                    }   
                }
                sye_TCPServer.canUpdate=false;   
                System.out.println(user + ": "+ message);
                sye_TCPServer.log.write(user + ": "+ message+"\n");
                sye_TCPServer.log.flush();
                sye_TCPServer.numMessages ++;
                //end of synchronization
                sye_TCPServer.canUpdate=true;
                synchronized(this.lock){this.lock.notifyAll();}

                //cycle and broadcast input to !this.out
                for(int i=0; i<sye_TCPServer.arr.size();i++)
                {
                    ClientHandler temp = sye_TCPServer.arr.get(i);
                    if(temp.index!=this.index)
                    {
                        temp.out.print(user + ": "+ message+"\n");   //broadcasting back
                        temp.out.flush();
                    }
                }
                message = this.in.readLine();
            }
            //client has said they're done

            // Send a report back and close the connection
            this.out.println("--Information Received From the Server--");
            this.out.flush();
            Scanner file = new Scanner(sye_TCPServer.chatLog);
            while(file.hasNextLine())
            {
                message = file.nextLine();
                this.out.println(message);
                this.out.flush();
            }
            this.out.println("Server received " + sye_TCPServer.numMessages + " messages total");
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

            //departure announcement to remaining clients
            for(int i=0; i<sye_TCPServer.arr.size();i++)
            {
                ClientHandler temp = sye_TCPServer.arr.get(i);
                if(temp.index!=this.index)
                {
                    temp.out.println(user + " has left the chat.");
                    temp.out.flush();
                }
            }
            System.out.println(this.user+" has left the chat.");

            //actual departure from server and arrayList
            sye_TCPServer.arr.remove(sye_TCPServer.arr.indexOf(this));
            sye_TCPServer.count--;

            if(sye_TCPServer.arr.isEmpty())
            {
                System.out.println("Server is empty, clearing logs...");    
                //debugging statement but I like it there^
                out.close();
                file.close();
                sye_TCPServer.chatLog.delete();
            }

        }
        catch(IOException e){ e.printStackTrace(); }
        finally
        {
            try
            {
                sye_TCPServer.log.write(this.user+" has left the chat.\n");
                //departure notification placed in chatlog
                sye_TCPServer.log.flush();
                if(sye_TCPServer.arr.isEmpty()) //if server is empty then delete the chatlog
                    sye_TCPServer.log.close();
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