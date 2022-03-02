package components;
import java.util.Scanner;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class
import products.*;
import java.io.*;
import java.util.Arrays;

public class VendingMachine {
	//Declare number of drinks slot
	protected final int drinksLimit = 10;
	//Declare array of drinks' name
	protected String[] drinkName = new String[drinksLimit];
	//Declare array of drinks description
	protected String[] drinkDescription = new String[drinksLimit];
	//Declare array of price
	protected float[] price = new float[drinksLimit];
	//Declare array of drinks code , quantity
	protected int[] code = new int[drinksLimit],quantity = new int[drinksLimit];
	//Declare user drinks selection
	protected int selection;
	//Declare system password;
	protected String systemPassword;
	
	//Declare site status, to determine which site user wnant to go
	protected String site = "";
	//Instantiate scanner
	Scanner sc = new Scanner(System.in);
	//Instantiate coinchecker
	CoinChecker coinchecker = new CoinChecker();
	//Instantiate inventory
	Inventory[] inventory = new Inventory[drinksLimit];
	//Instantiate drinks
	Description[] drinks = new Description[drinksLimit];
	
	//Constructor
	VendingMachine() {
		this.drinkName = new String[drinksLimit] ;
		this.price = new float[drinksLimit];
		this.code = new int[drinksLimit];
		this.quantity = new int[drinksLimit];
		this.systemPassword = "123";
	}
	VendingMachine(String[] drinkName,String[] drinkDescription, float[] price,int[] code,int[] quantity,String password) {
		this.drinkName = drinkName;
		this.drinkDescription = drinkDescription;
		this.price = price;
		this.code = code;
		this.quantity = quantity;
		this.systemPassword = password;
	}
	public static File setFilePath(String filename) {
		//Set relative file path
		String filepath = new File("").getAbsolutePath()+"\\src\\components\\data\\"+filename;
		//Instantiate File
		File file = new File(filepath);
		return file;
	}
	//Check sufficient coins in the machine
	public boolean checkCoinAmt() {
		return (coinchecker.getNumOf10sen()*0.1+coinchecker.getNumOf20sen()*0.2+coinchecker.getNumOf50sen()*0.5)>=8;
	}
	//Print startup message
	public void startup() {
		System.out.println("Welcome to JK's Drinks Vending Machine");
		//Press enter to start
		System.out.println("Press ENTER to start.\n");
		sc.nextLine();
		System.out.println("----------------MENU----------------");
	}
	//Check drinks stock
	public String drinksStatus(int code) {
		String status;
		if(inventory[code-1].checkStatus()==false) {
			status = "Available";
		}else {
			status = "Out of stock";
		}
		return status;
	}
	//Print drinks
	public String displayMenu(String name,float price,int code) {
		return "#"+code+" "+name+" RM "+String.format("%.2f", price)+" ["+drinksStatus(code)+"]";
	}
	//Check drinks stock (boolean)
	public boolean checkAvailability(int code,int max) {
		boolean status=false;
		//If it is false it break the while loop, stop prompting user for selection
		if(selection>max || selection<=0) {
			System.out.println("Out Of range! Please enter the number from the menu.");
			status = true;
		}else {
			status = inventory[code-1].checkStatus();
			if(status) {
				System.out.println("Out of stock! Please select other drinks.");
			}
		}
		return status;
	}
	//Ask user to make selection
	public void promptForSelection() {
		//Check if the input has integer, if not keep looping until it gets a integer
		while(!sc.hasNextInt()) {
			site = sc.next();
			//If it gets string "admin" break the loop, and take user to admin site
			if(site.equals("admin")) {
				break;
			}
			System.out.println("Please enter a number.");
		}
		
	}
	//read number of coins
	public void readCoinAmt() {
		Scanner data = null;
		int[] coin = new int[3];
		try {
			data = new Scanner(setFilePath("coins.txt"));
			String[] p = data.nextLine().split(" ");
			for(int i=0;i<coin.length;i++) {
				coin[i]= Integer.parseInt(p[i]);
			}
			coinchecker.setNumOf10sen(coin[0]);
			coinchecker.setNumOf20sen(coin[1]);
			coinchecker.setNumOf50sen(coin[2]);
		}
		catch(FileNotFoundException e) {
			System.out.println(e);
		}
		finally {
			if(data != null) {
				//Close file
				data.close();
			}
		}
	}
	//Check coins sufficient
	public boolean checkSufficient(float price) {
		return coinchecker.checkBalance(price);
	}
	//Ask user to insert coins
	public void promptForCoins(float price) {
		System.out.println("Please insert coins. Selected drinks: "+drinks[selection-1].getName()+". Amount To Pay: RM "+amountToPay(price)+".\nEnter 'cancel' to go back to menu.");
		String coin = sc.next();
		//Non integer value will keep looping until it gets an integer
		while(!coin.equals("1") && !coin.equals("5") && !coin.equals("10") && !coin.equals("0.1") && !coin.equals("0.2") && !coin.equals("0.5")) {
			//check whether the user want to go back to menu
			if(!coin.equals("cancel")){
				System.out.println("Only 1, 5, 10, 0.1, 0.2, 0.5 are accepted!\nEnter 'cancel' to go back to menu.");
				coin = sc.next();
			}else {
				site = coin;
				return;
			}
		}
		coinchecker.insertCoin(Float.parseFloat(coin));
	}
	//Calculate the remaining amount 
	public String amountToPay(float price) {
		return coinchecker.amountToPay(price);
	}
	//Dispense drink
	public String drinkDispense(int code) throws FileNotFoundException {
		//deduct product's quantity
		inventory[code-1].deductStock(1);
		quantity[code-1]-=1;
		//update data
		updateData(drinkName,drinkDescription,price,this.code,quantity);
		return "Thank you for using me :), Here's your drinks:\n\n"+drinks[selection-1].generateImage(drinks[selection-1].getName(), drinks[selection-1].getPrice(), code);
	}
	//Return changes
	public void returnBalance() {
		int[] changes = coinchecker.returnChanges();
		//Check if there is changes
		if(changes[0]>0 || changes[1]>0 || changes[2]>0) {
			System.out.println("Here's your changes: "+changes[0]+" x 10 sen, "+changes[1]+" x 20 sen, "+changes[2]+" x 50 sen."+"\nHave a nice day.");
		}
	}
	//Update new password
		public void updateCoins() {
			PrintWriter pw = null;
			int[] coins = {coinchecker.getNumOf10sen(),coinchecker.getNumOf20sen(),coinchecker.getNumOf50sen()};
			try {
				File file = setFilePath("coins.txt");
				pw = new PrintWriter(new FileOutputStream(file, false));
				String coinAmt = Arrays.toString(coins).replace(",", "").replace("[", "").replace("]", "");
				pw.print(coinAmt);
			}
			catch(FileNotFoundException e) {
				System.out.println(e);
			}
			finally {
				if(pw != null) {
					pw.close();
				}
			}
			
		}
	//Update text file
	public void updateData(String[] drinkName,String[] drinkDescription, float[] price,int[] code,int[] quantity) {
		//Declare PrintWriter
		PrintWriter pw = null;
		try {
			//Set file name
			File file = setFilePath("drinks.txt");
			//Allow PrintWriter overwrite the current text file
			pw = new PrintWriter(new FileOutputStream(file, false));
			//Store all the updated data into String
			String newData = String.join(",", drinkName)+"\n";
			newData += String.join(",", drinkDescription)+"\n";
			String pricelist = Arrays.toString(price).replace(",", "").replace("[", "").replace("]", "");
			newData += pricelist+"\n";
			String quantitylist = Arrays.toString(quantity).replace(",", "").replace("[", "").replace("]", "");
			newData += quantitylist+"\n";
			String codelist = Arrays.toString(code).replace(",", "").replace("[", "").replace("]", "");
			newData += codelist;
			//Overwrite the current file
			pw.println(newData);
		}
		//Exceptions
		catch(FileNotFoundException e) {
			System.out.println(e);
		}
		//Finally
		finally {
			if(pw!=null) {
				//Close the PrintWriter
				pw.close();
			}
			
		}
		
	}
//Admin site
	//Prompt for admin password
	public boolean promptPassword(String ps) {
		//Status of validating the password
		boolean correctPassword = false;
		System.out.println("Password:");
		//Prompt for password
		String password = sc.next();
		//Check password
		if(password.equals(ps)) {
			correctPassword = true;
		}else {
			correctPassword = false;
		}
		return correctPassword;
	}
	//Prompt for admin selection
	public void promptAdminSelction() {
		System.out.println("\nPlease select a drink in the menu to edit by entering the drinks code. (Example: 1)\nEnter 'coin' to refill coins.\nEnter 'password' to change admin password.\nEnter 'history' to view transaction history.\nEnter 'logout' to logout.\nEnter 'shutdown' to turn off the machine.");
		//Keep looping until user enter a valid value which is 1 to 10
		while(sc.hasNext()) {
			//Check for integer
			while(!sc.hasNextInt()) {
				site = sc.next();
				if(site.equals("logout")||site.equals("password")||site.equals("shutdown")||site.equals("coin")||site.contentEquals("history")) {
					return;
				}
				System.out.println("Please enter a number.");
			}
			int val = sc.nextInt();
			//Check the input whether it is within the range
			if(val >0 && val<=drinksLimit) {
				selection = val;
				break;
			}else {
				System.out.println("Out of range! Please select a code from the menu.");
			}
		}
	}
//Change password
	//Prompt for new password
	public String promptNewPassword() {
		System.out.println("New password:");
		return sc.next();
	}
	//Update new password
	public void updateNewPassword(String password) {
		PrintWriter pw = null;
		try {
			File file = setFilePath("password.txt");
			pw = new PrintWriter(new FileOutputStream(file, false));
			this.systemPassword = password;
			pw.print(password);
		}
		catch(FileNotFoundException e) {
			System.out.println(e);
		}
		finally {
			if(pw != null) {
				pw.close();
			}
		}
		
	}
	//Update history
	public void updateHistory(int code) {
		PrintWriter pw = null;
		try {
			File file = setFilePath("history.txt");
			pw = new PrintWriter(new FileOutputStream(file, true));
			LocalDateTime currentDateTime = LocalDateTime.now();
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		    String formattedDateTime = formatter.format(currentDateTime);
			pw.println("Date time:" + formattedDateTime +" | "+"Selected drinks: "+drinkName[code-1]+" | "+"Paid: RM"+coinchecker.getTotalPaid());
		}
		catch(FileNotFoundException e) {
			System.out.println(e);
		}
		finally {
			if(pw != null) {
				pw.close();
			}
		}
		
	}
	//Print history
	public void printHistory() {
		Scanner data = null;
		try {
			data = new Scanner(setFilePath("history.txt"));
			System.out.println("\nHISTORY\n");
			while(data.hasNextLine()) {
				System.out.println(data.nextLine());
			}
		}
		catch(FileNotFoundException e) {
			System.out.println(e);
		}
		finally {
			if(data != null) {
				//Close file
				data.close();
			}
		}
	}
	//Refill coins
	public void refillCoins() {
		int[] senNum = {1,2,5};
		for(int i:senNum) {
			System.out.println("Please enter the amount of "+i+"0 sen you want to refill:");
			while(!sc.hasNextInt()) {
				sc.hasNext();
				System.out.println("Please enter an absolute number");
			}
			if(i==1) {
				coinchecker.addNumOf10sen(sc.nextInt());
			}else if(i==2) {
				coinchecker.addNumOf20sen(sc.nextInt());
			}else {
				coinchecker.addNumOf50sen(sc.nextInt());
			}
		}
		updateCoins();
	}
	//Prompt for restock or edit
	public void promptRestockOrEdit() {
		while(true) {
			System.out.println("Please enter 'restock' or 'edit' or 'delete' to continue.");
			site = sc.next();
			if(site.equals("restock")||site.equals("edit")||site.equals("delete")) {
				break;
			}
		}
	}
//Restock
	//Prompt for restock quantity
	public int promptRestockQuantity() {
		System.out.println("Enter the restock amount. Use negative number to deduct stock.");
		while(!sc.hasNextInt()) {
			sc.next();
			System.out.println("Please enter a number.");
		}
		return sc.nextInt();
	}
	//Update quantity
	public void restock(int code,int quantity) throws FileNotFoundException {
		inventory[code-1].restock(quantity);
		this.quantity[code-1]+=quantity;
		updateData(drinkName,this.drinkDescription,price,this.code,this.quantity);
		System.out.println("Updated successfully!");
	}
//Edit
	//prompt for new name
	public String promptForNewName() {
		System.out.print("Enter a new drink name: ");
		sc.nextLine();
		return sc.nextLine();
	}
	//prompt for new description
	public String promptForDescription() {
		System.out.print("Enter a new drink description: ");
		return sc.nextLine();
	}
	//prompt for new price
	public float promptForNewPrice() {
		System.out.print("Enter a new price: ");
		while(!sc.hasNextFloat()) {
			sc.next();
			System.out.println("Please enter a number.");
		}
		return sc.nextFloat();
	}
	//Update name and price
	public void edit(int code,String name,String description,float price) throws FileNotFoundException {
		drinks[code-1].editName(name);
		drinks[code-1].editPrice(price);
		drinks[code-1].editDescription(description);
		this.drinkName[code-1] = name;
		this.price[code-1] = price;
		this.drinkDescription[code-1] = description;
		updateData(this.drinkName,this.drinkDescription,this.price,this.code,this.quantity);
		System.out.println("Edited successfully!");
	}
//Delete
	public void delete(int code) throws FileNotFoundException {
		drinks[code-1].deleteDrink();
		drinks[code-1].deleteDescription();;
		inventory[code-1].emptyStock();
		this.drinkName[code-1] = drinks[code-1].getName();
		this.price[code-1] = drinks[code-1].getPrice();
		this.quantity[code-1] = inventory[code-1].getQuantity();
		this.drinkDescription[code-1] = "No description";
		updateData(this.drinkName,this.drinkDescription,this.price,this.code,this.quantity);
	}
//MAIN
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Declare variable for number of drinks
		int drinkNum;
		//Open drinks text file
		Scanner data = null;
		
