/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalseniorproject;

import java.util.ArrayList;

/**
 * Similar functioning to that of the SmartTrader - uncommented
 * @author JackN
 */
public class TechTrader extends Trader {

    private final int NUM_BOTS = 100;
    private final double TEST_CASH;
    private final int TEST_STOCK;

    private double[] data;
    private double[] high;
    private double[] low;
    private double[][][] avg;
    private double[][] currAvg;
    private ArrayList<TBot> botList;

    private TBot featuredBot;

    /**
     * Constructs a SmartTrader with initial stocks, cash, and stock data
     *
     * @param stocks initial stocks
     * @param cash initial cash
     * @param data stock data
     */
    public TechTrader(int stocks, double cash, double[] data, double[] high, double[] low) {
        super(stocks, cash);

        TEST_CASH = cash;
        TEST_STOCK = stocks;

        this.data = data;
        this.high = high;
        this.low = low;

        fillAvg();
        fillBotList();

        test(10);

        featuredBot = botList.get(0);

        System.out.println("Done!");
    }

    @Override
    public void next() {
        fillCurrAvg();

        tradeStocks(featuredBot.toTrade(data, currAvg));
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public void setHigh(double[] high) {
        this.high = high;
    }

    public void setLow(double[] low) {
        this.low = low;
    }

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

    private ArrayList<TBot> iterate() {
        ArrayList<TBot> bots = new ArrayList<>();

        first:
        for (TBot bot : botList) {
            double test = bot.simulate(data, high, low, avg, TEST_CASH, TEST_STOCK, 20);

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

    private void mutateBots() {
        for (TBot bot : botList) {
            bot.mutate();
        }
    }

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

    private void fillCurrAvg() {
        currAvg = new double[10][10];

        for (int j = 0; j < 10; j++) {
            for (int k = 0; k < 10; k++) {
                currAvg[j][k] = calc(data.length - 19 + k, j + 1);
            }
        }
    }

    private void fillBotList() {
        botList = new ArrayList<>();

        for (int i = 0; i < NUM_BOTS; i++) {
            TBot bot = new TBot();
            botList.add(bot);
        }
    }

    private double calc(int day, int amount) {
        double sum = 0;

        for (int i = day; i < day + amount; i++) {
            sum += data[i];
        }

        return sum / amount;
    }

    private class TBot {

        private final static double DATA_VARIATION = 10;
        private final static double DATA2_VARIATION = 1;
        private final static double MULT_VARIATION = 10;

        private double[][] data;
        private double[] data2;
        private double multiplier;

        private double tempStore;

        private double tempCash;
        private double tempStock;

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

            data2 = new double[14];
            for (int i = 0; i < data2.length; i++) {
                data2[i] = 1;
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

            data2 = new double[14];
            for (int i = 0; i < data2.length; i++) {
                data2[i] = t.data2[i];
            }

            multiplier = t.multiplier;
        }

        public double[][] getData() {
            return data;
        }

        public double getMult() {
            return multiplier;
        }

        public double getTemp() {
            return tempStore;
        }

        public double getCash() {
            return tempCash;
        }

        public double getStock() {
            return tempStock;
        }

        public void mutate() {
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] += (Math.random() - .5) * DATA_VARIATION;
                }
            }

            for (int i = 0; i < data2.length; i++) {
                data2[i] += (Math.random() - .5) * DATA2_VARIATION;
            }

            multiplier += (Math.random() - .5) * MULT_VARIATION;
        }

        public double simulate(double[] values, double[] high, double[] low, double[][][] avg,
                double cash, int stocks, int day) {

            for (int it = 4; it < avg.length - 1; it++) {
                double change = 0;

                if (Utility.calculateADX(values, high, low) > 20) {
                    for (int i = 0; i < data.length; i++) {
                        for (int j = 0; j < data[0].length; j++) {
                            change += data[i][j] * avg[it][i][j];
                        }
                    }
                } else {
                    for (int i = 0; i < 14; i++) {
                        change += data2[i] * Utility.calculateRSI14(i + 14, values);
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

        public int toTrade(double[] values, double[][] avg) {
            double change = 0;

            if (Utility.calculateADX(values, high, low) > 20) {
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    change += data[i][j] * avg[i][j];
                }
            }
            } else {
                for (int i = 0; i < 14; i++) {
                    change += data2[i] * Utility.calculateRSI14f(i, values);
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
