package org.get.oxicam.clinicalguide.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.LoginActivity;
import org.get.oxicam.clinicalguide.xml.data.AbstractAnswer;
import org.get.oxicam.clinicalguide.xml.data.Annotation;
import org.get.oxicam.clinicalguide.xml.data.CombinedAnswer;
import org.get.oxicam.clinicalguide.xml.data.FollowUp;
import org.get.oxicam.clinicalguide.xml.data.Info;
import org.get.oxicam.clinicalguide.xml.data.Option;
import org.get.oxicam.clinicalguide.xml.data.PatientAttribute;
import org.get.oxicam.clinicalguide.xml.data.Question;
import org.get.oxicam.clinicalguide.xml.data.Questionnaire;
import org.get.oxicam.clinicalguide.xml.data.SimpleAnswer;
import org.get.oxicam.clinicalguide.xml.data.Symptom;
import org.get.oxicam.clinicalguide.xml.data.Treatment;
import org.get.oxicam.clinicalguide.xml.data.TreatmentAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class CGParser {

	private final Document mDom;
	private final String CGfilename = "imciPneumonia.xml"; //"clinicalguide.xml";
	private boolean encryptXML=false; //true;  //use encrypt-decrypt XML
	private static boolean locationXML=false;  //location of XML file, true for SD card, false for assets folder
	
	public CGParser(Context context) {
		Document document = null;
		InputStream inputStream = null;
		if (encryptXML) {
			inputStream = XMLHandler.getDecryptedXMLInputStream(context,
					CGfilename);
		} else {
			inputStream = XMLHandler.getXMLInputStream(context,
					CGfilename);
		}
		if (inputStream != null) {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// deploy images from assets to sdcard if using assets
			if (!locationXML){
				copyAssetsToSD("icons");
				copyAssetsToSD("images");
			}
		}

		if (document == null) {
			Log.e("Parser", "failed loading " +CGfilename );
		} else {
			if (!locationXML){
			Log.v("Parser", "Loaded asset " +CGfilename +" sucessfully.");
			Toast.makeText(context,
                    String.format("Sucessfully loaded " + CGfilename +" from assets into memory." ),
                    Toast.LENGTH_SHORT).show(); 
			} else {
				Log.v("Parser", "Loaded SDcard " +CGfilename +" sucessfully.");
				Toast.makeText(context,
	                    String.format("Sucessfully loaded " + CGfilename +" from SDcard into memory." ),
	                    Toast.LENGTH_SHORT).show(); 
			}
				
		}
		mDom = document;
	}

	public ArrayList<PatientAttribute> getPatientDetails() {
		ArrayList<PatientAttribute> patientDetails = new ArrayList<PatientAttribute>();
		NodeList patientAttributes = mDom
				.getElementsByTagName("patientattribute");
		for (int i = 0; i < patientAttributes.getLength(); i++) {
			PatientAttribute patientAttribute = getPatientAttribute((Element) patientAttributes
					.item(i));
			patientDetails.add(patientAttribute);
		}
		return patientDetails;
	}

	public PatientAttribute getPatientAttribute(Element element) {
		String name = element.getAttribute("name");
		String value = element.getAttribute("value");
		String answerType = element.getAttribute("answerType");
		return new PatientAttribute(name, value, answerType);
	}

	public ArrayList<Symptom> getMainSymptoms() {
		ArrayList<Symptom> mainSymptoms = new ArrayList<Symptom>();
		if (mDom != null) {
			NodeList symptoms = mDom.getElementsByTagName("symptom");
			for (int i = 0; i < symptoms.getLength(); i++) {
				Symptom mainSymptom = getSymptom((Element) symptoms.item(i));
				mainSymptoms.add(mainSymptom);
			}
		} else {
			Log.e("Parser", "mDom is null");
		}
		return mainSymptoms;
	}

	public ArrayList<Option> getOptions(Element element) {
		ArrayList<Option> options = new ArrayList<Option>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) childNode;
				if (child.getTagName().equals("options")) {
					NodeList optionChildNodes = child.getChildNodes();
					for (int j = 0; j < optionChildNodes.getLength(); j++) {
						Node optionChildNode = optionChildNodes.item(j);
						if (optionChildNode.getNodeType() == Node.ELEMENT_NODE) {
							Element optionChild = (Element) optionChildNode;
							if (optionChild.getTagName().equals("option")) {
								Option option = getOption(optionChild);
								options.add(option);
							}
						}
					}
				}
			}
		}
		// NodeList optionList = element.getElementsByTagName("option");
		// for (int i = 0; i<optionList.getLength(); i++) {
		// Option option = getOption((Element)optionList.item(i));
		// options.add(option);
		// }
		/*
		 * Element optionsNode = null; NodeList children =
		 * element.getChildNodes(); for (int i = 0; i<children.getLength(); i++)
		 * { Node child = children.item(i);
		 * if(child.getNodeName().equals("options")) { optionsNode =
		 * (Element)child; break; } }
		 * 
		 * if(optionsNode != null) { children = optionsNode.getChildNodes(); for
		 * (int i = 0; i<children.getLength(); i++) { Node child =
		 * children.item(i); if(child.getNodeName().equals("option")) { Option
		 * option = getOption((Element)child); options.add(option);
		 * Log.d("Parser", "found option: " + option.nextId); } } }
		 */
		return options;
	}

	public Option getOption(Element element) {
		String nextId = element.getAttribute("nextId");
		String answerRef = element.getAttribute("ref");
		if (answerRef.isEmpty()) {
			return new Option(null, nextId);
		}
		AbstractAnswer answer = getAnswer(answerRef);
		return new Option(answer, nextId);

	}

	public AbstractAnswer getAnswer(String answerId) {
		Element element = mDom.getElementById(answerId);
		if (element.hasChildNodes()) {
			return getCombinedAnswer(element);
		} else {
			return getSimpleAnswer(element);
		}
	}

	public CombinedAnswer getCombinedAnswer(Element element) {
		String answerId = element.getAttribute("id");
		ArrayList<HashMap<String, Object>> answers = new ArrayList<HashMap<String, Object>>();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			HashMap<String, Object> answerHM = new HashMap<String, Object>();
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) childNode;
				answerHM.put("type", child.getTagName());
				ArrayList<AbstractAnswer> answerList = new ArrayList<AbstractAnswer>();
				NodeList answerRefs = child.getElementsByTagName("answerRef");
				for (int j = 0; j < answerRefs.getLength(); j++) {
					Element answerRef = (Element) answerRefs.item(j);
					String answerRefId = answerRef.getAttribute("ref");
					AbstractAnswer answer = getAnswer(answerRefId);
					answerList.add(answer);
				}
				answerHM.put("answers", answerList);
				answers.add(answerHM);
			}
		}
		return new CombinedAnswer(answerId, answers);
	}

	public SimpleAnswer getSimpleAnswer(Element element) {
		String answerId = element.getAttribute("id");
		String questionId = element.getAttribute("for");
		String value = element.getAttribute("value");
		String constraint = element.getAttribute("constraint");
		return new SimpleAnswer(answerId, questionId, value, constraint);
	}

	public Symptom getSymptom(Element element) {
		String name = element.getAttribute("name");
		String symptomId = element.getAttribute("id");
		String icon = element.getAttribute("icon");
		ArrayList<Questionnaire> questionnaires = getQuestionnaires(element);
		ArrayList<Option> options = getOptions(element);
		ArrayList<Treatment> treatments = getTreatments(element);
		FollowUp followUp = getFollowUp(element);
		return new Symptom(symptomId, name, questionnaires, options,
				treatments, followUp, icon);
	}

	public FollowUp getFollowUp(Element element) {
		ArrayList<Questionnaire> questionnaireList = new ArrayList<Questionnaire>();
		ArrayList<Option> options = new ArrayList<Option>();

		// element is a symptom node and has a name, which is used as
		// questionnaire title
		String title = "Follow-Up - " + element.getAttribute("name");

		NodeList followUp = element.getElementsByTagName("followup");
		for (int i = 0; i < followUp.getLength(); i++) {
			Element fu = (Element) followUp.item(i);

			NodeList questionnaires = fu.getElementsByTagName("questionnaire");
			for (int j = 0; j < questionnaires.getLength(); j++) {
				Node questionnaireNode = questionnaires.item(j);
				Questionnaire questionnaire = getQuestionnaire(
						(Element) questionnaireNode, title);
				questionnaireList.add(questionnaire);
			}

			options.addAll(getOptions(fu));
		}

		return new FollowUp(questionnaireList, options);
	}

	public ArrayList<Questionnaire> getQuestionnaires(Element element) {
		// element is a symptom node and has a name, which is used as
		// questionnaire title
		String title = element.getAttribute("name");
		ArrayList<Questionnaire> questionnaireList = new ArrayList<Questionnaire>();

		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) childNode;
				if (child.getTagName().equals("questionnaire")) {
					Questionnaire questionnaire = getQuestionnaire(child, title);
					questionnaireList.add(questionnaire);
				}
			}
		}
		// NodeList questionnaires =
		// element.getElementsByTagName("questionnaire");
		// ArrayList<Questionnaire> questionnaireList = new
		// ArrayList<Questionnaire>();
		// for (int i = 0; i<questionnaires.getLength(); i++) {
		// Node questionnaireNode = questionnaires.item(i);
		// Questionnaire questionnaire =
		// getQuestionnaire((Element)questionnaireNode, title);
		// questionnaireList.add(questionnaire);
		// }
		/*
		 * NodeList children = element.getChildNodes(); ArrayList<Questionnaire>
		 * questionnaireList = new ArrayList<Questionnaire>(); for (int i = 0;
		 * i<children.getLength(); i++) { Node child = children.item(i);
		 * if(child.getNodeName().equals("questionnaire")) { Questionnaire
		 * questionnaire = getQuestionnaire((Element)child, title);
		 * questionnaireList.add(questionnaire); } }
		 */
		return questionnaireList;
	}

	public Questionnaire getQuestionnaire(Element element, String title) {
		String questionnaireId = element.getAttribute("id");
		String type = element.getAttribute("type");
		NodeList questionRefs = element.getElementsByTagName("questionRef");
		ArrayList<Question> questions = new ArrayList<Question>();
		for (int i = 0; i < questionRefs.getLength(); i++) {
			Element questionRef = (Element) questionRefs.item(i);
			String questionId = questionRef.getAttribute("ref");
			Question question = getQuestion(questionId);
			questions.add(question);
		}
		return new Questionnaire(questionnaireId, title, type, questions);
	}

	public Question getQuestion(String questionId) {
		Element element = mDom.getElementById(questionId);
		String answerType = element.getAttribute("answerType");
		String min = element.getAttribute("min");
		String link = element.getAttribute("link");
		NodeList children = element.getChildNodes();
		String label = null;
		Annotation annotation = null;
		Info info = null;
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) childNode;
				if (child.getTagName().equals("label")) {
					label = getText(child);
				} else if (child.getTagName().equals("info")) {
					info = getInfo(child);
				} else if (child.getTagName().equals("annotation")) {
					annotation = getAnnotation(child);
				}
			}
		}
		return new Question(questionId, label, answerType, min, info,
				annotation,link);
	}

	public Info getInfo(Element element) {
		String type = element.getAttribute("type");
		String label = getText(element);
		return new Info(type, label);
	}

	public Annotation getAnnotation(Element element) {
		NodeList children = element.getChildNodes();
		String label = null;
		String img = null;
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) childNode;
				if (child.getTagName().equals("label")) {
					label = getText(child);
				} else if (child.getTagName().equals("image")) {
					img = child.getAttribute("src");
				}
			}
		}
		return new Annotation(label, img);
	}

	public String getText(Element element) {
		String label = null;
		NodeList textNodes = element.getChildNodes();
		for (int j = 0; j < textNodes.getLength(); j++) {
			Node textNode = textNodes.item(j);
			if (textNode.getNodeType() == Node.TEXT_NODE) {
				label = textNode.getNodeValue().trim();
			}
		}
		return label;
	}

	public ArrayList<Questionnaire> getGeneralQuestionnaires() {
		ArrayList<Questionnaire> questionnaires = new ArrayList<Questionnaire>();
		Element elem = mDom.getElementById("generalQuestionnaires");
		String title = elem.getAttribute("name");

		NodeList questionnaireNodeList = elem
				.getElementsByTagName("questionnaire");
		for (int i = 0; i < questionnaireNodeList.getLength(); i++) {
			Node questionnaireNode = questionnaireNodeList.item(i);
			Questionnaire questionnaire = getQuestionnaire(
					(Element) questionnaireNode, title);
			questionnaires.add(questionnaire);
		}
		return questionnaires;
	}

	public String getGeneralQuestionnaireName() {
		return mDom.getElementById("generalQuestionnaires")
				.getAttribute("name");
	}

	public ArrayList<Treatment> getTreatments(Element element) {
		ArrayList<Treatment> treatments = new ArrayList<Treatment>();
		NodeList treatmentRefs = element.getElementsByTagName("treatmentRef");
		for (int i = 0; i < treatmentRefs.getLength(); i++) {
			Element treatmentRef = (Element) treatmentRefs.item(i);
			String treatmentId = treatmentRef.getAttribute("ref");
			Treatment treatment = getTreatment(treatmentId);
			treatments.add(treatment);
		}
		return treatments;
	}

	public Treatment getTreatment(String treatmentId) {
		return getTreatment(mDom.getElementById(treatmentId));
	}

	public Treatment getTreatment(Element element) {
		String treatmentId = element.getAttribute("id");
		String classification = element.getAttribute("classification");
		ArrayList<TreatmentAction> treatmentActions = new ArrayList<TreatmentAction>();
		NodeList treatmentActionRefs = element
				.getElementsByTagName("treatmentActionRef");
		for (int i = 0; i < treatmentActionRefs.getLength(); i++) {
			Element treatmentActionRef = (Element) treatmentActionRefs.item(i);
			String treatmentActionId = treatmentActionRef.getAttribute("ref");
			Element treatmentAction = mDom.getElementById(treatmentActionId);
			treatmentActions.add(getTreatmentAction(treatmentAction));
		}
		return new Treatment(treatmentId, classification, treatmentActions);

	}

	public TreatmentAction getTreatmentAction(Element element) {
		String treatmentActionId = element.getAttribute("id");
		String label = null;
		Info info = null;
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) childNode;
				if (child.getTagName().equals("label")) {
					label = getText(child);
				} else if (child.getTagName().equals("info")) {
					info = getInfo(child);
				}
			}
		}
		return new TreatmentAction(treatmentActionId, label, info);
	}
	
	
	private void copyAssetsToSD(String foldername) {

		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		String basepath = ClinicalGuideActivity.getClinicalGuideDirectory().getAbsolutePath();
		AssetManager assetManager = LoginActivity.getActivity().getAssets();

		String[] files = null;

		File configdir = new File(basepath + "/" + foldername + "/");
		if (!configdir.exists()) {
			configdir.mkdirs();}
		//this overwrites the sd folder at each start. TODO

		try {
			files = assetManager.list(foldername);
		} catch (Exception e) {
			Log.e("read config ERROR", e.toString());
			e.printStackTrace();
		}
		for(int i=0; i<files.length; i++) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(""+foldername+"/" + files[i]);
				out = new FileOutputStream(basepath + "/"+foldername+"/" + files[i]);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch(Exception e) {
				Log.e("copy config ERROR", e.toString());
				e.printStackTrace();
			}       
		}
	}
	
	
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}
	
	public static void setLocationXML(boolean value){

		locationXML=value;
	}
}