		//Declare name and price and code arrays
		String[] drinkName = new String[10];
		String[] description = new String[10];
		float[] price = new float[10];
		int[] code = new int[10],quantity = new int[10];
		
		try {
			data = new Scanner(setFilePath("drinks.txt"));
			//Read name from text file
			drinkName = data.nextLine().split(",");
			//Read description from text file
			description = data.nextLine().split(",");
			
			//Read price from text file
	 		String[] p = data.nextLine().split(" ");
			for(int i=0;i<drinkName.length;i++) {
				price[i]= Float.parseFloat(p[i]);
			}
			//Read quantity from text file
			String[] q = data.nextLine().split(" ");
			for(int i=0;i<drinkName.length;i++) {
				quantity[i]= Integer.parseInt(q[i]);
			}
			//Read drinks code from text file
			String[] c = data.nextLine().split(" ");
			for(int i=0;i<drinkName.length;i++) {
				code[i]= Integer.parseInt(c[i]);
			}
		}
		catch(FileNotFoundException e) {
			System.out.println(e);
		}
		finally {
			if(data != null) {
				//Close file
				data.close();
			}
		}
		
		Scanner data2 = null;
		//Declare password 
		String password = "";
		
		try {
			data2 = new Scanner(setFilePath("password.txt"));
			password = data2.nextLine();
		}
		catch(FileNotFoundException e) {
			System.out.println(e);
		}
		finally {
			data2.close();
		}
		
		
		//Instantiate VendingMachine
		VendingMachine machine = new VendingMachine(drinkName,description,price,code,quantity,password);
		
