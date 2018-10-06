
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
This program is capable of running Group by queries, i.e.
i) SELECT A,B,Sum(F) FROM R GROUP BY A,B; and 
ii) SELECT C,D,Sum(F) FROM R GROUP BY C,D;

It stores the answer in a Hashtable and displays it in a nicer,readable format at the end
Works on the given C.csv files for the decomposition {(A,B,C),(A,B,D),(A,E),(E,F)}
Reusing previous values rather than maintaining an extra hashtable.



HOW TO RUN:
1) Navigate to the practicals folder in terminal (the one containing this file)
2) Compile the program using javac Cover2.java
3) Once compiled successfully, run the program using java Cover2 _ (where _ is the query number you want to run) 
[_ has the default argument 1, so running only java Cover1 will run java Cover1 1, i.e. the first Group-by A,B query]
*/

class Cover2	{

	public static void main(String[] args)	{
        //choice variable stores which query to execute; default is 1
        //NOTE: we are NOT calculating both Groupby A,B and Groupby C,D together. Thus, the choice of query will have different execution times
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

        //list and list2 stores the results of Groupby A,B and Groupby C,D respectively
        Hashtable<String,Integer> list = new Hashtable<String,Integer>();
        Hashtable<String,Integer> list2 = new Hashtable<String,Integer>();

        //the innerSum hashtable is used to store the calculated sums for each (C,D) pairs (i.e. C-D acts a key, and the calculated sum is its corresponding value). This is because the two variables belong to different sub-tables, and it becomes much easier and faster to manage this grouped value in a hashtable. Clearly, this is only used in Groupby (C,D) query.
        Hashtable<String,Integer> innerSum = new Hashtable<String,Integer>();


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
            String[] linesA = new String[3];
            String[] linesB = new String[3];
            String[] linesC = new String[2];
            String[] linesD = new String[2];

            //variable storing count of f and oldF
            int f=0, oldF=0;
            
            //iterate through all elements of first table (ABC)
            while ((it1.hasNext())) {
                lineA = it1.next();
                String[] newlinesA = lineA.split(cvsSplitBy);
                String val = newlinesA[0]+"-"+newlinesA[1];

                //if grouping by (A,B), then check if the current value is the same as last value
                if (choice==1)  {
                    if (newlinesA[0].equals(linesA[0]) && newlinesA[1].equals(linesA[1]))   {
                        //Update the current value of (A,B) in the hashtable by adding the last seen sum (of F) to it, and skipping to next iteration
                        list.put(val,list.get(val)+oldF);      
                        continue;
                    }
                    //otherwise we will have to calculate the value of this group. reset the value of F as well.
                    else    {
                    linesA = lineA.split(cvsSplitBy);
                    oldF=0;
                    }   
                }

                //if not grouping by (A,B), then we are grouping by (C,D), in which case we will have to calculate the values for all the (A,B) groups (since C,D haven't been encountered yet)
                else    {
                    linesA = lineA.split(cvsSplitBy);
                    oldF=0;
                }

                //For this (A,B) group, iterate over elements of second table (A,B,D)
                while (((it2.hasNext()))) {
                    lineB = it2.next();
                    linesB = lineB.split(cvsSplitBy);
                    String val2 = newlinesA[2]+"-"+linesB[2];
                    
                    //FOR Q4
                    //If we are grouping (C,D), then there is a chance to reuse some values now that we have a dedicated (C,D) group. We check if the current join-values are the same as last seen. But along with that, we also check if we actually have a stored sum for that group, by checking innerSum 
                    if (choice==2)  {
                        if ((linesA[0].equals(linesB[0]) && linesA[1].equals(linesB[1])) && innerSum.get(val2)!=null)    {
                            //If so, we can reuse that value from innerSum by adding that to our original hashtable list2, under the correct group. We skip to the next iteration.
                            list2.put(val2, list2.get(val2)+innerSum.get(val2));
                            continue;
                        }
                    //Otherwise we will have to calculate values for this group, so reset oldF
                    oldF=0;
                    }
                    
                    //If the join attributes match, then we iterate through third table (AE)
                    while ((linesA[0].equals(linesB[0]) && linesA[1].equals(linesB[1])) && (it3.hasNext())) { 
                        lineC=it3.next();
                        String[] newlinesC = lineC.split(cvsSplitBy);
                        linesC = lineC.split(cvsSplitBy);

                        //FOR Q4
                        //This is another reusing step. If the join attributes match, we check if we have already done this calculation before by checking if innerSum has a value for that or not.
                        if (choice==2)  {
                            if ((linesB[0].equals(linesC[0])) && innerSum.get(val2)!=null)   {
                                //If so, we can just reuse that innerSum value, and update our list2 hashtable, AND the oldF value, and skip to next iteration. The updation of oldF is important, because this oldF will be reused later at the top level, and thus, needs to be in sync, sum-wise.
                                list2.put(val2,list2.get(val2)+innerSum.get(val2));
                                oldF+=innerSum.get(val2);
                                continue;
                            }
                        }

                        //If the join attributes match, then iterate through fourth table (EF)
                        while ((linesB[0].equals(linesC[0])) && (it4.hasNext())) { 
                            lineD = it4.next();
                            linesD = lineD.split(cvsSplitBy);

                            //If the join attributes match, then we have found a valid row
                            if ((linesC[1].equals(linesD[0]))) { 
                                
                                //we update the oldF sum to include the current F value.
                                f = Integer.parseInt(linesD[1]);
                                oldF += f;

                                //Depending on the query being executed, we update the relevant Hashtables. If they are empty, then we create a new key, and enter the value of f.
                                //FOR Q3
                                if (choice==1)  {
                                    if (list.get(val)!=null)  {
                                        list.put(val,list.get(val)+f);
                                    }
                                    else    {
                                        list.put(val,f);
                                    }
                                }
                                //FOR Q4
                                else    {
                                    if (list2.get(val2)!=null)  {
                                        list2.put(val2,list2.get(val2)+f);
                                    }
                                    else    {
                                        list2.put(val2,f);
                                    }
                                }
                            }
                        }
                        //Reset the iterators
                        it4 = set4.iterator();
                        //This is an important step. We update the innerSum of the just calculated (C,D) group, so that this can be reused later.
                        if (choice==2)  innerSum.put(val2,oldF);
                    }
                    it3 = set3.iterator();
                }
                it2 = set2.iterator();
            }


            LocalTime newTime = LocalTime.now();
            
            //pretty print the relevant table
            if (choice==1)  {
                Set<String> keys = list.keySet();
                List<String> sorted = new ArrayList<>();
                for(String p : keys) {
                    sorted.add(p);
                }
                Collections.sort(sorted);
                for (String key: sorted)
                    System.out.println(key + "  |  " + list.get(key));
            }

            else    {
                Set<String> keys = list2.keySet();
                List<String> sorted = new ArrayList<>();
                for(String p : keys) {
                    sorted.add(p);
                }
                Collections.sort(sorted);
                for (String key: sorted)
                    System.out.println(key + "  |  " + list2.get(key));
            }

            //Display the time taken
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
