/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ThreadPoolExecutor;

import static threads.Threads.*;

public class LinkChecker implements Runnable {


    public static double startTime; // Elapsed time until analysis starts (in milliseconds)
    public static double endTime; // Elapsed time until analysis ends (in milliseconds)
    public static double executionTime;
    /*  Execution time is calculated by the following formulae:
     *         (in seconds) :  ( endTime - startTime ) / 1000
     *         (in minutes) :  ( ( endTime - startTime) / 1000 ) / 60
     */

    public int depth; // Represents the depth level being analysed by the task
    public String url; // Holds the input  url

    public static int innerValid = 0; // Holds the amount of valid links detected
    public static int innerInvalid = 0; // Holds the amount of invalid links detected
    public static int linkNumber = -1; // Holds the number of the depth-zero link in which the analysis is taking place


    // Constructor for runnable linkChecker instance
    public LinkChecker(String link, int d) {
        url = link;
        depth = d;
    }

    // Overridden run method for linkChecker instance
    @Override
    public void run() {

        if (depth > 0) { // Base condition for recursion
            linkNumber++;
            String temp = ""; // Holds url temporarily for checking
            boolean flag = false; // flag is true when a link is valid. Directs the program to call the executorService recursively
            Document tempDoc = null;

            try {

                // Parse html file and store in links list.
                tempDoc = (Document) Jsoup.connect(url).get();
                Elements links = tempDoc.select("a[href]");

                // Loop on the links sequentially
                for (Element link : links) {

                    if (linkNumber > 0) { // Checking at depth > 0
                        System.out.println("Link #" + (linkNumber - 1));
                    } else { // Checking at depth 0
                        System.out.println("Main Link");
                    }

                    System.out.println(Thread.currentThread().getName()); // Print name of thread handling the respective link
                    System.out.println("\033[32m" + innerValid + " \033[31m " + innerInvalid + "\033[0m");
                    flag = false; // Initialize flag
                    String tempUrl = url;

                    temp = link.attr("href");

                    // Check correct syntax for the url if correct, start with validations
                    if (temp.startsWith("https://")) {
                        // Validate link
                        if (Validations.isValid(link.attr("href")) && Validations.isValid2(link.attr("href"))) {
                            flag = true;
                            innerValid++;

                            System.out.println("\033[32mVALID \033[0m");

                        } else if (!Validations.isValid(link.attr("href")) || !Validations.isValid2(link.attr("href"))) {
                            innerInvalid++;
                            System.out.println("\033[31mINVALID \033[0m");

                        }
                        System.out.println("Link: " + link.attr("href"));
                        System.out.println("Text: " + link.text() +"\n");

                        if (flag) {

                            if (numberOfThreads > 1) { // Utilize thread pool in case of multi-threaded analysis
                                Threads.executorService.execute(new LinkChecker(temp, depth - 1));

                            } else { // Proceed sequentially in case of single-threaded analysis
                                LinkChecker deeperLinksChecker = new LinkChecker(temp, depth - 1);
                                deeperLinksChecker.run();
                            }
                        }

                    } else { // Incorrect url syntax
                        String tempDomain = Validations.getDomain(url);
                        tempUrl = Validations.combine(tempDomain, temp); // Fix the url by adding " https:// "

                        // Validate the link
                        if (Validations.isValid(tempUrl) && Validations.isValid2(tempUrl)) {
                            innerValid++;
                            System.out.println("\033[32mVALID (else)\033[0m");
                            flag = true;

                        } else if (!Validations.isValid(tempUrl) || !Validations.isValid2(tempUrl)) {
                            innerInvalid++;
                            System.out.println("\033[31mINVALID (else)\033[0m");
                        }
                        System.out.println("Link: " + tempUrl);
                        System.out.println("Text: " + link.text() + "\n");

                        if (flag) {

                            if (numberOfThreads > 1) { // Utilize thread pool in case of multi-threaded analysis
                                Threads.executorService.execute(new LinkChecker(tempUrl, depth - 1));

                            } else { // Proceed sequentially in case of single-threaded analysis
                                LinkChecker deeperLinksChecker = new LinkChecker(tempUrl, depth - 1);
                                deeperLinksChecker.run();
                            }

                        }

                    }

                }

                // In case of multi-threaded analysis only. If only 1 thread in the pool is active, initiate executorService shutdown
                if (Threads.numberOfThreads > 1 && ((ThreadPoolExecutor) executorService).getActiveCount() == 1) {
                    executorService.shutdown();
                }

            } catch (Exception ex) {
                // Catches any exceptions thrown by the analysis block
            }

        }

    }

}
