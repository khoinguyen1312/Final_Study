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
	
	public static String nameOfTest = "LevelTOEFL";
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

		Collection<Object[]> answer = new ArrayList<>();
		
		for (int i = 1; i < allRows.size(); i++) {
			String[] question = allRows.get(i);
	
			String ID = question[0];
			String Content = question[1];
			String A = question[2];
			String B = question[3];
			String C = question[4];
			String D = question[5];			
			
			List<String> options = new ArrayList<>();
			options.add(A.trim());
			options.add(B.trim());
			options.add(C.trim());
			options.add(D.trim());

			System.out.println(ID + ": " + Content);
			System.out.println("A: " + A +
						   "    B: " + B + 
						   "    C: " + C +
						   "    D: " + D);
			
			Map<Integer, IsCover> isCover = new HashMap<>();
			
			String ans = findAnswerQuestion(Content, options);
			
			System.out.println("    Ans:      " + ans + 
					         "\n    Is cover: " + isCover.toString());
			
			Integer index = options.indexOf(ans);
			
			answer.add(new String[] {ID, index.toString()});
		}
		
			String pathToAnswerFile = "H:/Bo_De/" + nameOfTest + "-Answer-4-3-2-1" + ".csv";
			
			CsvWriter writer = new CsvWriter(new FileWriter(pathToAnswerFile), new CsvWriterSettings());
			writer.writeHeaders("ID", "Answer");
			writer.writeRowsAndClose(answer);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Total Excute time: " + (endTime - startTime) + " milliseconds.");
	}
	
	private static String findAnswerQuestion(String Content, List<String> options) throws SQLException, UnirestException {
				
		JSONObject jsonPreprocess = getJsonAfterPreprocess(Content, options);
		List<String> finalAnswer = new ArrayList<>();
		List<Double> max = new ArrayList<>(); 
		List<String> remainOptions = new ArrayList<>(options);
		int n = 4;
		
		while (n != 0) {		
			List<SimpleEntry<String, List<String>>> resultPreprocess = getResultFromPreProcess(jsonPreprocess, n);
			List<SimpleEntry<String, List<String>>> newResultPreprocess = new ArrayList<>();
			for (SimpleEntry<String, List<String>> entry : resultPreprocess) {
				if (remainOptions.contains(entry.getKey())) {
					newResultPreprocess.add(entry);
				}
			}
			resultPreprocess = newResultPreprocess;
			
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
			
			finalAnswer.clear();
			max.clear();
			max.add(-1000000000000000.0);
			
			for (SimpleEntry<String, Double> entry : result) {
				if  (entry.getValue().doubleValue() == max.get(0).doubleValue()) {
					finalAnswer.add(entry.getKey());
					max.add(entry.getValue());
				}				
				else if (entry.getValue().doubleValue() > max.get(0).doubleValue()) {
					finalAnswer.clear();
					max.clear();
					finalAnswer.add(entry.getKey());
					max.add(entry.getValue());
				}
			}
			
			if (finalAnswer.size() == 1) {
				return finalAnswer.get(0);
			}
			remainOptions = new ArrayList<>(finalAnswer);
			n--;
		}
		return finalAnswer.get(0);
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

	private static JSONObject getJsonAfterPreprocess(String sentence, List<String> options) throws UnirestException, JSONException {		
		
		List<SimpleEntry<String, String>> listOfText = new ArrayList<>();
		
		for (String option : options) {
			option = option.trim();
			
			int lastIndex = 0;
			int count = 0;
			
			String replace = sentence;

			String[] list = option.split("\\.\\.\\.");			

			while (lastIndex != -1) {

			    lastIndex = replace.indexOf("__", lastIndex);

			    if (lastIndex != -1) {
					
					if ("-".equals(list[count].trim())) {
						list[count] = " ";
					}
					
					replace = replace.replaceFirst("__", list[count]);
			    	
			        lastIndex += list[count].length();
			        count++;
			    }
			}
			
			listOfText.add(new SimpleEntry<String, String>(option, replace));
		}
		
		JSONObject jsonToSend = wrapToSendPreProcess(listOfText);
		
		return uniRest.uniRestPost(urlToPreProcess, AuthorPreProcess, jsonToSend);
	}
}
