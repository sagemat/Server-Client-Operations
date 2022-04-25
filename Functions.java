import java.lang.Math;

//The functions class contains the mathematical operations and the related threads
//it is an interesting class because the start() and run() overrides
//make the code simpler to expand and implement from the main class 
public class Functions extends Thread {

    //below are the attributes used initialized with the according defaul values
    private Thread t;

    //this attribute is designed to let the main thread know that a object thread
    //instantiated in the Functions class has finished its calculations
    //That means this thread can now close since it calculeted everything it had to calculate
    private boolean finishedCalculation = false;

    //this parameter can be "2POW", "POW2", "RLOG"...
    private String function = "";


    //the result array
    private int[] result = new int[1000];
    //the size of the result array
    private int resultSize;

    //the number of this thread which also reprezents its name. Alike an ID
    private int threadNr;

    //the input list of integers we are tasked to process
    private int[] input;
    
    //gets the status of the thread - "Is it true that it finished all its calculations?"
    public boolean getStatus(){
        return finishedCalculation;
    }

    //getters
    public int[] getResult() {
        return result;
    }
    public int getThreadNr() {
        return threadNr;
    }
    public int getResultSize() {
        return resultSize;
    }

    //default constructors
    public Functions() {}
    public Functions(String function, int threadNr, int[] input, int resultSize) {
        this.threadNr = threadNr;
        this.input = input;
        this.resultSize = resultSize;
        this.function = function;
        System.out.println("Creating thread " + Integer.toString(threadNr));
    }

    //override the runnable method to implement the threads
    @Override
    public void run() {
        int sum = 1;
        try {
            for(int k = 0; k <= resultSize - 1; k++) {
                if(function.equals("2POW")) { //2^x function
                    for(int i = 1; i <= input[k + 1]; i++) {
                        sum *= 2;
                        result[k] = sum;
                    }
                    sum = 1;
                } else if(function.equals("RSQR")) { //rounded square root function
                    result[k] = (int)Math.round(Math.sqrt(input[k + 1]));
                } else if(function.equals("POW2")) {
                    result[k] = input[k + 1]*input[k + 1];
                } else if(function.equals("RLOG")) { //rounded log x function
                    result[k] = (int)Math.round(Math.log10(input[k + 1]));
                }
                //Added a delay to "ignore" the time delay of the computer and get some 
                //meaningful complexity data
                Thread.sleep(10);
            }
            //this is true when all the tasks for thread k are completed
            finishedCalculation = true;     
        } catch (InterruptedException e) {}
    }

    //override the start method to create a new thread when calling Functions with start()
    //this makes life simpler for future code development
    public void start() {
        System.out.println("Starting thread " + Integer.toString(threadNr));
        if (t == null) {
           t = new Thread(this, Integer.toString(threadNr));
           t.start();
        }
    }

    //have the Functions object terminate its thread t when the time comes
    public final void joinThread(){
        try {
            t.join();
        } catch(InterruptedException e) {}
    }

}