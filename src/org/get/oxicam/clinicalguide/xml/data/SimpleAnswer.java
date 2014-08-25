package org.get.oxicam.clinicalguide.xml.data;

public class SimpleAnswer extends AbstractAnswer {
	public final String answerId;
	public final String questionId;
	public final String value;
	public final String constraint;
	
	public SimpleAnswer(String answerId, String questionId, String value, String constraint) {
		this.answerId = answerId;
		this.questionId = questionId;
		this.value = value;
		this.constraint = constraint;
	}
	
	public boolean isSameAnswer(Answer answer) {
		boolean a = false;
		boolean b = questionId.equals(answer.question.questionId);
		if (constraint.isEmpty()) {
			a = value.equals(answer.value.toString());
		}
		else if (constraint.equals("ge")) {
			a = value.compareTo(answer.value.toString()) <= 0;
		}
		else if (constraint.equals("le")) {
			a = value.compareTo(answer.value.toString()) >= 0;
		}
		else if (constraint.equals("gt")) {
			a = value.compareTo(answer.value.toString()) < 0;
		}
		else if (constraint.equals("lt")) {
			a = value.compareTo(answer.value.toString()) > 0;
		}
		return (a && b);
	}
	
	public boolean checkAge(float age) {
		boolean a = false;
		float val = Float.valueOf(value);
		if (constraint.isEmpty()) {
			a = (age == val);
		}
		else if (constraint.equals("ge")) {
			a = (age >= val);
		}
		else if (constraint.equals("le")) {
			a = (age <= val);
		}
		else if (constraint.equals("gt")) {
			a = (age > val);
		}
		else if (constraint.equals("lt")) {
			a = (age < val);
		}
		return a;
	}
}
