
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.time.LocalTime;
import java.io.IOException;
import java.util.*;
import static java.time.temporal.ChronoUnit.*;

/*
INFO:
This program is capable of running two queries, i.e. Sum(1) and Sum(A).
Works on the given C.csv files for the decomposition {(A,B,C),(A,B,D),(A,E),(E,F)}
Reusing previous values rather than maintaining an extra hashtable to avoid complications.

HOW TO RUN:
1) Navigate to the practicals folder in terminal (the one containing this file)
2) Compile the program using javac Cover1.java
3) Once compiled successfully, run the program using java Cover1 _ (where _ is the query number you want to run) 
[_ has the default argument 1, so running only java Cover1 will run java Cover1 1, i.e. the first Sum(1) query]
*/

class Cover1	{

	public static void main(String[] args)	{
        //choice variable stores which query to execute; default is 1
        //NOTE: we are calculating both sum(1) and sum(A) together. this choice only decides what value to display
        int choice = args.length==0?1:Integer.parseInt(args[0]);	

        //This is the folder number being used. To use a different folder, just change 1 to the required number
		String folder = "../Dataset/1/";

        //The readers the main file and for each sub-table
        BufferedReader starter = null;
        BufferedReader ABC = null;
        BufferedReader ABD = null;
        BufferedReader AE = null;
        BufferedReader EF = null;

        //The strings storing current value of the respective sub-table
        String lineA = "";
        String lineB = "";
        String lineC = "";
        String lineD = "";

        //delim to split the input
        String cvsSplitBy = "\\|";

        //the four sets storing the four tables (we use LinkedHashSet to preserve order, which provides slight improvements, as more data is reused)
        Set<String> set1 = new LinkedHashSet<String>();
        Set<String> set2 = new LinkedHashSet<String>();
        Set<String> set3 = new LinkedHashSet<String>();
        Set<String> set4 = new LinkedHashSet<String>();

        //current time
        LocalTime currentTime = LocalTime.now();


        try {

            //SETUP PHASE: we initialize each set by going through C, and then create their iterators for the next part
            starter = new BufferedReader(new FileReader(folder+"C.csv"));
            while ((lineA = starter.readLine()) != null) {
            	String[] linesA = lineA.split(cvsSplitBy);
                set1.add(linesA[0]+"|"+linesA[1]+"|"+linesA[2]);
                set2.add(linesA[0]+"|"+linesA[1]+"|"+linesA[3]);
                set3.add(linesA[0]+"|"+linesA[4]);
                set4.add(linesA[4]+"|"+linesA[5]);
            }
            Iterator<String> it1 = set1.iterator();
            Iterator<String> it2 = set2.iterator();
            Iterator<String> it3 = set3.iterator();
            Iterator<String> it4 = set4.iterator();
            
            //SETUP PHASE ends

            //initializing values for different sub-tables
            String[] linesA = new String[3];    //ABC
            String[] linesB = new String[3];    //ABD
            String[] linesC = new String[2];    //AE
            String[] linesD = new String[2];    //AE

            //variables storing the count, i.e. sum(1), and sum(A)
            int count = 0;
            int totalA = 0;

            //variables storing previous values of count and sum(A)
            int oldTotalA = 0;
            int oldCount = 0;
            
            //iterate through all elements of first table (ABC)
            while ((it1.hasNext())) {
                lineA = it1.next();
                String[] newlinesA = lineA.split(cvsSplitBy);
                String val = newlinesA[0]+"-"+newlinesA[1];

                //check if new join value and old join value are the same (hence checking only for A,B)
                if (newlinesA[0].equals(linesA[0]) && newlinesA[1].equals(linesA[1]))   {
                    //if so, increment the count and totalA by their last value, and skip to next iteration
                    count += oldCount;
                    totalA += oldTotalA;
                    continue;
                }
                //new join value is not the same, so we will have to calculate it. reset counts of old values of count and sum(A)
                else    {
                linesA = lineA.split(cvsSplitBy);
                oldCount = 0;
                oldTotalA = 0;
                }   

                //iterate through second table (ABD)
                while (((it2.hasNext()))) {
                    lineB = it2.next();
                    linesB = lineB.split(cvsSplitBy);
                    String val2 = newlinesA[2]+"-"+linesB[2];
                    
                    //if the join values of (ABC) and (ABD) match, iterate through third table (AE)
                    while ((linesA[0].equals(linesB[0]) && linesA[1].equals(linesB[1])) && (it3.hasNext())) { 
                        lineC=it3.next();
                        String[] newlinesC = lineC.split(cvsSplitBy);
                        linesC = lineC.split(cvsSplitBy);

                        //if join values of (ABD) and (AE) match, iterate through fourth table (EF)
                        while ((linesB[0].equals(linesC[0])) && (it4.hasNext())) { 
                            lineD = it4.next();
                            linesD = lineD.split(cvsSplitBy);

                            //if join values of (AE) and (EF) match, then we have found a valid row
                            if ((linesC[1].equals(linesD[0]))) { 
                                //increment count and oldcount
                                count++;
                                oldCount++;
                                //increment sum(A) and oldsum(A)
                                totalA += Integer.parseInt(linesB[0]);
                                oldTotalA += Integer.parseInt(linesB[0]);
                            }
                        }
                        //reset the iterators for next round
                        it4 = set4.iterator();
                    }
                    it3 = set3.iterator();
                }
                it2 = set2.iterator();
            }

            //End time
            LocalTime newTime = LocalTime.now();
    
            //Display either Sum(1) or Sum(A)
            if (choice==1)  System.out.println(folder + " - " + count);
            else    System.out.println(folder + " Sum(A) - " + totalA);
            
            //Display time taken
            System.out.println("Time : "+MILLIS.between(currentTime, newTime));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ABC != null) {
                try {
                    ABC.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
