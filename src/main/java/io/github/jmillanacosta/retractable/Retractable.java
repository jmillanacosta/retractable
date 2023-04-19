package io.github.jmillanacosta.retractable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.jmillanacosta.retractable.classes.RetractedArticle;
import io.github.jmillanacosta.retractable.query.SearchEpmc;
import io.github.jmillanacosta.retractable.utils.EuropePMC.JsonUtils;

public class Retractable{
    //TODO: clean up main class and move code to the adequate classes
    //TODO: creates Derby database and sets up a webservice/API endpoint
    //TODO: add more sources/services besides EuropePMC, ask around
    //TODO: method to retrieve only new additions to the .json/future relational db
    public final static String PMC_FILEPATH = "data/ePMC_retracts.json";
    public final static String DATA_FILEPATH = "data/retractable.json";
    public static void main( String[] args ) throws Exception
    {   
        
        String query = "PUB_TYPE%3A%22retraction%20of%20publication%22";
        // Retrieve JsonArray for the query
        JsonArray resultEPmcQuery = SearchEpmc.searchQuery(query);
        // Write it to a file
        FileWriter file = new FileWriter(PMC_FILEPATH);
        file.write(resultEPmcQuery.toString());
        //System.out.println("Successfully wrote JSON array to file.");
        file.close();
        //TODO only a temporary pipeline of course
        JsonArray queriedEPmc = JsonUtils.openJson(PMC_FILEPATH);
        ArrayList<RetractedArticle> retractedArticles = JsonUtils.instantiateRetractedArticles(queriedEPmc);
       
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        int i = 0;
        for (RetractedArticle article : retractedArticles) {
            JsonObject articleJson = new JsonObject();
            i +=1;
            System.out.println(article.pmcid);
            articleJson.addProperty("article", article.id != null ? article.id : "");
            articleJson.addProperty("pmcid", article.pmcid != null ? article.pmcid : "");
            articleJson.addProperty("url", article.url != null ? article.url : "");
            articleJson.addProperty("retraction_reason", article.retractionReason != null ? article.retractionReason.toString() : "");
            jsonArray.add(articleJson);
}
        JsonObject json = new JsonObject();
        // Add the jsonArray to the root json object
        json.add("retracted_articles", jsonArray);

        String jsonString = gson.toJson(json);
        // Write JSON to file
            try {
                FileWriter fileWriter = new FileWriter(DATA_FILEPATH);
                fileWriter.write(jsonString);
                fileWriter.flush();
                fileWriter.close();
                System.out.println("JSON file created");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        
    }
            
}


  
