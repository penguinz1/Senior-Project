/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalseniorproject;

/**
 *
 * @author JackN
 */
public class RandomTrader extends Trader {
    //Constant
    private final int MAX_VARIATION = 100;
    
    /**
     * Constructs a RandomTrader with initial stocks and cash
     * @param stocks initial stocks
     * @param cash initial cash
     */
    public RandomTrader(int stocks, double cash){
        super(stocks, cash);
    }
    
    /**
     * Buys or sells stocks randomly
     */
    @Override
    public void next(){
        tradeStocks((int)((Math.random() - 0.5) * MAX_VARIATION));
    }
}
