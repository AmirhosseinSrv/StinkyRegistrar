package domain;

import java.util.ArrayList;

public class ViolationException {
	private final ArrayList<String> errors;

	public ViolationException(){
		errors = new ArrayList<>();
	}

	public void addError(String error, int indentation) {
		errors.add(error);
	}

	public void addError(String error) {
		addError(error, 1);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String e : errors) {
			sb.append("\n").append("There is an error: ").append(e);
		}
		sb.delete(0, 1);
		return sb.toString();
	}
}