		//Get number of drinks
		if(drinkName.length<machine.drinksLimit) {
			drinkNum = drinkName.length;
		}else {
			drinkNum = machine.drinksLimit;
		}
		//Insert data into Drinks type
		for(int i=0;i<drinkNum;i++) {
			machine.drinks[i] = new Description(machine.drinkName[i],machine.price[i],machine.code[i],machine.drinkDescription[i]);
			machine.inventory[i] = new Inventory(code[i],quantity[i]);
		}
		
		
		//Get coins amount of the machine
		machine.readCoinAmt();
		//System loop
		while(true) {
			//Print start up message
			machine.startup();
			//Display drinks menu
			for(int i=0;i<drinkNum;i++) {
				System.out.print(machine.displayMenu(machine.drinks[i].getName(), machine.drinks[i].getPrice(), machine.drinks[i].getCode()));
				System.out.print(machine.drinks[i].toString());
			}
			//Prompt for selection
			System.out.println("\nPlease select your drinks in the menu by entering the drinks code. (Example: 1)\nEnter 'admin' to enter into admin site.");
			do{
				machine.promptForSelection();
				//If 'admin' entered break the loop
				if(machine.site.equals("admin")) {
					break;
				}
				machine.selection = machine.sc.nextInt();
			}while(machine.checkAvailability(machine.selection,drinkNum)); 
			//admin site
			if(machine.site.equals("admin")) {
			//check password
				//Correct password
				if(machine.promptPassword(machine.systemPassword)) {
					System.out.println("Correct Password!");
					//admin system loop
					while(true) {
						//Display menu
						System.out.println("\n############# ADMIN #############\n");
						for(int i=0;i<drinkNum;i++) {
							System.out.print(machine.displayMenu(machine.drinks[i].getName(), machine.drinks[i].getPrice(), machine.drinks[i].getCode())+" Stock: "+machine.inventory[i].getQuantity());
							System.out.print(machine.drinks[i].toString());
						}
						//Prompt for admin selection
						machine.promptAdminSelction();
						//if enter logout or shutdown break the loop
						if(machine.site.equals("logout")||machine.site.equals("shutdown")) {
							break;
						}
						//if enter change password
						if(machine.site.equals("password")) {
							machine.updateNewPassword(machine.promptNewPassword());
						}
						//if enter history
						else if(machine.site.equals("history")) {
							machine.printHistory();
							System.out.println("\nPress ENTER to go back.");
							machine.sc.nextLine();
							machine.sc.nextLine();
						}
						else if(machine.site.equals("coin")) {
							machine.refillCoins();
						}
						else {
							//Prompt for function
							machine.promptRestockOrEdit();
						//Check function entered
							//Restock
							if(machine.site.equals("restock")) {
								machine.restock(machine.selection, machine.promptRestockQuantity());
							}
							//Edit
							if(machine.site.equals("edit")){
								machine.edit(machine.selection, machine.promptForNewName(),machine.promptForDescription(), machine.promptForNewPrice());
							}
							//Delete
							if(machine.site.equals("delete")) {
								machine.delete(machine.selection);
							}
						}
					}
				}
				//Wrong password
				else {
					System.out.println("Wrong Password!\n");
				}
				//shutdown the system
				if(machine.site.equals("shutdown")) {
					System.out.println("The system is shutting down.");
					break;
				}
			}else {
				if(machine.checkCoinAmt()) {
					//Prompt for coins
					while(machine.checkSufficient(machine.drinks[machine.selection-1].getPrice())) {
						machine.promptForCoins(machine.drinks[machine.selection-1].getPrice());
						if(machine.site.equals("cancel")) {
							//Update site status *in case next loop perform 'cancel' again
							break;
						}
					}
					if(!machine.site.equals("cancel")) {
						//Dispense drinks
						System.out.println(machine.drinkDispense(machine.drinks[machine.selection-1].getCode()));
						//Update history
						machine.updateHistory(machine.selection);						
					}
					//Return changes
					machine.site = "";
					machine.returnBalance();
					System.out.println("\n######### NEXT TRANSACTION #########\n");
					machine.updateCoins();	
					machine.sc.nextLine();
				}else {
					System.out.println("\n########## OUT OF SERVICE ##########");
					System.out.println("## INSUFFICIENT COINS FOR CHANGES ##\n");
					machine.sc.nextLine();
				}
			}
		}
	}

}
