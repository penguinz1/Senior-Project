/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalseniorproject;

import java.util.ArrayList;

/**
 *
 * @author JackN
 */
public class Utility {

    /**
     * Returns the parameter, rounded to two decimal places
     *
     * @param d the parameter
     * @return the parameter, rounded to two decimal places
     */
    public static double twoDecimal(double d) {
        return Math.round((d * 100)) / 100.0;
    }

    /**
     * Return moving averages of "days" days for values in values ArrayList
     *
     * @param days moving average for days
     * @param values ArrayList of stock values
     * @return array of moving averages
     */
    public static double[] movingAverage(int days, ArrayList<Double> values) {
        double[] averages = new double[values.size() - days + 1];
        for (int i = 0; i < values.size() - days + 1; i++) {
            double sum = 0;
            for (int j = i; j < i + days; j++) {
                sum += values.get(j);
            }
            averages[i] = sum / days;
        }
        return averages;
    }

    /**
     * Picks half of the numbers from 0 to num, biased towards the higher numbers
     * @param num the maximum value
     * @return an array containing half of the numbers from 0 to num
     */
    public static int[] pickOut(int num) {
        int[] outOf = new int[num];

        for (int i = 0; i < num; i++) {
            outOf[i] = i;
        }

        int pos = 0;
        int[] picks = new int[outOf.length / 2];

        ArrayList<Integer> select = new ArrayList<>();

        for (int i = 0; i < outOf.length; i++) {
            for (int j = 0; j < i; j++) {
                select.add(i);
            }
        }

        first:
        while (pos < picks.length) {
            int attempt = select.get((int) (Math.random() * select.size()));
            for (int i = 0; i < pos; i++) {
                if (picks[i] == attempt) {
                    continue first;
                }
            }
            picks[pos++] = attempt;
        }

        qsort(picks, 0, picks.length - 1);

        return picks;
    }

    /**
     * Quicksort of a given array - found online
     * @param a the array
     * @param si the starting index
     * @param ei the ending index
     */
    private static void qsort(int[] a, int si, int ei) {
        //base case
        if (ei <= si || si >= ei) {
        } else {
            int pivot = a[si];
            int i = si + 1;
            int tmp;

            //partition array 
            for (int j = si + 1; j <= ei; j++) {
                if (pivot > a[j]) {
                    tmp = a[j];
                    a[j] = a[i];
                    a[i] = tmp;

                    i++;
                }
            }

            //put pivot in right position
            a[si] = a[i - 1];
            a[i - 1] = pivot;

            //call qsort on right and left sides of pivot
            qsort(a, si, i - 2);
            qsort(a, i, ei);
        }
    }

