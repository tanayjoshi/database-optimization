
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.*;


/*
INFO:
This program is a representation of StandardDB capable of running all four queries.

NOTE: we always calculate sum(1) and sum(A), because they don't affect the time much. Depending on the choice, we may also calculate either list (for Q3) or list2 (for Q4)
Works on the given R.csv files.


HOW TO RUN:
1) Navigate to the practicals folder in terminal (the one containing this file)
2) Compile the program using javac Standard.java
3) Once compiled successfully, run the program using java Standard _ (where _ is the query number you want to run) 
[_ has the default argument 1, so running only java Standard will run java Standard 1, i.e. the first Sum(1) query]
*/

class Standard	{

	public static void main(String[] args)	{
        //choice variable stores which query to execute; default is 1		
        int choice = args.length==0?1:Integer.parseInt(args[0]);  

        //This is the folder number being used. To use a different folder, just change 1 to the required number
		String folder = "../Dataset/1/R.csv";
        BufferedReader ABC = null;
        BufferedReader ABD = null;
        BufferedReader AE = null;
        BufferedReader EF = null;       
        String lineA = "";
        String lineB = "";
        String lineC = "";
        String lineD = "";
        String cvsSplitBy = "\\|";
        
        int count = 0, a = 0, f;
        
        //start time
        LocalTime currentTime = LocalTime.now();
        
        //hashtables for groupby queries
        Hashtable<String,Integer> list = new Hashtable<String,Integer>();
        Hashtable<String,Integer> list2 = new Hashtable<String,Integer>();


        try {

            ABC = new BufferedReader(new FileReader(folder));
            ABD = new BufferedReader(new FileReader(folder));
            AE = new BufferedReader(new FileReader(folder));
            EF = new BufferedReader(new FileReader(folder));

            lineB = ABD.readLine();
            lineC = AE.readLine();
            lineD = EF.readLine();

            String[] linesB = lineB.split(cvsSplitBy);
            String[] linesC = lineC.split(cvsSplitBy);
            String[] linesD = lineD.split(cvsSplitBy);

            //We loop through the entire table R, and calculate the relevant quantities on the fly. For group-by queries, we use hashtables instead of sorting. We use the group-by attributes as the key, the keep on updating its associated values.
            while ((lineA = ABC.readLine()) != null) {
            	String[] linesA = lineA.split(cvsSplitBy);
                count ++;
                a += Integer.parseInt(linesA[0]);
                f = Integer.parseInt(linesA[5]);

                if (choice==3)  {
                    String val = linesA[0]+"-"+linesA[1];
                    if (list.get(val)!=null)  {
                        int key = (int) list.get(val);
                        list.put(val,key+f);
                    }
                    else    {
                        list.put(val,f);
                    }
                }

                if (choice==4)  {
                    String val2 = linesA[2]+"-"+linesA[3];
                    if (list2.get(val2)!=null)  {
                        int key = (int) list2.get(val2);
                        list2.put(val2,key+f);
                    }
                    else    {
                        list2.put(val2,f);
                    }
                }
            }

            //end time
            LocalTime newTime = LocalTime.now();

            //print the relevant output
            if (choice==1)  System.out.println(folder + " Sum(1) - " + count);
            else if (choice==2) System.out.println(folder + " Sum(A) - " + a);
            else if (choice==3)  {
                Set<String> keys = list.keySet();
                List<String> sorted = new ArrayList<>();
                for(String p : keys) {
                    sorted.add(p);
                }
                Collections.sort(sorted);
                for (String key: sorted)
                    System.out.println(key + "  |  " + list.get(key));
            }

            else if (choice==4)    {
                Set<String> keys = list2.keySet();
                List<String> sorted = new ArrayList<>();
                for(String p : keys) {
                    sorted.add(p);
                }
                Collections.sort(sorted);
                for (String key: sorted)
                    System.out.println(key + "  |  " + list2.get(key));
            }
            else System.out.println("Wrong choice");

            //print the time taken
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
