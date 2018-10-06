# database-optimization
An exponentially faster way to calculate SUM and GROUP BY queries by using table decompositions.

How to run: Navigate to src folder, compile and run the desired program (javac, then java <filename>)

Suppose: We have a huge database and we want to carry out certain SUM or GROUP BY queries. SUM would require O(n) time, and so would GROUP BY. Now, instead of keeping full tables with all the rows, we store only the decomposition of these tables (which may be more than 1). This results in tables being stored in much lesser space, with a worst case for SUM still of O(n) time. But, if there are any repetitions in the row values (see the datasets for the effect of decompositions), the table is condensed and the time to calculate the query decreases correspondingly. We exploit this re-usability criteria to speed up our query. We call this algorithm Cover1(SUM) and Cover2(for GROUP BY); both follow the same protocol, but the differentiation just makes for a better reading.

Information specific to this Implementation: Here, the table has rows A,B,C,D,E,F, and its decomposition is into four different tables:
{(A,B,C), (A,B,D), (A,E), (E,F)}. For comparison, Standard.java implements these queries in the normal way. Other details shown in code.

Cover Pseudocode

    //Set Up

    Get number of decompositions -> L

    Initialize L sets, S1...SL each corresponding to a sub-table (i.e. the projection of C[i] on R).

    Var sum, sum1, sum2, ... sumL-1 = 0

    //Algorithm

    Iterate through each element of S1

      If the joining rows of S1 are the same, sum += sum1 and continue to next iteration

      Else sum1=0

      Iterate through each element of S2

        If the joining rows of S2 are same, [sum, sum1] += sum2 and continue to next iteration

        Else sum2=0

        ...

        Iterate through each element of SL-1

          If the joining rows of SL-1 are same, [sum, sum1, ..., sumL-2] += sumL-1 and continue to next iteration

          Else sumL-1=0

          Iterate through each element of SL

            [sum, sum1, ..., sumL-1] += SL[i]

    //SL[i] stands for row A of ith element in table SL or could be just +=1, if counting total rows.

      ...

    Print sum
