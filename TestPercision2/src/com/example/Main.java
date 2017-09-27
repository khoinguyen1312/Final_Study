package com.example;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.IsCover;
import com.azureSQL;
import com.uniRest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

public class Main {

	public static String AuthorPreProcess 
		= "Bearer RE+oL0X8HZYPb21XFhGO6nrQg3zOK6wIxUSKjEymuumf9fwvgiJvCvq66ssilxMaDShNFtM1UIa9iSA8FI59xw==";
	public static String urlToPreProcess
		= "https://asiasoutheast.services.azureml.net/subscriptions/58da00ad08d24544b9707ca81b6f7604/services/11bda6cd15c642f9b39f6523fd65e593/execute?api-version=2.0&format=swagger";
	
	public static int BigCount = 77205;
	
	public static String nameOfTest = "LevelA";
/*	public static void main(String[] args) throws ClientProtocolException, IOException, JSONException, UnirestException, SQLException {
				
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String inputFromUser = br.readLine();
		
		while (!"@@@".equals(inputFromUser)) {
			handleMessage("someone", inputFromUser);
			inputFromUser = br.readLine();
		}
	}*/
	
	static void sendToSender(String senderID, String text) {
		System.out.println(text);
	}
	
/*	public static void main(String[] args) throws UnirestException {
		getJsonAfterPreprocess("You can join the club when you __ a bit  older.",
								"will have got",
								"will get",
								"get",
								"are getting");
	}*/

	/*private static void handleMessage(String senderID, String text) {
		
		if ("start".equals(text.toLowerCase())) {
			if (!senderOptions.containsKey(senderID)) {
				senderOptions.put(senderID, new ArrayList<>());
			}
			else {
				senderOptions.get(senderID).clear();
			}
			
			senderSentences.put(senderID, "");
			senderFirstMessage.put(senderID, true);
			senderIsReceivingMessage.put(senderID, true);
			
			sendToSender(senderID, "Start");
		}
		else {
			boolean isRecevingMessage = false;
			if (senderIsReceivingMessage.containsKey(senderID)) {
				isRecevingMessage = senderIsReceivingMessage.get(senderID);
			}
			
			if (isRecevingMessage) {
				if ("~~~".equals(text) || "!!!".equals(text)) {
					if ("!!!".equals(text)) {
						senderIsReceivingMessage.put(senderID, false);
					}
					
					if (!"".equals(senderSentences.get(senderID))) {
						String result = "";
						try {
							result = Calculate.findAnswerQuestion(senderSentences.get(senderID),
										senderOptions.get(senderID), new IsCover(), NGram);
						} catch (SQLException | UnirestException | JSONException e) {
							result = "Error: " + e.getMessage();
							e.printStackTrace();
						}
		
						sendToSender(senderID, result);				
		
						senderOptions.get(senderID).clear();
						senderSentences.put(senderID, "");
						senderFirstMessage.put(senderID, true);
					}
				}
				else if (senderFirstMessage.get(senderID)) {
					senderFirstMessage.put(senderID, false);
					senderSentences.put(senderID, text);
				}
				else {
					senderOptions.get(senderID).add(text);
				}
			}
			else {
				sendToSender(senderID, text);	
			}
		}
	}*/

