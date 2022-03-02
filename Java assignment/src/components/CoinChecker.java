package components;

public class CoinChecker {
	//Declare paid amount by user
	private float balance;
	private int numOf10sen;
	private int numOf20sen;
	private int numOf50sen;
	private float totalpaid;
	
	//Constructor
	public CoinChecker(){
		
	}
	//Add balance
	public void insertCoin(float coin) {
		balance += coin;
		if(coin == 0.1) {
			numOf10sen += 1;
		}else if(coin == 0.2) {
			numOf20sen += 1;
		}else if(coin == 0.5){
			numOf50sen += 1;
		}
	}
	//Show the price in two decimal form
	public String amountToPay(float price) {
		return String.format("%.2f", price-balance);
	}
	//Check the remaining amount
	public boolean checkBalance(float price) {
		boolean sufficient = true;
		if(balance>=price) {
			sufficient = false;
			totalpaid = balance;
			balance -= price;
		}
		return sufficient;
	}
	//Get balance
	public float getTotalPaid() {
		return totalpaid;
	}
	//Get amount of sen of the machine
	public int getNumOf10sen() {
		return numOf10sen;
	}
	public int getNumOf20sen() {
		return numOf20sen;
	}
	public int getNumOf50sen() {
		return numOf50sen;
	}
	//Set amount of sen of the machine
	public void setNumOf10sen(int coinAmt) {
		numOf10sen = coinAmt;
	}
	public void setNumOf20sen(int coinAmt) {
		numOf20sen = coinAmt;
	}
	public void setNumOf50sen(int coinAmt) {
		numOf50sen = coinAmt;
	}
	//Add amount of sen of the machine
	public void addNumOf10sen(int coinAmt) {
		numOf10sen += coinAmt;
	}
	public void addNumOf20sen(int coinAmt) {
		numOf20sen += coinAmt;
	}
	public void addNumOf50sen(int coinAmt) {
		numOf50sen += coinAmt;
	}
	//Return changes
	public int[] returnChanges() {
		float changes = balance;
		balance = 0;
		int[] coins = new int[3];
		while(changes >0) {
			if(numOf50sen > 0 && changes>=0.5) {
				changes -= 0.5f;
				numOf50sen -=1;
				coins[2] += 1;
			}
			else if(numOf20sen > 0 && changes>=0.2) {
				changes -= 0.2f;
				numOf20sen -=1;
				coins[1] += 1;
			}
			else if(numOf10sen > 0) {
				changes -= 0.1f;
				numOf10sen -=1;
				coins[0] += 1;
			}
			System.out.println(changes);
		}
		return coins;
	}
}
