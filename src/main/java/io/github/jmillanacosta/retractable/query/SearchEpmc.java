package io.github.jmillanacosta.retractable.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SearchEpmc {
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String BASE_URL = "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=%s&resultType=idlist&cursorMark=%s&pageSize=%s&format=json";
    private final static String FIRST_URL = "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=%s&resultType=idlist&cursorMark=*&pageSize=1000&format=json";

    public static JsonObject streamToJson(String query, String url, String cursorMark, int pageSize) throws Exception{
        String url_formatted = String.format(url, query, cursorMark, pageSize);
        URL obj = new URL(url_formatted);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);    
        int responseCode = con.getResponseCode();
        // handle non 200 range responses
        if (responseCode < 200 || responseCode >= 300) {
        throw new Exception("Failed to retrieve data. Response code: " +    responseCode);
        }
        InputStream inputStream = con.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader  (inputStream, "UTF-8");
        BufferedReader in = new BufferedReader(inputStreamReader);

        String inputLine;
        StringBuffer response = new StringBuffer();
        // Append lines to response buffer
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // Parse JSON data using Gson 
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response.toString(), JsonObject.class);
        return json; 
    }
    public static JsonArray searchQuery(String query) throws Exception {
        // encode query
        // First request
        JsonObject firstJson = streamToJson(query, FIRST_URL, "*", 1);
        // Start stream
        int hitCount = firstJson.get("hitCount").getAsInt();
        // Calculate the number of requests needed to retrieve all results with pageSize = 1000
        int pageSize = 1000;
        int reqNum = (( (hitCount + pageSize) / pageSize ));

        JsonArray result = new JsonArray();
        String cursorMark = "*";
        System.out.println(reqNum);
        for (int i = 0; i <= reqNum; i++) {
            System.out.println("Request #" + i);
            System.out.println(cursorMark);
            JsonObject json = streamToJson(query, BASE_URL, cursorMark, pageSize);
            JsonArray resultList = json.getAsJsonObject("resultList").getAsJsonArray("result");
            for (JsonElement element : resultList) {
                result.add(element.getAsJsonObject());
            JsonElement nextCursorMarkElement = json.get("nextCursorMark");
            if (nextCursorMarkElement.isJsonNull()) {
                System.out.println("Done requesting data.");
                break;
            }
            cursorMark = json.get("nextCursorMark").getAsString();
            
            }
        }    
        return result;
   }
   //TODO: add method that only retrieves the latest additions and adds them to the json? or always download the whole list (better if some are removed...)?
}    

