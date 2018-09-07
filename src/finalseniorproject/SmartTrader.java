/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalseniorproject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JackN
 */
public class SmartTrader extends Trader {

    private final int NUM_BOTS = 500; //Number of TBots
    private final double TEST_CASH; //Initial Cash
    private final int TEST_STOCK; //Initial Stock

    private double[] data; //Stock price data
    private double[][][] avg; //Past moving average data
    private double[][] currAvg; //Current moving average data
    private ArrayList<TBot> botList; //ArrayList holding the TBots

    private final TBot featuredBot; //The TBot that this SmartTrader uses

    /**
     * Constructs a SmartTrader with initial stocks, cash, and stock data
     *
     * @param stocks initial stocks
     * @param cash initial cash
     * @param data stock data
     */
    public SmartTrader(int stocks, double cash, double[] data) {
        super(stocks, cash);

        TEST_CASH = cash;
        TEST_STOCK = stocks;

        this.data = data;

        fillAvg();
        fillBotList();

        test(50);

        featuredBot = botList.get(0);
        printData();

        System.out.println("Done!");
    }

    /**
     * Prints the data held in the featuredBot
     */
    private void printData() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter("src/FinalSeniorProject/data2.txt")))) {
            for(int i = 0; i < featuredBot.data.length; i++){
                for(int j = 0; j < featuredBot.data[0].length; j++){
                    out.print(oneDecimal(featuredBot.data[i][j]) + " ");
                }
                out.println();
            }
            out.println(featuredBot.multiplier);
        } catch (IOException ex) {
            Logger.getLogger(SmartTrader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Converts a number into a one decimal format
     * @param d the input number
     * @return the number in a one decimal format
     */
    private double oneDecimal(double d) {
        return (int) (d * 10) / 10.0;
    }

    /**
     * Buys or sells stocks using the featuredBot's data
     */
    @Override
    public void next() {
        fillCurrAvg();

        tradeStocks(featuredBot.toTrade(data, currAvg));
    }

    /**
     * Sets the stock price data
     * @param data stock price data
     */
    public void setData(double[] data) {
        this.data = data;
    }

    /**
     * Tests the TBots and removes TBots randomly, biased toward the weaker ones
     * @param num number of tests
     */
    private void test(int num) {
        for (int i = 0; i < num; i++) {
            mutateBots();
            ArrayList<TBot> bots = iterate();

            int[] take = Utility.pickOut(NUM_BOTS);

            for (int j = 0; j < NUM_BOTS / 2; j++) {
                bots.remove(take[NUM_BOTS / 2 - j - 1]);
            }

            for (int j = NUM_BOTS / 2 - 1; j >= 0; j--) {
                bots.add(new TBot(bots.get(j)));
            }

            botList = bots;
        }
    }

    /**
     * Orders the TBots in terms of performance
     * @return an ArrayList of TBots, in order of performance 
     */
    private ArrayList<TBot> iterate() {
        ArrayList<TBot> bots = new ArrayList<>();

        first:
        for (TBot bot : botList) {
            double test = bot.simulate(data, avg, TEST_CASH, TEST_STOCK, 20);

            for (int i = 0; i < bots.size(); i++) {
                if (test > bots.get(i).getTemp()) {
                    bots.add(i, bot);
                    continue first;
                }
            }

            bots.add(bot);
        }

        return bots;
    }

    /**
     * Slightly alters the data in each TBot
     */
    private void mutateBots() {
        for (TBot bot : botList) {
            bot.mutate();
        }
    }

    /**
     * Fills the past moving average array
     */
    private void fillAvg() {
        avg = new double[data.length - 19][10][10];

        for (int i = 0; i < data.length - 19; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    avg[i][j][k] = calc(i + k, j + 1);
                }
            }
        }
    }

    /**
     * Fills the current moving average array
     */
    private void fillCurrAvg() {
        currAvg = new double[10][10];

        for (int j = 0; j < 10; j++) {
            for (int k = 0; k < 10; k++) {
                currAvg[j][k] = calc(data.length - 19 + k, j + 1);
            }
        }
    }

    /**
     * Fills the TBot ArrayList
     */
    private void fillBotList() {
        botList = new ArrayList<>();

        for (int i = 0; i < NUM_BOTS; i++) {
            TBot bot = new TBot();
            botList.add(bot);
        }
    }

    /**
     * Calculates the moving average
     * @param day starting day
     * @param amount number of days
     * @return the moving average
     */
    private double calc(int day, int amount) {
        double sum = 0;

        for (int i = day; i < day + amount; i++) {
            sum += data[i];
        }

        return sum / amount;
    }

    /**
     * The TBot class
     */
    private class TBot {

        //Constants
        private final static double DATA_VARIATION = 10;
        private final static double MULT_VARIATION = 10;

        //Data
        private double[][] data;
        private double multiplier;

        private double tempStore; //Temporary performance
        private double tempCash; //Temporary cash
        private double tempStock; //Temporary stock

        /**
         * Creates default TBot with data values of 1
         */
        public TBot() {
            data = new double[10][10];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] = 1;
                }
            }

            multiplier = 30;
        }

        /**
         * Copy Constructor
         *
         * @param t TBot to be copied
         */
        public TBot(TBot t) {
            data = new double[10][10];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] = t.data[i][j];
                }
            }

            multiplier = t.multiplier;
        }

        /**
         * @return TBot data
         */
        public double[][] getData() {
            return data;
        }

        /**
         * @return TBot multiplier
         */
        public double getMult() {
            return multiplier;
        }

        /**
         * @return temporary performance
         */
        public double getTemp() {
            return tempStore;
        }

        /**
         * @return temporary cash
         */
        public double getCash() {
            return tempCash;
        }

        /**
         * @return temporary stock
         */
        public double getStock() {
            return tempStock;
        }

        /**
         * Slightly alters the TBot
         */
        public void mutate() {
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] += (Math.random() - .5) * DATA_VARIATION;
                }
            }

            multiplier += (Math.random() - .5) * MULT_VARIATION;
        }

        /**
         * Simulates the TBot on past data
         * @param values stock price values
         * @param avg moving average values
         * @param cash initial cash
         * @param stocks initial stock
         * @param day starting day
         * @return the final assets
         */
        public double simulate(double[] values, double[][][] avg,
                double cash, int stocks, int day) {

            for (int it = 0; it < avg.length - 1; it++) {
                double change = 0;

                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < data[0].length; j++) {
                        change += data[i][j] * avg[it][i][j];
                    }
                }

                change *= multiplier;
                int potChange = (int) change;

                if (potChange > 0) {
                    if (potChange * values[day] < cash) {
                        stocks += potChange;
                        cash -= potChange * values[day];
                    } else {
                        stocks += (int) (cash / values[day]);
                        cash -= (int) (cash / values[day]) * values[day];
                    }
                } else {
                    if (-potChange < stocks) {
                        stocks += potChange;
                        cash -= potChange * values[day];
                    } else {
                        cash += stocks * values[day];
                        stocks = 0;
                    }
                }

                day++;
            }

            day--;
            tempStore = cash + stocks * values[day];

            tempCash = cash;
            tempStock = stocks;

            return cash + stocks * values[day];
        }

        /**
         * Calculates how many stocks to buy or sell based on the TBot's data
         * @param values stock prices
         * @param avg current moving average
         * @return how many stocks to buy or sell
         */
        public int toTrade(double[] values, double[][] avg) {
            double change = 0;

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    change += data[i][j] * avg[i][j];
                }
            }

            change *= multiplier;

            if (change > 1000000) {
                change = 1000000;
            } else if (change < -1000000) {
                change = -1000000;
            }

            int potChange = (int) change;

            return potChange;
        }

    }
}
