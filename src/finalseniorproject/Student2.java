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
public class Student2 extends Trader {
    //Constant
    private final int STOCK_CONSTANT = 100;
    
    private ArrayList<Double> data; //Stock price data
    
    private int oversold; //Number of times oversold
    private int overbought; //Number of times overbought
    private boolean check; //Check if recently oversold
    
    /**
     * Constructs a Student2 Trader with initial stocks and cash
     * @param stocks
     * @param cash 
     */
    public Student2(int stocks, double cash){
        super(stocks, cash);
    }
    
    /**
     * Buys or sells stocks based on oversold and overbought values
     */
    @Override
    public void next(){
        double RSI = Utility.calculateRSI(14, data);
        
        if(RSI > 70){
            overbought++;
            oversold = 0;
        } else if (RSI < 30) {
            oversold++;
            overbought = 0;
        }
        
        if(oversold >= 2){
            tradeStocks(STOCK_CONSTANT);
            check = true;
            oversold = 0;
        } else if(check && overbought >= 1){
            check = false;
            tradeStocks(-STOCK_CONSTANT);
            overbought = 0;
        }
    }
    
    /**
     * Sets stock price data
     * @param data stock price data
     */
    public void setData(ArrayList<Double> data){
        this.data = data;
    }
}
