import java.io.*;
import java.util.*;
import java.security.*;
import java.lang.Math;

public class CS210project {
    // 00 - class variables
    private static String[] x1 = {"I", "You", "We", "They"}; // subject
    private static String[] x2 = {"abnormally", "accidentally", "actually", "almost", "always", "carefully", "deeply", "easily"}; // adverb
    private static String[] x3; // verb
    private static String[] x4 = {"you", "me", "him", "her", "it", "us", "them", "this", "that"}; // object
    private static String[] x5 = {", my dear", ", my love", ", goodbye", ", my darling", ", pal", ", friend", ", idiot", ", boy"}; // extra
    private static String[] x6 = {".", "!"}; // end


    // 01 - main()
    public static void main(String args[]) {
        instructions();
        ArrayList<String> x3_temp = load();
        x3 = correction(x3_temp);
        String[] sentence = generate(x1, x2, x3, x4, x5, x6); // ensure speed by passing the class variables (not mandatory)
        String[] sha256 = convert(sentence);
        info();
        char[][] matrix = charMatrix(sha256);
        int[][] bestMatches = compareMatrix(matrix, sentence);
        //int[] bestMatch = compare(sha256, sentence); // slow version for comparison: using the String[] instead the much quicker char[][]
        printResult(bestMatches, sentence);
    }


    // 02 - instructions()
    private static void instructions() {
        String intro = new String("\nHello,");
        intro += "\nThis program generates sentences based on 6 ordered variables (x1,x2,x3,x4,x5,x6).";
        intro += " Be careful: your ordered combination must hold synthax and grammar correctness.";
        intro += " By default, just 1 variable namely x3 is imported from .txt file. However, you";
        intro += " can use the 6 available variables in the way you prefer!!!";
        System.out.println(intro);
    }


    // 03 - load() the text file
    private static ArrayList<String> load() {
        try {
            Scanner sc = new Scanner(System.in); // scanner open
            System.out.println("\nWhat is the filename of the list? (1).txt included, (2)same folder");
            String n = new String();
            int count = 1;
            do { 
                n = sc.nextLine();
                if(!n.matches("^.+\\.txt$")) { // check the regex
                    count++;
                    System.out.println("Invalid file name. Attempt "+count+":"); // try again
                }
            } while(!n.matches("^.+\\.txt$"));
            sc.close(); // scanner close
            System.out.println("\nFile loaded");
            ArrayList<String> wor = readWords(n);
            return wor;
        } catch (Exception e) {
            System.err.println("\nError: " + e.getMessage());
            return null;
        }
    }