	public static void main(String[] args) throws ClientProtocolException, IOException, JSONException, UnirestException, SQLException {
		long startTime = System.currentTimeMillis();
		String pathToFile = "H:/Bo_De/" + nameOfTest + ".tsv";
		
		azureSQL.init();

		TsvParserSettings settings = new TsvParserSettings();
		TsvParser parser = new TsvParser(settings);

		List<String[]> allRows = parser.parseAll(new FileReader(pathToFile));
		
		for (int N = 1; N <= 4; N++) {
			
			Collection<Object[]> answer = new ArrayList<>();
			
			for (int i = 1; i < allRows.size(); i++) {
				String[] question = allRows.get(i);
		
				String ID = question[0];
				String Content = question[1];
				String A = question[2];
				String B = question[3];
				String C = question[4];
				String D = question[5];			
	
				System.out.println(ID + ": " + Content);
				System.out.println("A: " + A +
							   "    B: " + B + 
							   "    C: " + C +
							   "    D: " + D);
				
				IsCover isCover = new IsCover();
				isCover.setTrue(true);
				
				String ans = findAnswerQuestion(Content, A, B, C, D, isCover, N).toString();
				
				System.out.println("    Ans:      " + ans + 
						         "\n    Is cover: " + isCover.toString() +
						         "\n    Ngram:    " + N);
				
				answer.add(new String[] {ID, ans, isCover.toString()});
			}
			
			String pathToAnswerFile = "H:/Bo_De/" + nameOfTest + "-Answer-" + N + ".csv";
			
			CsvWriter writer = new CsvWriter(new FileWriter(pathToAnswerFile), new CsvWriterSettings());
			writer.writeHeaders("ID", "Answer", "IsCover");
			writer.writeRowsAndClose(answer);
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Total Excute time: " + (endTime - startTime) + " milliseconds.");
	}
	
	private static Integer findAnswerQuestion(String Content, String A, String B, String C, String D, IsCover isCover, int n) throws SQLException, UnirestException {
		JSONObject jsonPreprocess = getJsonAfterPreprocess(Content, A, B, C, D);
		List<SimpleEntry<String, List<String>>> resultPreprocess = getResultFromPreProcess(jsonPreprocess, n);
		
		Set<String> allToken = new HashSet<>();

		for (SimpleEntry<String, List<String>> answer : resultPreprocess) {
			List<String> tokens = answer.getValue();
			for (String token : tokens) {
				allToken.add(token);
			}
		}

		Map<String, Integer> tableNGram = azureSQL.querry(allToken);
		
		List<SimpleEntry<String, Double>> result = new ArrayList<>();
		Map<String, Boolean> cover = new HashMap<>();
		
		for (SimpleEntry<String, List<String>> answer : resultPreprocess) {
			String key = answer.getKey();
			List<String> tokens = answer.getValue();
			
			boolean isCoverThisOption = true;
			
			Double percisionOfThisOption = 0.0;
			
			for (String token : tokens) {
				int tokenCount = 1;
				if (tableNGram.containsKey(token)) {
					tokenCount = tableNGram.get(token) + 1;
				}
				else {
					isCoverThisOption = false;
				}
				
				percisionOfThisOption += Math.log((double)tokenCount / (BigCount + 1));
			}
			
			result.add(new SimpleEntry<>(key, percisionOfThisOption));
			cover.put(key, isCoverThisOption);
		}
		
		String finalAnswer = result.get(0).getKey();
		Double max = result.get(0).getValue();
		
		for (SimpleEntry<String, Double> entry : result) {
			if (entry.getValue() > max) {
				finalAnswer = entry.getKey();
				max = entry.getValue();
			}
		}

		if ("A".equals(finalAnswer)) {
			isCover.setTrue(cover.get(finalAnswer));
			return 0;
		}
		if ("B".equals(finalAnswer)) {
			isCover.setTrue(cover.get(finalAnswer));
			return 1;
		}
		if ("C".equals(finalAnswer)) {
			isCover.setTrue(cover.get(finalAnswer));
			return 2;
		}
		isCover.setTrue(cover.get(finalAnswer));
		return 3;
	}
	
	private static JSONObject wrapToSendPreProcess(List<SimpleEntry<String, String>> listOfText) throws JSONException {
		JSONObject jsonToSend = new  JSONObject();
		JSONObject input1Out = new JSONObject();
		JSONArray input1 = new JSONArray();
		for (SimpleEntry<String, String> text : listOfText) {
			JSONObject jsonText = new JSONObject();
			jsonText.put("key", text.getKey());
			jsonText.put("text", text.getValue());
			input1.put(jsonText);
		}
		input1Out.put("input1", input1);
		jsonToSend.put("Inputs", input1Out);
		jsonToSend.put("GlobalParameters", new JSONObject());
		return jsonToSend;
	}
	
	private static List<SimpleEntry<String, List<String>>> getResultFromPreProcess(JSONObject json, int n) {
		List<SimpleEntry<String, List<String>>> result = new ArrayList<>();
		
		JSONArray output = json.getJSONObject("Results").getJSONArray("output1");
		for (int i = 0; i < output.length(); i++) {
			String key = output.getJSONObject(i).getString("key");
			String NGramsString = output.getJSONObject(i).getString("NGramsString");
			
			JSONArray ngramsJSON = new JSONArray(NGramsString);
			List<Object> ngramsObj = ngramsJSON.toList();
			List<String> ngrams = new ArrayList<>();
			
			for (Object obj : ngramsObj) {
				String gram = obj.toString();
				if ((gram.length() - gram.replace("_", "").length()) == n - 1) {
					ngrams.add("T.[" + obj.toString() + "]");
				}
			}			
			
			result.add(new SimpleEntry<>(key, ngrams));
		}
		
		return result;
	}
	
	private static JSONObject getJsonAfterPreprocess(String sentence, String A, String B, String C, String D) throws UnirestException {	
		
		A = A.trim();
		B = B.trim();
		C = C.trim();
		D = D.trim();		
		
		List<SimpleEntry<String, String>> listOfText = new ArrayList<>();
				
		int lastIndex = 0;
		int count = 0;

		String replaceA = sentence;
		String replaceB = sentence;
		String replaceC = sentence;
		String replaceD = sentence;
		
		String[] listA = A.split("\\.\\.\\.");
		String[] listB = B.split("\\.\\.\\.");
		String[] listC = C.split("\\.\\.\\.");
		String[] listD = D.split("\\.\\.\\.");
		
		while (lastIndex != -1) {

		    lastIndex = sentence.indexOf("__", lastIndex);

		    if (lastIndex != -1) {
				
				if ("-".equals(listA[count].trim())) { listA[count] = " "; }
				if ("-".equals(listB[count].trim())) { listB[count] = " "; }
				if ("-".equals(listC[count].trim())) { listC[count] = " "; }
				if ("-".equals(listD[count].trim())) { listD[count] = " "; }				
		    	
		    	replaceA = replaceA.replaceFirst("__", listA[count]);
		    	replaceB = replaceB.replaceFirst("__", listB[count]);
		    	replaceC = replaceC.replaceFirst("__", listC[count]);
		    	replaceD = replaceD.replaceFirst("__", listD[count]);
		    	
		    	sentence.replaceFirst("__", " ");
		    	
		        lastIndex += 1;
		        count++;
		    }
		}
		
		listOfText.add(new SimpleEntry<String, String>("A", replaceA));
		listOfText.add(new SimpleEntry<String, String>("B", replaceB));
		listOfText.add(new SimpleEntry<String, String>("C", replaceC));
		listOfText.add(new SimpleEntry<String, String>("D", replaceD));
		
		/*listOfText.add(new SimpleEntry<String, String>("A", sentence.replaceFirst("__", A)));
		listOfText.add(new SimpleEntry<String, String>("B", sentence.replaceFirst("__", B)));
		listOfText.add(new SimpleEntry<String, String>("C", sentence.replaceFirst("__", C)));
		listOfText.add(new SimpleEntry<String, String>("D", sentence.replaceFirst("__", D)));*/
		
		JSONObject jsonToSend = wrapToSendPreProcess(listOfText);
		
		return uniRest.uniRestPost(urlToPreProcess, AuthorPreProcess, jsonToSend);
	}
}
