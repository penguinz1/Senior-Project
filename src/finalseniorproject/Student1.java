/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalseniorproject;

import java.util.ArrayList;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author JackN
 */
public class Student1{
    //Constants
    private final static double SLOPE_VARIATION = 100;
    private final static double VOL_VARIATION = .1;

    //Data
    private double SLOPE_MULT;
    private double VOL_MULT;

    private ArrayList<Double> data; //Stock prices data
    private ArrayList<Double> volData; //Volume data

    private double tempStore; //Temporary performance

    /**
     * Sets initial data
     */
    public Student1() {
        SLOPE_MULT = 10;
        VOL_MULT = .0001;
    }
    
    /**
     * Copy constructor
     * @param st Student1 object to be copied
     */
    public Student1(Student1 st){
        SLOPE_MULT = st.SLOPE_MULT;
        VOL_MULT = st.VOL_MULT;
    }

    /**
     * Buys or sells stocks based on data
     * @return how many stocks to buy or sell
     */
    public double next(){
        double[] movAvg = Utility.movingAverage(50, data);
        
        return SLOPE_MULT * movingAverageIndicator(movAvg) +
                VOL_MULT * volumeIndicator();
    }
    
    /**
     * Calculates a moving average indicator
     * @param movAvg moving average data
     * @return moving average indicator
     */
    private double movingAverageIndicator(double[] movAvg) {
        SimpleRegression simpleRegression = new SimpleRegression(true);

        double[][] sampData = new double[50][2];
        for (int i = 0; i < 50; i++) {
            sampData[i] = new double[]{i, movAvg[movAvg.length - 50 + i]};
        }

        simpleRegression.addData(sampData);

        return simpleRegression.getSlope();
    }

    /**
     * Calculates a volume indicator
     * @return volume indicator
     */
    private double volumeIndicator() {
        double sum = 0;

        for (int i = volData.size() - 6; i < volData.size(); i++) {
            sum += volData.get(i);
        }

        double avg = sum / 5;
        return volData.get(volData.size() - 1) - avg;

    }

    /**
     * Sets stock price data
     * @param data stock price data
     */
    public void setData(ArrayList<Double> data) {
        this.data = data;
    }

    /**
     * Sets volume data
     * @param volData volume data
     */
    public void setVolumeData(ArrayList<Double> volData) {
        this.volData = volData;
    }

    /**
     * Simulates the Student1 Trader on past data
     * @param values stock prices
     * @param pastVol past volume data
     * @param pastMA past moving average data
     * @param cash initial cash
     * @param stocks initial stock
     * @return the final assets
     */
    public double simulate(double[] values, double[] pastVol,
            double[] pastMA, double cash, int stocks) {
        for (int day = 50; day < values.length; day++) {

            double change = 0;

            change += SLOPE_MULT * pastMA[day - 50]
                    + VOL_MULT * pastVol[day - 5];

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
        }

        tempStore = cash + stocks * values[values.length - 1];

        return cash + stocks * values[values.length - 1];
    }
    
    /**
     * Slightly alters the Student1 Trader
     */
    public void mutate(){
        SLOPE_MULT += (Math.random() - .5) * SLOPE_VARIATION;
        VOL_MULT += (Math.random() - .5) * VOL_VARIATION;
    }

    /**
     * @return the tempStore
     */
    public double getTemp() {
        return tempStore;
    }
    

}
