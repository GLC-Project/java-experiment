package core;

import coin.Coin;

/**
 * @author Amir Eslampanah
 * 
 */
public class MiningHandler {

    private Coin curCoin;

    /**
     * @param coin
     */
    public MiningHandler(Coin coin) {
	// TODO Auto-generated constructor stub
	this.setCurCoin(coin);
    }

    /**
     * @return the curCoin
     */
    public Coin getCurCoin() {
	return this.curCoin;
    }

    /**
     * @param curCoin1
     *            the curCoin to set
     */
    public void setCurCoin(Coin curCoin1) {
	this.curCoin = curCoin1;
    }

    /**
     * 
     */
    public void start() {

    }

}
