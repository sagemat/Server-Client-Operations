import java.net.*;
import java.io.*;

public class Client {
	public static void main(String[] args) throws IOException {
		
        if (args.length != 2) {
            System.err.println(
                "Usage: java HelloClient <host name> <port number>");
            System.exit(1);
        }

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		
		try (
			Socket myClientSocket = new Socket(hostName, portNumber);
			//These are the two approaches I tried for the timeout.
			//However due to unforseen technical errors I could not figure it out. Hope this is fine
			//this is one method
			//myClientSocket.setSoTimeout(30000);
			//or this
			//myClientSocket.connect((new InetSocketAddress(hostName, portNumber)), 300000);
			PrintWriter output = new PrintWriter(myClientSocket.getOutputStream(),true);
			BufferedReader input = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		) {
			String userInput;
			System.out.println(input.readLine()); // reads the first message from the server and prints it
			System.out.println("Enter the condition and then the sequence (such as: 2POW 10 8 6 4 2)");
			userInput = stdIn.readLine(); // reads user's input
			output.println(userInput); // user's input transmitted to server
			System.out.println(input.readLine()); // reads server's output result
			System.out.println(input.readLine()); // reads the server's output time
			System.out.println(input.readLine());
			System.out.println("-----------End of communication-----------");
			System.out.println("\nCommunication with server " + hostName + " was successful! Now closing...");
			
		} catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
			e.printStackTrace();
            System.exit(1);
        }
	
	}
}