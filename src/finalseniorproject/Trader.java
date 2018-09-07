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
public abstract class Trader {

    private int stocks; //Amount of stocks this Trader has
    private double cash; //Amount of cash this Trader has

    private static double currPrice; //Current price of stocks
    protected static int currDay; //Current day

    /**
     * Constructs a trader object with initial stocks and cash
     *
     * @param stocks initial stocks
     * @param cash initial cash
     */
    public Trader(int stocks, double cash) {
        this.stocks = stocks;
        this.cash = cash;
    }

    /**
     * Sells an amount of stocks equal to the amount desired or equal to the
     * maximum amount of stocks available, whichever is less
     *
     * @param toBeSold Amount of stocks desired to be sold
     */
    protected void sellStocks(int toBeSold) {
        if (toBeSold <= getStocks()) {
            stocks -= toBeSold;
            cash += toBeSold * currPrice;
        } else {
            cash += getStocks() * currPrice;
            stocks = 0;
        }
    }

    /**
     * Buys an amount of stocks equal to the amount desired or equal to the
     * maximum amount of stocks that can be bought with available cash,
     * whichever is less
     *
     * @param toBeBought Amount of stocks desired to be bought
     */
    protected void buyStocks(int toBeBought) {
        if (toBeBought * currPrice <= getCash()) {
            cash -= toBeBought * currPrice;
            stocks += toBeBought;
        } else {
            stocks += (int) (getCash() / currPrice);
            cash -= (int) (getCash() / currPrice) * currPrice;
        }
    }

    /**
     * Either buys or trade stocks, depending on the sign of amount
     *
     * @param amount Amount of stocks desired to be bought or sold
     */
    protected void tradeStocks(int amount) {
        if (amount < 0) {
            sellStocks(-amount);
        } else if (amount > 0) {
            buyStocks(amount);
        }
    }

    /**
     * Trader buys or sells stocks for that current day
     */
    public abstract void next();

    /**
     * @return the stocks
     */
    public int getStocks() {
        return stocks;
    }

    /**
     * @return the cash
     */
    public double getCash() {
        return cash;
    }
    
    /**
     * @return the cash value of stocks and cash
     */
    public double getAssets(){
        return stocks * currPrice + cash;
    }

    /**
     * @param aCurrPrice the currPrice to set
     */
    public static void setCurrPrice(double aCurrPrice) {
        currPrice = aCurrPrice;
    }

    /**
     * @param aCurrDay the currDay to set
     */
    public static void setCurrDay(int aCurrDay) {
        currDay = aCurrDay;
    }
    
}
