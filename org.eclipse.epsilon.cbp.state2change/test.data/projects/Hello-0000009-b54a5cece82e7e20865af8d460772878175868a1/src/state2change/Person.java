package state2change;

public class Person {

	private Greet greet;
	private String name;
	
	public Person(String name){
		this.name = name;
		this.greet = new Greet();
	}
	
	public void greet(Person person){
		this.greet.hello(person.name);
	}
}
