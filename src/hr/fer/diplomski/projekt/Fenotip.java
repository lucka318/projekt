package hr.fer.diplomski.projekt;

import java.util.ArrayList;
import java.util.List;

public class Fenotip {

	String name;
	List<String> positive;
	List<String> negative;

	public Fenotip(String name, List<String> positive, List<String> negative) {
		this.name = name;
		this.positive = new ArrayList<String>(positive);
		this.negative = new ArrayList<String>(negative);
	}

	@Override
	public String toString() {
		return "Fenotip [name=" + name + ", positive=" + positive
				+ ", negative=" + negative + "]";
	}

}
