Creator: Sarah Yaw
Project Description:
	Multithreaded client and server programs. The server is able to support multiple clients coming and going. As long as there is at least one client, the server will update any incoming clients to the chats that happened before that specific arrival. The server will broadcast arrivals and departures of clients as well as their messages.
Run Instructions:
	1)  Open up the terminal window.
	2)  Type "cd "and then the file path to where the sye_TCPServer.java and sye_TCPClient.java files are located and hit enter.
			Example: cd Desktop/school/s1/CommNet/Project2
	3)  Type "javac sye_TCPServer.java" in the terminal and hit enter. This compiles the program and adds a file called sye_TCPServer.class to your folder.
	4)  Type "java sye_TCPServer" and type in the port number then hit enter. Your server will now be running.
			Example: java sye_TCPServer 20700
			Note: If running on Google Cloud the run command will be "nohup java sye_TCPServer 20700 &"
	5)  Open a second terminal window. It is imperative you do not close the one you have started the server on.
	6)  Type "cd "and then the file path to where the sye_TCPServer.java and sye_TCPClient.java files are located and hit enter.
			Example: cd Desktop/school/s1/CommNet/Project2
	7)  Type "javac sye_TCPClient.java" in the terminal and hit enter. This compiles the program and adds a file called sye_TCPClient.class to your folder.
	8)  Type "java sye_TCPClient" and type in the credentials required. These include the host, port number, and username. Each of these three are optional, but you will be prompted to type in the username if you do not include it here. If the other two values are missing, the program will alert the user and terminate. Each of these values needs a key to be recognized as well. -h for host, -p for port, and -u for user. If an incorrect key is used, the program will complain and terminate.
			Example: java sye_TCPClient -h hostName -u userName -p portName
	9)  You are now connected to the server and should be able to interact with it. Send whatever messages you want by typing them in and hitting enter to send.
	10) To disconnect form the server type in "DONE" and hit enter. This must be its own message.'
Conclusion: 
		I estimate this took 36 hours to complete. I started this around the 19th, but I had also woken up that day with an external ear infection which turned quite severe. The pain from this made it rather difficult to concentrate, and I actually re-started the project once the pain began to subside around the 22nd. Because of all of this, it took me a bit longer to think through what needed to be done and to implement it effectively than it would have if I were not ill.
                The most difficult part for me was really just breaking the project down into its basic blocks. My thought process was really foggy so my comments in my code may sound a little weird. I think I spent the most time figuring out where exactly to break the end product into chunks. I had a tendency to think things like broadcasting to other clients was going to be a lot harder than it really was, but again, I think it was just because of pain and medication I was on (steroids make it a little hard for me to think)
		I'm happy to report that my program works really well! I'm actually going to try to get one of my friends to set it up on their computer. I want to see how it handles that. I also think it might be a fun side project if I were to make a rough GUI so each client had a color to their text. The only place I deviated from the rubric though that I'm aware of is line 221 of the server program. I added a print statement to the server console that announces when there are no clients connected. I had it in there to debug and make sure it was going into the chatlog clearing loop, but I quite like being able to easily see if the file is there or not without another console command.
		I think this assignment worked out pretty well. I don't really have any changes I would make. Had I known that I was going to get sick, I might have pulled a few more late/all nighters to start this assignment earlier so I could rest, but I'm pretty proud of this either way.