    // 04 - readWords(): slightly mod given method
    private static ArrayList<String> readWords(String filename) {
        try {
            // Open the file that is the first command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            // Initialize ArrayList
            ArrayList<String> wor = new ArrayList<String>();
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) 	{
                wor.add(strLine);
            }
            in.close(); // Close the input stream
            br.close(); // Close the buffer
            System.out.println("\nTemp ArrayList created");
            return wor;
        } catch(Exception e) { // Catch exception if any
            System.err.println("\nError: " + e.getMessage());
            return null;
        }
    }


    // 05 - correction(): check if any duplicate words in the imported list and remove them
    private static String[] correction(ArrayList<String> temp) {
        try {
            int len1 = temp.size();
            Collections.sort(temp, String.CASE_INSENSITIVE_ORDER);
            System.out.println("\nOriginal length = "+temp.size());
            for(int i=0; i<temp.size()-1; i++) {
                if(temp.get(i).equals(temp.get(i+1))) {
                    temp.remove(i);
                    System.out.println("  Duplicate removed: "+temp.get(i));
                    i--;
                }
            }
            String[] correct = temp.toArray(new String[0]);
            int len2 = correct.length;
            System.out.println("Definitive length = "+len2);
            savedComputationPower(len1, len2);
            return correct;
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
            return null;
        }
    }


    // 06 - savedComputationPower()
    private static void savedComputationPower(int len1, int len2) {
        try {
            int sentences1 = x1.length*x2.length*len1*x4.length*x5.length;
            int sentences2 = x1.length*x2.length*len2*x4.length*x5.length;
            long comp1 = 0, comp2 = 0;
            double result = 0;
            for(int i=1; i<sentences1-1; i++) {
                comp1 = comp1+i;
            }
            for(int i=1; i<sentences2-1; i++) {
                comp2 = comp2+i;
            }
            double d1 = reduceNumber(comp1);
            double d2 = reduceNumber(comp2);
            result = (1-(d2/d1))*100;
            result = Math.round(result*10.0)/10.0;
            System.out.println("\nUnnecessary computations avoided: "+result+" %");
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
        }
    }


    // 07 reduction(): reduce a long to 7 digits, allowing <double> operations
    private static double reduceNumber(long n) {
        double d = 0;
        String s = Long.toString(n);
        if(s.length()>7) {
            s = s.substring(0, 8);
        }
        d = Double.parseDouble(s);
        return d;
    }


    // 08 - sentences generator
    private static String[] generate(String[] x1, String[] x2, String[] x3, String[] x4, String[] x5, String[] x6) {
        try {
            String[] sentence = new String[x1.length*x2.length*x3.length*x4.length*x5.length*x6.length];
            int index = 0;
            for(String i : x1) {
                for(String j : x2) {
                    for(String k : x3) {
                        for(String u : x4) {
                            for(String v : x5) {
                                for(String w : x6) {
                                    sentence[index] = i+" "+j+" "+k+" "+u+v+w;
                                    index++;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("\nString[] of valid sentences: done");
            return sentence;
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
            return null;
        }
    }


    // 09 - sha256(): hash generator
    private static String sha256(String input) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
            byte[] salt = "CS210+".getBytes("UTF-8");
            mDigest.update(salt);
            byte[] data = mDigest.digest(input.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<data.length;i++) {
                sb.append(Integer.toString((data[i]&0xff)+0x100,16).substring(1));
            }
            return sb.toString();
        } catch(Exception e) {
            return(e.toString());
        }
    }


    // 10 - convert(): String[] sentence --> String[] sha256
    private static String[] convert(String[] sentences) {
        try {
            int len = sentences.length;
            String[] sha256 = new String[len];
            for(int i=0; i<len; i++) {
                sha256[i] = sha256(sentences[i]);
            }
            System.out.println("\nString[] SHA256: done");
            return sha256;
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
            return null;
        }
    }


    // 11 - info(): how many comparisons are made depending on inputs
    private static void info() {
        try {
            int sentences = x1.length*x2.length*x3.length*x4.length*x5.length;
            long result = 0;
            for(int i=1; i<sentences-1; i++) {
                result = result+i;
            }
            String output = new String("\nValid sentences generated: ");
            output += sentences;
            output += "\nComparisons pool: ";
            output += result;
            if(sentences>1500000) {
                output += "\n\nREQUIREMENTS: 8+Gb ram";
            }
            System.out.println(output);
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
        }
    }


    // 12 - check(): corresponding equal chars in 2 strings - NOT USED
    private static int check(String s1, String s2) {
        try {
            int count = 0;
            for(int i=0; i<64; i++) {
                if(s1.charAt(i)==s2.charAt(i)) {
                    count++;
                }
                if(i==53 && count<11) { // power efficiency: skip 10 comparisons (15%) --> result: +2% performance
                    return 0;
                }
            }
            return count;
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
            return 0;
        }
    }


    // 13 - compare(): ALL possible comparisons in the array of sentences - NOT USED
    private static int[] compare(String[] sen256, String[] sen) {
        try {
            System.out.println("\nMINING .....");
            int[] data = new int[3];
            int max = 0, i1 = 0, i2 = 0;
            int len = sen256.length;
            for(int i=0; i<len-1; i++) { // start from last <i>
                long startTime = elapsedStart();
                for(int j=i+1; j<len; j++) { // inner loop starts from outer index
                    int count = check(sen256[i], sen256[j]);
                    if(count>19) {
                        max = count;
                        i1 = i;
                        i2 = j;
                        System.out.println("\nn="+max+" --- i="+i+" --- "+sen[i1]+" --- "+sen[i2]);
                    }
                }
                elapsedEnd(startTime);
            }
            System.out.println("\nThe comparisons are finished.");
            data[0] = i1;
            data[1] = i2;
            data[2] = max;
            return data;
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
            return null;
        }

    }


    // 14 - charMatrix(): create a char matrix storing the sha256 values
    private static char[][] charMatrix(String[] s) {
        try {
            char[][] matrix = new char[s.length][64];
            for(int i=0; i<64; i++) {
                for(int j=0; j<s.length; j++) {
                    matrix[j][i] = s[j].charAt(i);
                }
            }
            System.out.println("\nchar[][] matrix: done --> 375% speed!");
            return matrix;
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
            return null;
        }
    }


    // 15 - compareMatrix(): ALL possible comparisons in the char matrix
    private static int[][] compareMatrix(char[][] matrix, String[] sentence) {
        System.out.println("\nMINING .....");
        int[][] results = new int[10][3]; // store the best 10 matches
        int len = matrix.length;
        int count = 0, r1 = 0, r2 = 0;
        for(int i=0; i<len-1; i++) { // you can replace the index of the last finding here, and restart from that point
            long startTime = elapsedStart();
            for(int j=i+1; j<len; j++) {
                for(int k=0; k<64; k++) {
                    if(matrix[i][k]==matrix[j][k]) {
                        count++;
                    }
                }
                if(count>19) { // just for count 20+ to avoid useless computations
                    System.out.println("\nn="+count+" --- i="+i+" --- "+sentence[i]+" --- "+sentence[j]); // keep track of the index, in case you want to stop mining
                    if(count>=results[9][0]) { // if greater or equal than 10th best result
                        results = findingsManager(results, count, r1, r2); // insert it in best results matrix
                    }
                }
                count = 0;
            }
            elapsedEnd(startTime);
        }
        System.out.println("\nThe comparisons are finished.");
        return results;
    }


    // 16 - findingsManager(): to manage in real time the best 10 matches
    private static int[][] findingsManager(int[][] matrix, int count, int i1, int i2) {
        int[] temp = new int[3];
        for(int i=9; i>0; i--) { // just loop 1 time through the best results matrix
            if(matrix[i-1][0] < count) { // swap if greater than 9th, 8th, ..., 1st result
                temp[0] = matrix[i-1][0];
                temp[1] = matrix[i-1][1];
                temp[2] = matrix[i-1][2];
                matrix[i-1][0] = count;
                matrix[i-1][1] = i1;
                matrix[i-1][2] = i2;
                matrix[i][0] = temp[0];
                matrix[i][1] = temp[1];
                matrix[i][2] = temp[2];
            }
        }
        return matrix;
    }


    // 17 - printResult(): print the best 10 matches
    private static void printResult(int[][] result, String[] sentence) {
        try {
            String output = new String("\nBest 10 Matches:");
            for(int i=0; i<10; i++) {
                output += "\n"+result[i][0];
                output += "\n"+sentence[result[i][1]];
                output += "\n"+sentence[result[i][2]];
            }
            System.out.println(output);
        } catch(Exception e) {
            System.err.println("\nError: " + e.getMessage());
        }
    }


    // 18 - elapsedStart(): start monitoring elapse time
    private static long elapsedStart() {
        long startTime = System.nanoTime();
        return startTime;
    }


    // 19 - elapsedEnd(): end monitoring elapse time; print the delta
    private static void elapsedEnd(long startTime) {
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000; // conversion in milliseconds
        System.out.println(duration);
    }

}
