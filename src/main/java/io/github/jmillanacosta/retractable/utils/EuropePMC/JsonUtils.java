package io.github.jmillanacosta.retractable.utils.EuropePMC;

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
            JsonElement pmcid_el = jsonObject.get("pmcid");
            String id = source + source_id;
            System.out.println(String.format("_______________\nInstantiating %s",id));
            retractedArticle.setId(id);
            retractedArticles.add(retractedArticle);
            String url = String.format("https://europepmc.org/article/%s/%s", source, source_id);
            retractedArticle.setURL(url);
            try{
                if (pmcid_el != null) {
                    retractedArticle.setPmcId(pmcid_el.getAsString());
                    retractedArticle.setArticleFullText();
                    retractedArticle.setRetractionReason();
                } else {
                    // TODO
                } } catch (IndexOutOfBoundsException e) {
                    // TODO
                }
            

            

            
        }
    
        return retractedArticles;
    }


   
}