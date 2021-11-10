package threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Threads {

    public static ExecutorService executorService;  // Executor Service to create the thread pool for multi-threaded analysis
    public static int numberOfThreads; // User input
    public static int depth; // User input
    public static String inputUrl; // User input
    public static boolean analysisOnline = false;

    public static void main(String[] args) {

        new Start().setVisible(true);
    }

    public static void analyseLinks(){

        analysisOnline = true;

        String url = inputUrl;

        String s = "s";
        if (numberOfThreads == 1) {
            s = "";
        }
        System.out.println("Initiating \033[36m" + numberOfThreads + "\033[0m thread" + s + " with depth = \033[36m" + (depth - 1));
        System.out.println("\033[0m\n");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }

        //Initiate fixed thread pool with the inputted amount of threads.
        executorService = Executors.newFixedThreadPool(numberOfThreads);

        //Get elapsed time until the start of execution
        LinkChecker.startTime = (double) System.currentTimeMillis();

        //Execute at depth 0;
        if (numberOfThreads > 1) {
            Threads.executorService.execute(new LinkChecker(url, depth));
        } else {
            LinkChecker linkChecker = new LinkChecker(url, depth);
            linkChecker.run();
        }

    // Delay the main thread until the thread pool is terminated
    if(numberOfThreads > 1){
        while (true) {
            if (executorService.isTerminated()) {
                break;
            }
        }
    }

    //Get elapsed time until the end of execution
    LinkChecker.endTime  = (double) System.currentTimeMillis();
    analysisOnline = false;

    LinkChecker.executionTime = (LinkChecker.endTime - LinkChecker.startTime) / 1000;

    System.out.println (
    "Done!\nAnalysis executed with " + numberOfThreads
                    + " threads in " + LinkChecker.executionTime / 60 + " minutes(" + LinkChecker.executionTime + " seconds)");

        }
    
    
    }

