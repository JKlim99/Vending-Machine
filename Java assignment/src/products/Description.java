package products;

public class Description extends Drinks{
	String description;
	
	public Description(String name,float price,int code,String d) {
		super.name = name;
		super.price = price;
		super.code = code;
		this.description = d;
	}
	@Override
	public String toString() {
		return " | Description: "+description+"\n";
	}
	public void editDescription(String d) {
		this.description = d;
	}
	public void deleteDescription() {
		this.description = "No Description";
	}
}
