package products;

public class Inventory {
	//Declare drinks quantity , code
	int quantity,code;
	
	//Default constructor
	public Inventory() {
		
	}
	//Constructor
	public Inventory(int code,int quantity) {
		this.code = code;
		this.quantity = quantity;
	}
	//Check drinks availability (boolean)
	public boolean checkStatus() {
		boolean status=true;
		if(quantity<=0) {
			status = true;
		}else {
			status = false;
		}
		return status;
	}
	//Restock
	public void restock(int quantity) {
		this.quantity += quantity;
	}
	//Deduct stock
	public void deductStock(int quantity) {
		this.quantity -= quantity;
	}
	//Get quantity
	public int getQuantity() {
		return quantity;
	}
	//Trigger when Drinks.delete() is called, to empty the stock while deleting the drink
	public void emptyStock() {
		this.quantity = 0;
	}
}
