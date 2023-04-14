package io.github.jmillanacosta.retractable.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.jmillanacosta.retractable.classes.RetractedArticle;


public class JsonUtils {
    public static JsonArray openJson() throws IOException {
        Gson gson = new Gson();
        Reader reader = new FileReader("data/retracted.json");
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
            String url = "https://europepmc.org/article/" + source + "/" + source_id;
            JsonElement pmcid_el = jsonObject.get("pmcid");
            //if (!pmcid_el.isJsonNull()) {
            //    String pmcid = pmcid_el.getAsString();
            //    retractedArticle.setPmcId(pmcid);
            //}
            //JsonElement hasFullText = jsonObject.get("fullTextIdList");
            //TODO: if (!hasFullText.isJsonNull()) {
            //    String fullTextId = hasFullText.getAsJsonObject().getAsString();
                
            //    String fullTextXMLBase = "https://www.ebi.ac.uk/europepmc/webservices/rest/%s/fullTextXML"; 
            //    String fullTextXML = String.format(fullTextXMLBase, fullTextId);
            //}
            String id = source + source_id;
            retractedArticle.setId(id);
            retractedArticle.setURL(url); //TODO set url
            retractedArticle.setArticleAbstract(""); //TODO set abstract
            retractedArticle.setRetractionReason(); //TODO set retraction reason

            
        }
    
        return retractedArticles;
    }

   
}
