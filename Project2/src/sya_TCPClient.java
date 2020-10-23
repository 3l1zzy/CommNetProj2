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
                    //System.out.println("-h found");
                    host = InetAddress.getByName(args[hostIndex]);
                }
                else
                {
                    //System.out.println("default host");
                    host = InetAddress.getLocalHost();
                }
                
                
                //Get Port
                if(hasPort)
                {
                    //System.out.println("-p found");
                    port = Integer.parseInt(args[portIndex]);
                }
                else
                {
                    //System.out.println("default port");
                    port = 20700;
                }
                
                // Get username
                if(hasUser)
                {
                    //System.out.println("-u found");
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
                //System.out.println("Empty command line");
                //host
                host = InetAddress.getLocalHost();
                //port
                port = 20700;
                //username
                System.out.print("Please enter a username: ");
                user = keyb.next();
            }
            //System.out.println("--------");
            //System.out.println("Host: "+host);
            //System.out.println("User: "+user);
            //System.out.println("Port: "+port);
            //System.out.println("--------");
        }
        catch(UnknownHostException e)
        {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        run(port, user); //port
    }

    private static void run(int port, String user)
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

            // Get data from the user and send it to the server
            do
            {
                System.out.print("Enter message: ");
                message = userEntry.readLine();
                out.println(message);
            }while (!message.equals("DONE"));

            // Receive the final report and close the connection
            message = in.readLine();
            response = message;
            System.out.println(response);
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
            //response = in.readLine();
            //System.out.println(response);
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