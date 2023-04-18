package io.github.jmillanacosta.retractable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import io.github.jmillanacosta.retractable.classes.RetractedArticle;
import io.github.jmillanacosta.retractable.utils.JsonUtils;

public class Retractable{
    //TODO: clean up main class and move code to the adequate classes
    //TODO: creates Derby database and sets up a webservice/API endpoint
    //TODO: add more sources/services besides EuropePMC, ask around
    //TODO: method to retrieve only new additions to the .json/future relational db
    public final static String FILEPATH = "data/retracted.json";
    public static void main( String[] args ) throws Exception
    {   
        
        //String query = "PUB_TYPE%3A%22retraction%20of%20publication%22";
        // Retrieve JsonArray for the query
        //JsonArray result = SearchEpmc.searchQuery(query);
        // Write it to a file :)
        //FileWriter file = new FileWriter("data/retracted.json");
        //file.write(result.toString());
        //System.out.println("Successfully wrote JSON array to file.");
        //file.close();
        JsonArray result = JsonUtils.openJson(FILEPATH);
        ArrayList<RetractedArticle> retractedArticles = JsonUtils.instantiateRetractedArticles(result);
        for (RetractedArticle article : retractedArticles){

            System.out.println(article.id);
         
        }
        // Create Gson object
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        // Convert list of RetractedArticle objects to JSON
        String json = gson.toJson(retractedArticles);
        
        // Write JSON to file
        try {
            FileWriter fileWriter = new FileWriter("data/retracted_reason.json");
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
            System.out.println("JSON file created: data/retracted_reason.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
            
}


  
