package state2change;

public class Main {

	public static void main(String[] args) {

		Lecturer vladimir = new Lecturer("Vladimir");
		Student donald = new Student("Donald");
		vladimir.greet(donald);
		donald.greet(vladimir);
	}

}
