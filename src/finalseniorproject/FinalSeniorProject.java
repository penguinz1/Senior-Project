/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalseniorproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author JackN
 */
public class FinalSeniorProject extends Application implements Runnable {

    private final int DELAY = 40; //Speed of the simulation
    private final String[] months = { //Array holding the names of the months
        "January", "February", "March", "April",
        "May", "June", "July", "August", "September",
        "October", "November", "December"
    };
    private final static int INITIAL_CASH = 1000; //Initial cash of the traders
    private final static int INITIAL_STOCK = 1000; //Initial stock of the traders
    private final static int NUM_RAND = 500; //Number of random traders
    private final String FIRST_YEAR = "" + 1990; //Value of the starting year

    public static ArrayList<Double> stockPrices; //Stock prices
    private static ArrayList<Double> currData; //Stock data available
    private static ArrayList<RandomTrader> randTraders; //Random Traders
    private static ArrayList<Double> stockVolume; //Stock volumes
    private static ArrayList<Double> currVolume; //Stock volume data available
    private static ArrayList<String> dates; //Stock dates
    private static ArrayList<Double> currHigh; //Stock high price data available
    private static ArrayList<Double> allHigh; //Stock high prices
    private static ArrayList<Double> currLow; //Stock low price data available
    private static ArrayList<Double> allLow; //Stock low prices

    private SmartTrader smartTrader; //Smart Trader object
    private Student1Smart s1Trader; //Student 1 Trader object
    private Student2 s2Trader; //Student 2 Trader object
    private TechTrader techTrader; //Technical Trader object

    private boolean proceed = true; //Control for stopping simulation
    private XYChart.Series series, series4,
            series5, series6, series7, series8, series9; // Graph lines
    private Text txt1, txt2, txt3, txt4, txtf; //Text for simulation description and date
    private Text txtR, txtRA, txtRAS, txtRM, txtRASD; //Text for Random Trader info
    private Text txtSm, txtSmA, txtSmS, txtSmM; //Text for Smart Trader info
    private Text txtS1, txtS1A, txtS1S, txtS1M; //Text for Student 1 Trader info
    private Text txtS2, txtS2A, txtS2S, txtS2M; //Text for Student 2 Trader info
    private Text txtT, txtTA, txtTS, txtTM; //Text for Technical Trader info
    private Text txtC, txtCA, txtCAS, txtCM; //Text for Control Trader info
    private Text txtRS1, txtRS2, txtRS3, txtRS4, txtRS5,
            txtRS6, txtRS7, txtRS8, txtRS9, txtRS10, txtRS11, txtRS12; //Text for spacing purposes
    private Text aPrice; //Text to display the current stock price

    private double currValue; //Value of the current stock price
    private static int currDay; //Value of the current day