    /**
     * Prints out a two dimensional array
     * @param array the array
     */
    public static void printArray(double[][] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print("[");
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println("]");
        }
    }

    /**
     * Converts an ArrayList into an array
     * @param list the input ArrayList
     * @return the output array
     */
    public static double[] toArray(ArrayList<Double> list) {
        double[] d = new double[list.size()];

        for (int i = 0; i < list.size(); i++) {
            d[i] = list.get(i);
        }

        return d;
    }

    /**
     * Calculates RSI
     * @param n the number of days
     * @param list the stock price data
     * @return the RSI
     */
    public static double calculateRSI(int n, ArrayList<Double> list) {
        ArrayList<Double> differences = new ArrayList<>();

        for (int i = list.size() - n; i < list.size() - 1; i++) {
            differences.add(list.get(i + 1) - list.get(i));
        }

        double totalGain = 0;
        double totalLoss = 0;

        for (int i = 0; i < differences.size(); i++) {
            double d = differences.get(i);
            if (d > 0) {
                totalGain += d;
            } else {
                totalLoss += Math.abs(d);
            }
        }

        double avgGain = totalGain / differences.size();
        double avgLoss = totalLoss / differences.size();

        return 100 - 100 / (1 + avgGain / avgLoss);
    }

    /**
     * Calculates RSI over 14 days
     * @param n the starting day
     * @param list the stock price data
     * @return the RSI
     */
    public static double calculateRSI14(int n, double[] list) {
        ArrayList<Double> differences = new ArrayList<>();

        for (int i = n - 14; i <= n; i++) {
            differences.add(list[i + 1] - list[i]);
        }

        double totalGain = 0;
        double totalLoss = 0;

        for (int i = 0; i < differences.size(); i++) {
            double d = differences.get(i);
            if (d > 0) {
                totalGain += d;
            } else {
                totalLoss += Math.abs(d);
            }
        }

        double avgGain = totalGain / differences.size();
        double avgLoss = totalLoss / differences.size();

        return 100 - 100 / (1 + avgGain / avgLoss);
    }

    /**
     * Calculates the RSI over 14 days, going backwards
     * @param n the number of days back
     * @param list the stock price data
     * @return the RSI
     */
    public static double calculateRSI14f(int n, double[] list) {
        ArrayList<Double> differences = new ArrayList<>();

        for (int i = list.length - 15 - n; i < list.length - 1 - n; i++) {
            differences.add(list[i + 1] - list[i]);
        }

        double totalGain = 0;
        double totalLoss = 0;

        for (int i = 0; i < differences.size(); i++) {
            double d = differences.get(i);
            if (d > 0) {
                totalGain += d;
            } else {
                totalLoss += Math.abs(d);
            }
        }

        double avgGain = totalGain / differences.size();
        double avgLoss = totalLoss / differences.size();

        return 100 - 100 / (1 + avgGain / avgLoss);
    }

    /**
     * Calculates the plusDI14
     * @param close the closing stock prices
     * @param high the stock price highs
     * @param low the stock price lows
     * @param day the stock prices
     * @return the plusDI14
     */
    public static double plusDI14(double[] close, double[] high, double[] low, int day) {
        double tr = 0;
        double plusDM = 0;
        for (int i = close.length - 15 - day; i < close.length - day; i++) {
            tr += Math.max(Math.max(Math.abs(high[i] = low[i]),
                    Math.abs(high[i] - close[i - 1])), Math.abs(low[i] - close[i - 1]));
            if (high[i] - high[i - 1] > 0) {
                plusDM += high[i] - high[i - 1];
            }
        }

        double tr14 = tr / 14;
        double plusDM14 = plusDM / 14;

        double plusDI14 = plusDM14 * 100 / tr14;
        return plusDI14;
    }

    /**
     * Calculates the minusDI14
     * @param close the closing stock prices
     * @param high the stock price highs
     * @param low the stock price lows
     * @param day the stock prices
     * @return the minusDI14
     */
    public static double minusDI14(double[] close, double[] high, double[] low, int day) {
        double tr = 0;
        double minusDM = 0;
        for (int i = close.length - 15 - day; i < close.length - day; i++) {
            tr += Math.max(Math.max(Math.abs(high[i] = low[i]),
                    Math.abs(high[i] - close[i - 1])), Math.abs(low[i] - close[i - 1]));
            if (low[i - 1] - low[i] > 0) {
                minusDM += low[i - 1] - low[i];
            }
        }

        double tr14 = tr / 14;
        double minusDM14 = minusDM / 14;

        double minusDI14 = minusDM14 * 100 / tr14;
        return minusDI14;
    }

    /**
     * Calculates the ADX
     * @param close the closing stock prices
     * @param high the stock price highs
     * @param low the stock price lows
     * @return the ADX
     */
    public static double calculateADX(double[] close, double[] high, double[] low) {
        double dx = 0;

        for (int i = 0; i < 14; i++) {
            double plusDI = plusDI14(close, high, low, i);
            double minusDI = minusDI14(close, high, low, i);
            dx += Math.abs((plusDI - minusDI) / (plusDI + minusDI)) * 100;
        }

        return dx / 14;
    }
}
