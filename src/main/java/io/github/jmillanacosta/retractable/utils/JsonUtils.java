package io.github.jmillanacosta.retractable.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.jmillanacosta.retractable.classes.RetractedArticle;


public class JsonUtils {
    public static JsonArray openJson(String filepath) throws IOException {
        Gson gson = new Gson();
        Reader reader = new FileReader(filepath);
        JsonElement json = gson.fromJson(reader, JsonElement.class);
        JsonArray JsonArray = json.getAsJsonArray();
        reader.close();
        return JsonArray;
    }
    public static ArrayList<RetractedArticle> instantiateRetractedArticles(JsonArray retractedJson) throws Exception{
        ArrayList<RetractedArticle> retractedArticles = new ArrayList<RetractedArticle>();
        // Iterate over the JSON array
        for (JsonElement jsonElement : retractedJson) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            RetractedArticle retractedArticle = new RetractedArticle();
            String source = jsonObject.get("source").getAsString();
            String source_id = jsonObject.get("id").getAsString();
            // TODO String url = "https://europepmc.org/article/" + source + "/" + source_id;
            JsonElement pmcid_el = jsonObject.get("pmcid");
            try{
                if (pmcid_el != null) {
                    retractedArticle.setPmcId(pmcid_el.getAsString());
                    retractedArticle.setArticleFullText();
                    retractedArticle.setRetractionReason();
                } else {
                    // TODO handle the case where pmcid_el is null
                } } catch (IndexOutOfBoundsException e) {
                    // TODO handle the IndexOutOfBoundsException here
                }
            
            String id = source + source_id;
            retractedArticle.setId(id);
            //retractedArticle.setURL(url); //TODO set url

            
        }
    
        return retractedArticles;
    }


   
}
