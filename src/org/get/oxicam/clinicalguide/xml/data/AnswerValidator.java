package org.get.oxicam.clinicalguide.xml.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.xml.CGParser;

import android.content.Context;

public class AnswerValidator {

	public static Treatment getTreatment(Context context, Symptom symptom, HashMap<String, Answer> answers, CGParser parser, long patientID) {
		for (Option option : symptom.options) {
			if (option.answer == null) {
				return parser.getTreatment(option.nextId);
			}
			AbstractAnswer answer = option.answer;
			if (answer instanceof SimpleAnswer) {
				String questionId = ((SimpleAnswer) answer).questionId;
				Answer actualAnswer = answers.get(questionId);
				if (((SimpleAnswer) answer).isSameAnswer(actualAnswer)) {
					return parser.getTreatment(option.nextId);
				}
			}
			else {
				if (fulfillsCombinedAnswer(context, (CombinedAnswer) answer, answers, patientID)) {
					return parser.getTreatment(option.nextId);
				}
			}
			
		}
		return null;
	}
	
	private static boolean fulfillsCombinedAnswer(Context context, CombinedAnswer cAnswer, HashMap<String, Answer> answers, long patientID) {
		boolean fulfilled = false;
		for (HashMap<String, Object> answerCombination : cAnswer.answers) {
			String type = (String) answerCombination.get("type");
			if (type.equals("choice")) {
				fulfilled = false;
			}
			else {
				fulfilled = true;
			}
			for (AbstractAnswer aAnswer: (ArrayList<AbstractAnswer>)answerCombination.get("answers")) {
				if (aAnswer instanceof SimpleAnswer) {
					SimpleAnswer sAnswer = (SimpleAnswer) aAnswer;
					if (sAnswer.questionId.equals("birthdate")) {
						Database db = new Database(context);
						float age = db.getPatientAge(patientID);
						if (type.equals("choice")) {
							fulfilled |= sAnswer.checkAge(age);
						}
						else {
							fulfilled &= sAnswer.checkAge(age);
						}
					}
					else {
						Answer actualAnswer = answers.get(sAnswer.questionId);
						if (type.equals("choice")) {
							fulfilled |= (((SimpleAnswer) aAnswer).isSameAnswer(actualAnswer));
						}
						else {
							fulfilled &= (((SimpleAnswer) aAnswer).isSameAnswer(actualAnswer));
						}
					}
				}
				else {
					if (type.equals("choice")) {
						fulfilled |= fulfillsCombinedAnswer(context, (CombinedAnswer) aAnswer, answers, patientID);
					}
					else {
						fulfilled &= fulfillsCombinedAnswer(context, (CombinedAnswer) aAnswer, answers, patientID);
					}
				}
			}
		}
		return fulfilled;
	}
}
