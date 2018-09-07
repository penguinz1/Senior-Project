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
public class Student1Smart extends Trader {

    private final int NUM_STUDENTS = 500; //Number of Student1 Traders

    private final double TEST_CASH; //Initial cash
    private final int TEST_STOCK; //Initial stock

    private final ArrayList<Double> values; //Stock prices
    private final double[] pastMA; //Past moving average indicator data
    private final double[] pastVol; //Past volume indicator data
    private final double[] movAvg; //Past moving average data
    private final ArrayList<Double> volData; //Current volume data

    private ArrayList<Student1> studentList; //ArrayList of Student1 Traders

    private final Student1 featuredStudent; //Student1 Trader whose data is to be used

    /**
     * Constructs a Student 1 Trader with initial stocks, cash, stock data, and volume data
     * @param stocks initial stocks
     * @param cash initial cash
     * @param values stock data
     * @param volData volume data
     */
    public Student1Smart(int stocks, double cash,
            ArrayList<Double> values, ArrayList<Double> volData) {
        super(stocks, cash);
        this.values = values;
        this.volData = volData;

        TEST_CASH = cash;
        TEST_STOCK = stocks;

        movAvg = Utility.movingAverage(50, values);

        pastMA = new double[values.size() - 50];
        for (int i = 0; i < pastMA.length; i++) {
            pastMA[i] = movingAverageIndicator(0);
        }

        pastVol = new double[values.size() - 5];
        for (int i = 0; i < pastVol.length; i++) {
            pastVol[i] = volumeIndicator(i + 5);
        }
        
        fillList();
        test(100);
        System.out.println("Done!");

        featuredStudent = studentList.get(0);
    }
    
    /**
     * Fills the Student1 ArrayList
     */
    private void fillList(){
        studentList = new ArrayList<Student1>();
        for(int i = 0; i < NUM_STUDENTS; i++){
            studentList.add(new Student1());
        }
    }
    
    /**
     * Calculates the volume indicator
     * @param day the day of calculation
     * @return the volume indicator
     */
    private double volumeIndicator(int day) {
        double sum = 0;

        for (int i = day - 5; i < day; i++) {
            sum += volData.get(i);
        }

        double avg = sum / 5;
        return volData.get(volData.size() - 1) - avg;

    }

    /**
     * Calculates the moving average indicator
     * @param day the day of calculation
     * @return the moving average indicator
     */
    private double movingAverageIndicator(int day) {
        SimpleRegression simpleRegression = new SimpleRegression(true);

        double[][] sampData = new double[50][2];
        for (int i = 0; i < 50; i++) {
            sampData[i] = new double[]{i, movAvg[day + i]};
        }

        simpleRegression.addData(sampData);

        return simpleRegression.getSlope();
    }

    /**
     * Buys or sells stocks using the featuredStudent's data
     */
    @Override
    public void next() {
        double d = featuredStudent.next();
        if(d < -100000){
            d = -10000;
        } else if(d > 100000){
            d = 10000;
        }
        tradeStocks((int)d);
    }
    
    /**
     * Sets stock price data
     * @param data stock price data
     */
    public void setData(ArrayList<Double> data){
        featuredStudent.setData(data);
    }
    
    /**
     * Sets volume data
     * @param vol volume data
     */
    public void setVolume(ArrayList<Double> vol){
        featuredStudent.setVolumeData(vol);
    }

    /**
     * Tests the Student1 Traders and removes Student1 Traders randomly,
     * biased toward the weaker ones
     * @param num number of tests
     */
    private void test(int num) {
        for (int i = 0; i < num; i++) {
            mutateStudents();
            ArrayList<Student1> students = iterate();

            int[] take = Utility.pickOut(NUM_STUDENTS);

            for (int j = 0; j < NUM_STUDENTS / 2; j++) {
                students.remove(take[NUM_STUDENTS / 2 - j - 1]);
            }

            for (int j = NUM_STUDENTS / 2 - 1; j >= 0; j--) {
                students.add(new Student1(students.get(j)));
            }

            studentList = students;
        }
    }

    /**
     * Orders the Student1 Traders in terms of performance
     * @return an ArrayList of Student1 Traders, in order of performance 
     */
    private ArrayList<Student1> iterate() {
        ArrayList<Student1> students = new ArrayList<>();

        first:
        for (Student1 student : studentList) {
            double test = student.simulate(Utility.toArray(values), pastVol,
                    pastMA, TEST_CASH, TEST_STOCK);

            for (int i = 0; i < students.size(); i++) {
                if (test > students.get(i).getTemp()) {
                    students.add(i, student);
                    continue first;
                }
            }

            students.add(student);
        }

        return students;
    }

    /**
     * Slightly alters the data in each Student1 Trader
     */
    private void mutateStudents() {
        for (Student1 student : studentList) {
            student.mutate();
        }
    }
}
