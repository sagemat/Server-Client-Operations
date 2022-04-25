import java.net.*;
import java.io.*;

public class Server {

    //these attributes define the finalResult array that is to be displayed to the client and the size of that array
    private static int[] finalResult = new int[1000];
    private static int finalResultSize = 0;

    //the listUnion method unites the results from the threads in a meaningful way
    public static void listUnion(int[] result, int resultSize) {
        for(int i = 0; i <= resultSize - 1; i++) {
            finalResult[finalResultSize] = result[i];
            finalResultSize++;
        }
    }

    //this is the server version which partitions threads tasks evenly
    public static int[] tasksPerThread(int numberOfTasks, int numberOfThreads) {

        int[] valuesPerThread = new int[1000];
        int valuePerThread = (int) Math.floor(numberOfTasks / numberOfThreads);
        int remainder = numberOfTasks % numberOfThreads;
        for(int i = 0; i <= numberOfThreads - 1; i++){
            valuesPerThread[i] = valuePerThread;
            if(remainder > 0) {
                remainder--;
                valuesPerThread[i]++;
            }
        }
        return valuesPerThread;
    }

    //Here we concatenate the values back into a final string that is to be sent to the client
    static String formattedResult(int[] parsedArgs) {
        String outputString = "Your feedback is:";
        for(int t = 0; t <= finalResultSize - 1; t++) {
            outputString += " " + String.valueOf(finalResult[t]);
        }
        return outputString;
    }

    //the attribute computeThreads is tightly linked with all the other functions in the code
    //it is like an intersection where all the processing functions join
    static void computeThreads(String function, int numberOfThreads, int[][] threadsTasks, int[] nrThreadsTasks) {
        Functions threadComputation[] = new Functions[50];
        int resultSize = 0;
        int result[] = new int[1000];
        for(int k = 0; k <= numberOfThreads - 1; k++) { 
            // create the kth thread with the following set of parameters
            threadComputation[k] = new Functions(function, k, threadsTasks[k], nrThreadsTasks[k]);
            threadComputation[k].start(); 
        }
        for(int k = 0; k <= numberOfThreads - 1; k++) {
            for(int t = 0; t <= nrThreadsTasks[k]; t++) { 
                //this for loop might seem redundat, but without it we might never get the 
                //output we want in time. This way we assure
                result = threadComputation[k].getResult(); 
        //that we get the desired result eventually in this aproximated nrThreadsTasks tries
                resultSize = threadComputation[k].getResultSize(); 
            }
            if(threadComputation[k].getStatus()) { 
                //we check if the calculation has completed in that thread
                listUnion(result, resultSize); 
                //if so unite this present result to the final result
                threadComputation[k].joinThread(); //and close the thread
            }
            else
                k--; //otherwise try again
        }

        //in this for loop we just display on the server console what each thread computed
        for(int k = 0; k <= numberOfThreads - 1; k++) {
            System.out.print("Thread ");
            System.out.print(k);
            System.out.print(" found the results:");
            for(int i = 0; i <= resultSize - 1; i++) {
                System.out.print(" ");
                System.out.print(result[i]);
            }
            System.out.println();
        }
    }

	public static void main(String[] args) throws IOException {
        //We start counting the execution time for the code

		int portNumber;

        if (args.length < 1) {
			System.out.println("Warning: You have provided no arguments\nTrying to connect to the default port 8000...");
			portNumber = 8000;
        } else if (args.length == 1) {
			portNumber = Integer.parseInt(args[0]);
		} else {
			System.out.println("Warning: You have provided > 1 arguments\nTrying with the first argument to connect to a port...");
			portNumber = Integer.parseInt(args[0]);
		}
		while(true){ //in order to serve multiple clients but sequentially, one after the other
			try (
                ServerSocket myServerSocket = new ServerSocket(portNumber);
                Socket aClientSocket = myServerSocket.accept();
				PrintWriter output = new PrintWriter(aClientSocket.getOutputStream(),true);
				BufferedReader input = new BufferedReader(new InputStreamReader(aClientSocket.getInputStream()));
			) {
				System.out.println("Connection established with a new client with IP address: " + aClientSocket.getInetAddress() + "\n");
				output.println("Client " + aClientSocket.getInetAddress() + ". This is server " + myServerSocket.getInetAddress() +
				" speaking. The functions you can use are: 'POW2' <=> x^2 | '2POW' <=> 2^x | 'RLOG' <=> (rounded) log x | 'RSQR' <=> (rounded) sqrt x");

                //read the input and separate the spaces, put all the strings parsed to int in an array
				String inputLine = input.readLine();
                String[] listOfArgs = inputLine.split(" ");
                int[] parsedArgs = new int[1000];
                long startTime = System.currentTimeMillis();
                for(int i = 1; i <= listOfArgs.length - 1; i++) {
                    parsedArgs[i] = Integer.parseInt(listOfArgs[i]);
                }
                //each thread holds a quarter of the number of argumets. If there are less than four arguments,
                //only the first few(1st, 2nd, and/or 3rd) threads hold a value and process it evenly.
                //If there is a number which is not divisible by 4, the remainder goes to the first thread.

                int numberOfTasks = listOfArgs.length - 1; // the number of tasks is the number of args - 1. Because the first arg is the operation

				System.out.println("Received a new message from client " + aClientSocket.getInetAddress());
				System.out.println("Client says: " + inputLine);
				output.println("Server says: Your message has been successfully received! Please wait for the feedback...");

                //the number of threads you want to use in your simulations
                int numberOfThreads = 5; //this can be modified to any natural* number

                int[] nrThreadsTasks = tasksPerThread(numberOfTasks, numberOfThreads);
                int[][] threadsTasks = new int[1000][1000];

                int m = 1;
                for(int k = 0; k <= numberOfThreads - 1; k++)
                    for(int t = 1; t <= nrThreadsTasks[k]; t++) {
                        threadsTasks[k][t] = parsedArgs[m];
                        m++;
                        // this is presenting each task to each thread
                    }
                
                computeThreads(listOfArgs[0], numberOfThreads, threadsTasks, nrThreadsTasks);

                
                //now we put the result in the desired format before we send it
                String outputString = formattedResult(parsedArgs);
                System.out.println(outputString);
                //we send the formatted result to the client
                output.println(outputString);

                long endTime   = System.currentTimeMillis();
                long totalTime = endTime - startTime;

                String outputTimeString = "Your execution time is: " + String.valueOf(totalTime) + "ms";
                output.println(outputTimeString);
                
				System.out.println("Connection with client " + aClientSocket.getInetAddress() + " is now closing...\n");

			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
				+ portNumber + " or listening for a connection");
				System.out.println(e.getMessage());
			}
		}
	}
}