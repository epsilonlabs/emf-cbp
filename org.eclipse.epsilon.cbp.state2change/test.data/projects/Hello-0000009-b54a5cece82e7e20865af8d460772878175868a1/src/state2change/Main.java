package state2change;

public class Main {

	public static void main(String[] args) {

		Person vladimir = new Person("Vladimir");
		Person donald = new Person("Donald");
		vladimir.greet(donald);
		donald.greet(vladimir);
	}

}
