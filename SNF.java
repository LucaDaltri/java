/* 
 * This program calculates the SNF Smith Normal Form of ANY nxn matrix
 * 
 * There are about 10 small functions 
 * these are called by 2 main functions that compute A1 and A2 (intermediate phases to get SNF)
 * then, a final function snf() uses the two main functions to build up the unique SNF of the input matrix
 * 
 * NOTES: 
 * - Whithin the program there are loops 'while count c<100', which can be increased if necessary
 * - Main method at the end. Just insert the matrix of which to compute the SNF
 */

import java.util.Arrays;

public class SNF {

    /* 1. find the smallest (absolute value) non-zero number in the matrix */
    public static int[] findSmallest(int[][] A) {
        int[] sml = {-1,-1}; //the index of the |smallest| element
        int val = Integer.MAX_VALUE;
        int n = A.length;
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                int abs = Math.abs(A[i][j]);
                if(abs<val && abs!=0) {
                    val = abs;
                    sml[0] = i;
                    sml[1] = j;
                }
            }
        }
        return sml;
    }

    /* 2. bring the smallest value at the top-left position */
    public static int[][] smallestTopLeft(int[][] A, int[] pos) { //position of the |smallest|
        int n = A.length;
        int[] tmp = new int[n];
        if(pos[0]!=0 && pos[0]!=-1) { //bring smallest to the first row
            for(int j=0; j<n; j++) {
                tmp[j] = A[0][j];
                A[0][j] = A[pos[0]][j];
                A[pos[0]][j] = tmp[j];
            }
        }
        if(pos[1]!=0 && pos[0]!=-1) { //bring smallest to the first column
            for(int i=0; i<n; i++) {
                tmp[i] = A[i][0];
                A[i][0] = A[i][pos[1]];
                A[i][pos[1]] = tmp[i];
            }
        }
        return A;
    }

    /* 3.1 first row reduction */
    public static int[][] rowReduction(int[][] A) {
        int n = A.length;
        int m = A[0][0]; //the minimum value at top-left
        int d = 0; //integer division result
        if(m!=0) { //avoid division by 0 when I have all zeros
            for(int j=1; j<n; j++) { //start from the second index
                d = A[0][j]/m;
                for(int i=0; i<n; i++) {
                    A[i][j] = A[i][j]-d*A[i][0];
                }
            }
        }
        return A;
    }

    /* 3.2 first column reduction */
    public static int[][] colReduction(int[][] A) {
        int n = A.length;
        int m = A[0][0]; //the minimum value at top-left
        int d = 0; //integer division result
        if(m!=0) { //avoid division by 0
            for(int i=1; i<n; i++) { //start from the second index
                d = A[i][0]/m;
                for(int j=0; j<n; j++) {
                    A[i][j] = A[i][j]-d*A[0][j];
                }
            }
        }
        return A;
    }

    /* 4. Find the non-zero entry of least norm in row[0] and col[0] */
    public static int[] leastNorm(int[][] A) {
        int n = A.length;
        int[] pos = {-1,-1};
        int val = Math.abs(A[0][0]); //must be greater then all other values in first row and column
        for(int i=1; i<n; i++) { //first row search
            int tmp = Math.abs(A[i][0]);
            if(tmp<val && tmp>0) {
                val = tmp;
                pos[0] = i;
                pos[1] = 0;
            }
        }
        for(int j=1; j<n; j++) { //first column search
            int tmp = Math.abs(A[0][j]);
            if(tmp<val && tmp>0) {
                val = tmp;
                pos[0] = 0;
                pos[1] = j;
            }
        }
        return pos;
    }

    /* 5. check if first row and column contain all zero except at 'a' the top-left entry (takes integer matrix, returns Boolean) */
    public static boolean allZero(int[][] A) {
        int n = A.length;
        boolean flag = true;
        for(int i=1; i<n; i++) { //avoid the first position
            if(A[i][0]!=0) {
                flag = false;
            }
        }
        for(int j=1; j<n; j++) { //avoid the first position
            if(A[0][j]!=0) {
                flag = false;
            }
        }
        return flag;
    }

    /* 6. check if a number <a> divides number <b> (takes in two integers, returns a Boolean) */
    public static boolean divides(int a, int b) {
        boolean flag = false;
        if(b%a==0) {
            flag = true;
        }
        return flag;
    }

    /* 7. check if in the sub-matrix (ignore first row and col) there is a number 'b' which is not divisible by the first top-left entry in the SNF matrix - by SNF definition the previous number must divide the following ones */
    public static int[] notDivisible(int[][] A) {
        int n = A.length;
        int[] pos = {-1,-1};
        if(A[0][0]!=0) { //avoid doing mod 0
            for(int i=1; i<n; i++) {
                for(int j=1; j<n; j++) {
                    if(A[i][j]%A[0][0]!=0) {
                        pos[0] = i;
                        pos[1] = j;
                    }
                }
            }
        }
        return pos;
    }

    /* 8. bring 'b' the non-divisible element row in the top row */
    public static int[][] bTop(int[][] A, int[] pos) {
        int n = A.length;
        for(int j=1; j<n; j++) {
            A[0][j] = A[pos[0]][j];
        }
        return A;
    }

    /* 9. decompose 'b' in quotient and remainder: b = qa + r */
    public static int[] qrDecomposition(int a, int b) {
        int r = 0; //remainder
        int q = 0; //quotient
        int[] qr = {q,r};
        if(b!=0) {
            r = a%b;
            q = a/b;
            qr[0] = q;
            qr[1] = r;
        }
        return qr;
    }

    /* 10. subtract 'q' times column0 to columnj - to isolate 'r' the remainder */
    public static int[][] isolateRem(int[][] A, int[] qr, int col) {
        int n = A.length;
        int q = qr[0]; //get the quotient
        for(int i=0; i<n; i++) {
            A[i][col] = A[i][col]-q*A[i][0];
        }
        return A;
    }

    /* I. Obtain A1 */
    public static int[][] getA1(int[][] A) {
        int[] idx = findSmallest(A);
        A = smallestTopLeft(A, idx);
        int[] pos = leastNorm(A);
        boolean rdy = allZero(A);
        int c = 0; //count
        while(!rdy && c<100) {
            A = rowReduction(A);
            A = colReduction(A); //here I have G
            pos = leastNorm(A);
            if(pos[0]==-1) { //all zeros in the 1st row and column: we are done
                rdy = true;
                break;
            }
            A = smallestTopLeft(A, pos); //move the smallest obtained after reduction to top-left position
            rdy = allZero(A);
            c++;
        }
        return A;
    }

    /* II. Obtain A2 */
    public static int[][] getA2(int[][] A) {
        int[] nd = {-1,-1}; //position of the non-divisible element
        int c = 0; //count
        while(c<100) {
            nd = notDivisible(A); //now need to check if there is a number 'b' which is not divisible by my first SNF entry
            if(nd[0]==-1) {
                break; //everything is divisible
            }
            A = bTop(A, nd); //bring 'b' at the top left position
            int[] qr = qrDecomposition(A[0][nd[1]], A[0][0]);
            A = isolateRem(A, qr, nd[1]);
            int[] col = {0, nd[1]}; //the column of the smallest element in row[0]
            A = smallestTopLeft(A, col);
            c++;
        }
        return A;
    }

    /* FINAL. Calculate SNF the Smith Normal Form */
    public static int[][] snf(int[][] A) {
        int n = A.length;
        int c = 0; //count
        while(c<n-1) { //no need to loop until 'n' because the last step takes in a 1x1 matrix 
            int[][] tmp = new int[n-c][n-c]; //temporary matrix that becomes smaller and smaller as 'c' increases
            for(int i=c; i<n; i++) { //remove the first col and row
                for(int j=c; j<n; j++) {
                    tmp[i-c][j-c] = A[i][j];
                }
            }
            tmp = getA1(tmp); 
            tmp = getA2(tmp);
            tmp = getA1(tmp); //A2 need to be in A1 form (with zeros in first row/col)
            for(int i=0; i<n-c; i++) { //update the matrix A
                for(int j=0; j<n-c; j++) {
                    A[i+c][j+c] = tmp[i][j];
                }
            }
            A[c][c] = Math.abs(A[c][c]); //want absolute values on SNF main diagonal
            System.out.println(Arrays.deepToString(A));
            c++;
        }
        return A;
    }
 
    public static void main(String[] args) {
        int[][] A = {{4,0,0,9,8,7},{0,0,-20,9,6,6},{11,0,10,6,66,5},{12,0,0,4,56,4},{10,0,10,6,36,33},{0,0,-20,9,6,50}};
        System.out.println(Arrays.deepToString(A));
        int[][] S = Arrays.copyOf(A, A.length); //send a copy otherwise Java modifies the original matrix
        S = snf(S);
    }
}