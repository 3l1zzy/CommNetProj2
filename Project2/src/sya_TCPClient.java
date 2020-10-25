// Programmer: Sarah Yaw
// Client program
// File name: TCPClient.java


import java.io.*;
import java.net.*;
import java.util.*;
public class sya_TCPClient
{
    private static InetAddress host;
    private static String input="";
    public static boolean closing=false;
    public static void main(String[] args)
    {
        int port = 0;
        String user = "";
        boolean hasHost=false, hasPort=false, hasUser=false;
        int hostIndex=0, portIndex=0, userIndex=0; 
        Scanner keyb = new Scanner(System.in);
        try
        {
            if(args.length>0)
            {
                for(int i=0; i<args.length;i++)
                {
                    if(args[i]==null)
                        input=input+args[i];
                    else
                        input = input+args[i]+" ";
                    if(args[i].equals("-h"))
                    {
                        hasHost=true;
                        hostIndex = i+1;
                    }
                    if(args[i].equals("-p"))
                    {
                        hasPort=true;
                        portIndex = i+1;
                    }
                    if(args[i].equals("-u"))
                    {
                        hasUser=true;
                        userIndex = i+1;
                    }
                }
                    
                // Get server IP-address
                if(hasHost)
                {
                    host = InetAddress.getByName(args[hostIndex]);
                }
                else
                {
                    host = InetAddress.getLocalHost();
                }
                
                
                //Get Port
                if(hasPort)
                {
                    port = Integer.parseInt(args[portIndex]);
                }
                else
                {
                    port = 20700;
                }
                
                // Get username
                if(hasUser)
                {
                    user = args[userIndex];
                }
                else
                {
                    System.out.print("Please enter a username: ");
                    user = keyb.next();
                }
            }
            else if (args.length==0) //if the command line is left empty of arguments aside from running the client
            {
                //host
                host = InetAddress.getLocalHost();
                //port
                port = 20700;
                //username
                System.out.print("Please enter a username: ");
                user = keyb.next();
            }
        }
        catch(UnknownHostException e)
        {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        runn(port, user); //port
    }

    private static void runn(int port, String user)
    {
        Socket link = null;
        try
        {
            // Establish a connection to the server
            link = new Socket(host,port);

            // Set up input and output streams for the connection
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(
                link.getOutputStream(),true); 
            
            //send username before anything else
            out.write(user);
            out.write("\n");
            out.flush();
     
            //Set up stream for keyboard entry
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
            String message, response;

            //communication to and from server
            Thread scComm=new Thread(new ServConsole(in));
            Thread usComm=new Thread(new UserServer(out, userEntry));
            //starts
            scComm.start();
            usComm.start();

            //joins - only receives at this point and only gets server exit report
            try
            {
                scComm.join();
                usComm.join();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            // Receive the final report and close the connection
            message = in.readLine();
            response = message;
            System.out.println("\r"+response+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" ");
            do
            {
                message = in.readLine();
                if (message==null){}
                else
                {
                    response = message;
                    System.out.println(response);
                }
            }while(message!=null && !message.substring(0,3).equals("Serve"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        finally
        {
            try
            {
                System.out.println("\n!!!!! Closing connection... !!!!!");
                link.close();
            }

           catch(IOException e)
           {
               System.out.println("Unable to disconnect!");
               System.exit(1);
           }
        }
    }
}
class ServConsole extends Thread
{
    private BufferedReader fromServ;
    //console output is system.out
    private String response;
    public ServConsole(BufferedReader sbr)
    {
        fromServ=sbr;
    }
    @Override
    public void run()
    {
        // Receive data from the server
        while(!sya_TCPClient.closing)
        {
            try
            {
                response = fromServ.readLine();
                if (response!=null)
                {
                    System.out.println("\r"+response+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" ");
                    System.out.print("Enter message: ");
                }
            }
            catch(Exception e){System.out.println(e);}
        }
        
    }
}
class UserServer extends Thread
{
    private BufferedReader fromUser;
    private PrintWriter toServ;
    private String message;
    UserServer(PrintWriter pw, BufferedReader cbr)
    {
        fromUser=cbr;
        toServ=pw;
    }
    @Override
    public void run()
    {
        // Get data from the user and send it to the server
        do
        {
            try
            {
                System.out.print("Enter message: ");
                message = fromUser.readLine();
                toServ.println(message);
            }
            catch(Exception e){System.out.println(e);}
        }while (!message.equals("DONE"));
        sya_TCPClient.closing=true;
    }
}