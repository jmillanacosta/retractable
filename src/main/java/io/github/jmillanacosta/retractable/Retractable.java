package io.github.jmillanacosta.retractable;
import java.io.FileWriter;
import java.util.ArrayList;

import com.google.gson.JsonArray;

import io.github.jmillanacosta.retractable.classes.RetractedArticle;
import io.github.jmillanacosta.retractable.query.SearchEpmc;
import io.github.jmillanacosta.retractable.utils.JsonUtils;

public class Retractable{
    public final static String FILEPATH = "data/retracted.json";
    public static void main( String[] args ) throws Exception
    {   
        //TODO: query as system args
        String query = "PUB_TYPE%3A%22retraction%20of%20publication%22";
        // Retrieve JsonArray for the query
        JsonArray result = SearchEpmc.searchQuery(query);
        // Write it to a file :)
        FileWriter file = new FileWriter("data/retracted.json");
        file.write(result.toString());
        System.out.println("Successfully wrote JSON array to file.");
        file.close();
        //TODO: instantiate all retracted articles
        ArrayList<RetractedArticle> retractedArticles = JsonUtils.instantiateRetractedArticles(result);


  
    
    }
}