    /**
     * Starts the simulation
     * @param primaryStage The stage
     */
    @Override
    public void start(Stage primaryStage) {
        initArrayLists(); //Initializes all ArrayLists
        initTraders(); //Initializes all Traders

        Thread t = new Thread(this); //Creates a Thread to run the simulation
        t.start(); //Starts the Thread

        Button btn = new Button(); //Creates a Button to stop the simulation
        btn.setText("Stop Simulation"); 
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                proceed = false;
            }
        });

        LineChart lc = makeLineChart(); //Makes the stock LineChart

        LineChart lc2 = makeLineChart2(); //Makes the trader asset LineChart

        HBox box = addHBox(); //Holds the simulation description and date
        VBox box2 = addVBox(); //Holds Random Trader info
        VBox box3 = addVBox2(); //Holds Smart Trader info
        VBox box4 = addVBox3(); //Holds Student 1 Trader info
        VBox box5 = addVBox4(); //Holds Student 2 Trader info
        VBox box8 = addVBox6(); //Holds Control Trader info
        VBox box9 = addVBox7(); //Holds Technical Trader info
        HBox box6 = addHBox2(box2, box3, box4, box5, box8, box9); //Places all info into one HBox
        VBox box7 = addVBox5(btn); //Places button and stock price into one VBox
        BorderPane border = new BorderPane(); //Creates new border type
        border.setTop(box); //Sets the top of the border to the simulation description and date
        border.setCenter(lc); //Sets the center of the border to the stock LineChart
        border.setBottom(box6); //Sets the bottom of the border to the trader info
        border.setRight(box7); //Sets the right of the border to the button and stock price
        border.setLeft(lc2); //Sets the left of the border to the trader asset LineChart

        Scene scene = new Scene(border, 1300, 1000); //Creates new scene with the implemented border

        primaryStage.setTitle("Stock Market"); //Creates a title for the simulation
        primaryStage.setScene(scene); //Sets the scene to the one created
        primaryStage.show(); //Sets the stage to be visible
    }

    /**
     * Holds the simulation description and date in one HBox
     * @return that HBox
     */
    private HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        txt1 = new Text("Date:");
        txt2 = new Text("January");
        txt3 = new Text("1");
        txt4 = new Text("1990");
        txtf = new Text("|||Mock Stock Market Performance|||");

        hbox.getChildren().addAll(txtf, txt1, txt2, txt3, txt4);

        return hbox;
    }

    /**
     * Places all trader info into one HBox
     * @param v1 Random Trader info
     * @param v2 Smart Trader info
     * @param v3 Student 1 Trader info
     * @param v4 Student 2 Trader info
     * @param v5 Control Trader info
     * @param v6 Technical Trader info
     * @return that HBox
     */
    private HBox addHBox2(VBox v1, VBox v2, VBox v3, VBox v4, VBox v5, VBox v6) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        hbox.getChildren().addAll(v1, v2, v3, v4, v5, v6);
        return hbox;
    }

    /**
     * Holds the Random Trader info in one VBox
     * @return that VBox
     */
    private VBox addVBox() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: red;");

        txtRS1 = new Text("-----------------------------");
        txtR = new Text("Random Traders");
        txtRA = new Text("Apple Stock: " + randTraders.get(0).getStocks());
        txtRM = new Text("Money: $" + moneyFormat(randTraders.get(0).getCash()));
        double avg = moneyFormat(getRandAvg());
        txtRAS = new Text("Avg. Assets: $" + moneyFormat(avg));
        txtRASD = new Text("Stnd. Deviation: " + moneyFormat(getRandSD(avg)));
        txtRS2 = new Text("-----------------------------");

        vbox.getChildren().addAll(txtRS1, txtR, txtRA,
                txtRM, txtRAS, txtRASD, txtRS2);

        return vbox;
    }

    /**
     * Holds the Smart Trader info in one VBox
     * @return that VBox
     */
    private VBox addVBox2() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: orange;");

        txtRS3 = new Text("-----------------------------");
        txtSm = new Text("Smart Trader");
        txtSmA = new Text("Apple Stock: " + smartTrader.getStocks());
        txtSmM = new Text("Money: $" + moneyFormat(smartTrader.getCash()));
        txtSmS = new Text("Total Assets: $" + moneyFormat(smartTrader.getAssets()));
        txtRS4 = new Text("-----------------------------");

        vbox.getChildren().addAll(txtRS3, txtSm,
                txtSmA, txtSmM, txtSmS, txtRS4);

        return vbox;
    }

    /**
     * Holds the Student 1 Trader info in one VBox
     * @return that VBox
     */
    private VBox addVBox3() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: green;");

        txtRS5 = new Text("-----------------------------");
        txtS1 = new Text("Student 1 Trader");
        txtS1A = new Text("Apple Stock: " + s1Trader.getStocks());
        txtS1M = new Text("Money: $" + moneyFormat(s1Trader.getCash()));
        txtS1S = new Text("Total Assets: $" + moneyFormat(s1Trader.getAssets()));
        txtRS6 = new Text("-----------------------------");

        vbox.getChildren().addAll(txtRS5, txtS1, txtS1A,
                txtS1M, txtS1S, txtRS6);

        return vbox;
    }

    /**
     * Holds the Student 2 Trader info in one VBox
     * @return that VBox
     */
    private VBox addVBox4() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: blue;");

        txtRS7 = new Text("-----------------------------");
        txtS2 = new Text("Student 2 Trader");
        txtS2A = new Text("Apple Stock: " + s2Trader.getStocks());
        txtS2M = new Text("Money: $" + moneyFormat(s2Trader.getCash()));
        txtS2S = new Text("Total Assets: $" + moneyFormat(s2Trader.getAssets()));
        txtRS8 = new Text("-----------------------------");

        vbox.getChildren().addAll(txtRS7, txtS2, txtS2A,
                txtS2M, txtS2S, txtRS8);

        return vbox;
    }

    /**
     * Holds the Control Trader info in one VBox
     * @return that VBox
     */
    private VBox addVBox6() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: purple;");

        txtRS9 = new Text("-----------------------------");
        txtC = new Text("Control Trader");
        txtCA = new Text("Apple Stock: " + INITIAL_STOCK);
        txtCM = new Text("Money: $" + moneyFormat(INITIAL_CASH));
        txtCAS = new Text("Total Assets: $"
                + moneyFormat(INITIAL_CASH + INITIAL_STOCK * currValue));
        txtRS10 = new Text("-----------------------------");

        vbox.getChildren().addAll(txtRS9, txtC, txtCA,
                txtCM, txtCAS, txtRS10);

        return vbox;
    }

    /**
     * Holds the Technical Trader into in one VBox
     * @return that VBox
     */
    private VBox addVBox7() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: purple;");

        txtRS11 = new Text("-----------------------------");
        txtT = new Text("Technical Trader");
        txtTA = new Text("Apple Stock: " + techTrader.getStocks());
        txtTM = new Text("Money: $" + moneyFormat(techTrader.getCash()));
        txtTS = new Text("Total Assets: $"
                + moneyFormat(techTrader.getAssets()));
        txtRS12 = new Text("-----------------------------");

        vbox.getChildren().addAll(txtRS11, txtT, txtTA,
                txtTM, txtTS, txtRS12);

        return vbox;
    }

    /**
     * Places button and stock price into one VBox
     * @param btn the button for stopping the simulation
     * @return that VBox
     */
    private VBox addVBox5(Button btn) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: yellow;");

        aPrice = new Text("Apple Stock: $" + moneyFormat(currValue));

        vbox.getChildren().addAll(btn, aPrice);

        return vbox;
    }

    /**
     * Makes the stock prices LineChart
     * @return that LineChart
     */
    private LineChart makeLineChart() {
        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Stock Price");
        //creating the chart
        final LineChart<String, Number> lineChart
                = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Stock Prices");
        lineChart.setAnimated(false);
        //defining a series
        series = new XYChart.Series();
        series.setName("Apple");
        //populating the series with data
        for (int i = currData.size() - 20; i < currData.size(); i++) {
            String date = dates.get(i);
            String[] parts = date.split("/");
            series.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                    + " " + Integer.parseInt(parts[1]), currData.get(i)));
        }

        lineChart.setCreateSymbols(false);
        lineChart.getData().addAll(series);

        return lineChart;
    }

    /**
     * Makes the trader asset LineChart
     * @return that LineChart
     */
    private LineChart makeLineChart2() {
        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Assets");
        //creating the chart
        final LineChart<String, Number> lineChart
                = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Trader Assets");
        lineChart.setAnimated(false);
        //defining a series
        series4 = new XYChart.Series();
        series4.setName("Random Trader");

        series5 = new XYChart.Series();
        series5.setName("Smart Trader");

        series6 = new XYChart.Series();
        series6.setName("Student Trader 1");

        series7 = new XYChart.Series();
        series7.setName("Student Trader 2");

        series8 = new XYChart.Series();
        series8.setName("Control Trader");

        series9 = new XYChart.Series();
        series9.setName("Technical Trader");

        lineChart.setCreateSymbols(false);
        lineChart.getData().addAll(series4, series5, series6, series7, series8, series9);

        return lineChart;
    }

    /**
     * Initializes all ArrayLists
     */
    private static void initArrayLists() {
        currData = new ArrayList<>();
        stockPrices = new ArrayList<>();
        randTraders = new ArrayList<>();
        stockVolume = new ArrayList<>();
        currVolume = new ArrayList<>();
        currHigh = new ArrayList<>();
        allHigh = new ArrayList<>();
        currLow = new ArrayList<>();
        allLow = new ArrayList<>();
        dates = new ArrayList<>();
        fillPrices();
        for (int i = 0; i < NUM_RAND; i++) {
            randTraders.add(new RandomTrader(INITIAL_STOCK, INITIAL_CASH));
        }
        currDay = currData.size();
    }

    /**
     * Initializes all Traders
     */
    private void initTraders() {
        smartTrader = new SmartTrader(INITIAL_STOCK, INITIAL_CASH, Utility.toArray(currData));
        s1Trader = new Student1Smart(INITIAL_STOCK, INITIAL_CASH, currData, currVolume);
        s2Trader = new Student2(INITIAL_STOCK, INITIAL_CASH);
        techTrader = new TechTrader(INITIAL_STOCK, INITIAL_CASH, Utility.toArray(currData),
                Utility.toArray(currHigh), Utility.toArray(currLow));
    }

    /**
     * Fills the stockPrices ArrayList
     */
    private static void fillPrices() {
        try {
            boolean check = true;
            BufferedReader f = new BufferedReader(new FileReader("src/FinalSeniorProject/AAPL.txt"));
            f.readLine();

            while (true) {
                String s = f.readLine();
                if (s == null) {
                    break;
                } else if (check && s.contains("stop")) {
                    check = false;
                }
                StringTokenizer st = new StringTokenizer(s);
                dates.add(st.nextToken());
                st.nextToken();

                double high = Double.parseDouble(st.nextToken());
                allHigh.add(high);

                double low = Double.parseDouble(st.nextToken());
                allLow.add(low);

                double next = Double.parseDouble(st.nextToken());
                stockPrices.add(next);

                st.nextToken();
                double vol = Double.parseDouble(st.nextToken());
                stockVolume.add(vol);

                if (check) {
                    currData.add(next);
                    currVolume.add(vol);
                    currHigh.add(high);
                    currLow.add(low);
                }

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FinalSeniorProject.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(FinalSeniorProject.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Converts a number into a two decimal format
     * @param d the input number
     * @return the number in a two decimal format
     */
    private double moneyFormat(double d) {
        return (int) (d * 100) / 100.0;
    }

    /**
     * Calculates the average assets of the random traders
     * @return the average assets
     */
    private double getRandAvg() {
        double sum = 0;
        for (RandomTrader rand : randTraders) {
            sum += rand.getAssets();
        }
        return sum / randTraders.size();
    }

    /**
     * Calculates the standard deviation of the random trader assets
     * @param avg the average assets
     * @return the standard deviation
     */
    private double getRandSD(double avg) {
        double var = 0;
        for (RandomTrader rand : randTraders) {
            var += Math.pow(rand.getAssets() - avg, 2) / NUM_RAND;
        }
        double stanD = Math.pow(var, 0.5);

        return stanD;
    }

    /**
     * Runs the simulation
     */
    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter("src/FinalSeniorProject/data.txt")))) {

            long prevTime, delay, sleep;
            prevTime = System.currentTimeMillis();

            try {
                Thread.sleep(5000); //Makes the simulation wait for 5 seconds before starting
            } catch (InterruptedException exc) {
                System.out.println("Thread Interrupted");
            }

            while (proceed) {
                if (currDay >= stockPrices.size()) {
                    break;
                }
                currValue = stockPrices.get(currDay); //Sets the current stock price value
                nextDay(currDay); //Allows the traders to make trades for that day
                updateRandTrader(); //Updates the display for the Random Trader
                updateSmartTrader(); //Updates the display for the Smart Trader
                updateS1Trader(); //Updates the display for the Student 1 Trader
                updateS2Trader(); //Updates the display for the Student 2 Trader
                updateControlTrader(); //Updates the display for the Control Trader
                updateTechTrader(); //Updates the display for the Technical Trader

                aPrice.setText("Apple: $" + moneyFormat(currValue)); //Updates the stock price display

                String date = dates.get(currDay);
                String[] parts = date.split("/");
                series.getData().remove(0); //Removes old stock price data
                series.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                        .substring(0, 3) + " " + parts[1], currValue)); //Adds the stock price to the stock prices LineChart
                txt3.setText(parts[1]);
                txt2.setText(months[Integer.parseInt(parts[0]) - 1]); //Changes the date display

                //Removes old trader asset data
                if (series4.getData().size() > 20) {
                    series4.getData().remove(0);
                    series5.getData().remove(0);
                    series6.getData().remove(0);
                    series7.getData().remove(0);
                    series8.getData().remove(0);
                    series9.getData().remove(0);
                }
                
                //Adds trader asset data to the trader asset LineChart
                series4.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                        .substring(0, 3) + " " + parts[1], moneyFormat(getRandAvg())));
                series5.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                        .substring(0, 3) + " " + parts[1], moneyFormat(smartTrader.getAssets())));
                series6.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                        .substring(0, 3) + " " + parts[1], moneyFormat(s1Trader.getAssets())));
                series7.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                        .substring(0, 3) + " " + parts[1], moneyFormat(s2Trader.getAssets())));
                series8.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                        .substring(0, 3) + " " + parts[1], moneyFormat(INITIAL_CASH + INITIAL_STOCK * currValue)));
                series9.getData().add(new XYChart.Data(months[Integer.parseInt(parts[0]) - 1]
                        .substring(0, 3) + " " + parts[1], moneyFormat(techTrader.getAssets())));

                //Changes the year display
                if (!parts[2].equals(FIRST_YEAR.substring(2, 4))) {
                    if (Integer.parseInt(parts[2]) > 60) {
                        txt4.setText("19" + parts[2]);
                    } else {
                        txt4.setText("20" + parts[2]);
                    }
                }

                delay = System.currentTimeMillis() - prevTime;
                sleep = DELAY - delay;

                if (sleep < 0) {
                    sleep = 2;
                }

                //Saves trader data to a data file
                double avg = getRandAvg();
                out.println(randTraders.get(0).getStocks() + " "
                        + moneyFormat(randTraders.get(0).getCash()) + " "
                        + moneyFormat(avg) + " " + moneyFormat(getRandSD(avg)));
                out.println(smartTrader.getStocks() + " "
                        + moneyFormat(smartTrader.getCash()) + " "
                        + moneyFormat(smartTrader.getAssets()));
                out.println(s1Trader.getStocks() + " "
                        + moneyFormat(s1Trader.getCash()) + " "
                        + moneyFormat(s1Trader.getAssets()));
                out.println(s2Trader.getStocks() + " "
                        + moneyFormat(s2Trader.getCash()) + " "
                        + moneyFormat(s2Trader.getAssets()));
                out.println(techTrader.getStocks() + " "
                        + moneyFormat(techTrader.getCash()) + " "
                        + moneyFormat(techTrader.getAssets()));

                //Delays the simulation
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException exc) {
                    System.out.println("Thread Interrupted");
                }

                prevTime = System.currentTimeMillis();
                currDay++; //Increments the day
            }
        } catch (IOException ex) {
            Logger.getLogger(FinalSeniorProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Allows all traders to make a move
     * @param i the current day
     */
    private void nextDay(int i) {
        Trader.setCurrPrice(stockPrices.get(i)); //Sets current price of traders
        Trader.setCurrDay(i); //Sets current day of traders

        //Updates Smart Trader
        currData.add(stockPrices.get(i)); //Sets current stock price data of traders
        smartTrader.setData(Utility.toArray(currData));
        smartTrader.next();

        //Updates Student 2 Trader
        s2Trader.setData(currData);
        s2Trader.next();

        //Updates Student 1 Trader
        currVolume.add(stockVolume.get(i)); //Sets current volume data of traders
        s1Trader.setData(currData);
        s1Trader.setVolume(currVolume);
        s1Trader.next();

        //Updates Technical Trader
        currHigh.add(allHigh.get(i)); //Sets current stock price high data of traders
        currLow.add(allLow.get(i)); //Sets current stock price low data of traders
        techTrader.setData(Utility.toArray(currData));
        techTrader.setHigh(Utility.toArray(allHigh));
        techTrader.setLow(Utility.toArray(allLow));
        techTrader.next();

        //Updates Random Traders
        for (RandomTrader rand : randTraders) {
            rand.next();
        }
    }

    /**
     * Updates the Random Trader display
     */
    private void updateRandTrader() {
        txtRA.setText("Apple Stock: " + randTraders.get(0).getStocks());
        txtRM.setText("Money: $" + moneyFormat(randTraders.get(0).getCash()));
        double avg = getRandAvg();
        txtRAS.setText("Avg. Assets: $" + moneyFormat(avg));
        txtRASD.setText("Stnd. Deviation: " + moneyFormat(getRandSD(avg)));
    }

    /**
     * Updates the Smart Trader display
     */
    private void updateSmartTrader() {
        txtSmA.setText("Apple Stock: " + smartTrader.getStocks());
        txtSmM.setText("Money: $" + moneyFormat(smartTrader.getCash()));
        txtSmS.setText("Total Assets: $" + moneyFormat(smartTrader.getAssets()));
    }

    /**
     * Updates the Student 1 Trader display
     */
    private void updateS1Trader() {
        txtS1A.setText("Apple Stock: " + s1Trader.getStocks());
        txtS1M.setText("Money: $" + moneyFormat(s1Trader.getCash()));
        txtS1S.setText("Total Assets: $" + moneyFormat(s1Trader.getAssets()));
    }

    /**
     * Updates the Student 2 Trader display
     */
    private void updateS2Trader() {
        txtS2A.setText("Apple Stock: " + s2Trader.getStocks());
        txtS2M.setText("Money: $" + moneyFormat(s2Trader.getCash()));
        txtS2S.setText("Total Assets: $" + moneyFormat(s2Trader.getAssets()));
    }

    /**
     * Updates the Technical Trader display
     */
    private void updateTechTrader() {
        txtTA.setText("Apple Stock: " + techTrader.getStocks());
        txtTM.setText("Money: $" + moneyFormat(techTrader.getCash()));
        txtTS.setText("Total Assets: $" + moneyFormat(techTrader.getAssets()));
    }

    /**
     * Updates the Control Trader display
     */
    private void updateControlTrader() {
        txtCAS.setText("Total Assets: $"
                + moneyFormat(INITIAL_CASH + INITIAL_STOCK * currValue));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args); //launches the simulation
    }
}
