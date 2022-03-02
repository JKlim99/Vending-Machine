package products;

public abstract class Drinks {
	//Declare drink name
	protected String name;
	//Declare price
	protected float price;
	//Declare code
	protected int code;
	
	//Default Constructor
	public Drinks() {
		
	}
	//Constructor
	public Drinks(String name,float price,int code) {
		this.name = name;
		this.price = price;
		this.code = code;
	}
	//Get drinks name
	public String getName() {
		return name;
	}
	//Get drinks price
	public float getPrice() {
		return price;
	}
	//Get drinks code
	public int getCode() {
		return code;
	}
	//Generate drinks image pattern
	public String generateImage(String name,float price,int code) {
		String drinkImage = "";
		int size = 7;
		for(int i=0;i<size;i++) {
			if(i==(size-1)/2) {
				drinkImage += "# "+name+" #\n";
			}else {
				for(int x=0;x<name.length()+4;x++) {
					if(i == 1 || i == size-2) {
						drinkImage += "-";	
					}else if((i==0 && x==0) || (i==size-1 && x==name.length()+3)) {
						drinkImage += "/";
					}else if((i==0 && x==name.length()+3)||(i==size-1 && x==0)){
						drinkImage += "\\";
					}else {
						drinkImage += "#";	
					}
					
					if(x == name.length()+3) {
						drinkImage += "\n";
					}
				}
			
			}
		}
		return drinkImage;
	}
	//Edit drink name
	public void editName(String name) {
		this.name = name;
	}
	//Edit drink price
	public void editPrice(float price) {
		this.price = price;
	}
	//Delete drink, make it empty
	public void deleteDrink() {
		this.name = "Empty";
		this.price = 0.0f;
	}
}
