package io.github.jmillanacosta.retractable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.jmillanacosta.retractable.classes.RetractedArticle;
import io.github.jmillanacosta.retractable.query.SearchEpmc;
import io.github.jmillanacosta.retractable.utils.EuropePMC.JsonUtils;
import io.github.jmillanacosta.retractable.utils.EuropePMC.RetractReasonMatcher;
public class Retractable{
    //TODO: clean up main class and move code to the adequate classes
    //TODO: creates Derby database and sets up a webservice/API endpoint
    //TODO: add more sources/services besides EuropePMC, ask around
    //TODO: method to retrieve only new additions to the .json/future relational db
    //TODO improve reasonmatcher and the way result is delivered
    public final static String PMC_FILEPATH = "data/ePMC_retracts.json";
    public final static String DATA_FILEPATH = "data/retractable.json";
    public final static String PMCIDS = "data/pmcids.csv";
    public final static String PMIDS = "data/pmids.csv";
    public final static String IDPMCID = "data/IDPMCID.csv";
    public final static String IDS = "data/ids.csv";
    public final static String PMIDS_DOIS="data/PMIDS_DOIS.csv";

    public static void main( String[] args ) throws Exception
    {   
        
        String query = "PUB_TYPE%3A%22Retracted%20Publication%22";
        // Retrieve JsonArray for the query
        JsonArray resultEPmcQuery = SearchEpmc.searchQuery(query);
        // Write it to a file
        FileWriter file = new FileWriter(PMC_FILEPATH);
        file.write(resultEPmcQuery.toString());
        //System.out.println("Successfully wrote JSON array to file.");
        file.close();
        JsonArray queriedEPmc = JsonUtils.openJson(PMC_FILEPATH);
        ArrayList<RetractedArticle> retractedArticles = JsonUtils.instantiateRetractedArticles(queriedEPmc);
       
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        int i = 0;
        ArrayList<String> pmcids = new ArrayList<String>();
        ArrayList<String> idPmcidMap = new ArrayList<String>();
        ArrayList<String> idPmidMap = new ArrayList<String>();
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> pmids = new ArrayList<String>();
        ArrayList<String> pmidDoiMap = new ArrayList<String>();

        idPmcidMap.add("id,pmcid");
        int articleNumber = retractedArticles.size(); 
        for (RetractedArticle article : retractedArticles) {
            JsonObject articleJson = new JsonObject();
            i +=1;
            System.out.println("Writing data for article " + i + " of " + articleNumber + ": " + article.pmid);
            articleJson.addProperty("article", article.id != null ? article.id : "");
            articleJson.addProperty("pmcid", article.pmcid != null ? article.pmcid : "");
            if (article.pmcid != null){
                pmcids.add(article.pmcid);
                idPmcidMap.add(article.id + "," + article.pmcid);
            }
            if (article.pmid != null){
                pmids.add(article.pmid);
                idPmidMap.add(article.id + "," + article.pmid);
                if (article.doi != null){
                    pmidDoiMap.add(article.pmid + "," + article.doi);
                    
                }
            }
            ids.add(article.id);
            articleJson.addProperty("url", article.url != null ? article.url : "");
            //articleJson.addProperty("retraction_reason", article.retractionReason != null ? article.retractionReason.toString() : "");
            //articleJson.addProperty("retraction_body", article.articleFullText != null ? article.articleFullText : "");
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
        
    

        // Write the ArrayList to the pmcids file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PMCIDS))) {
                for (String string : pmcids) {
                    writer.write(string);
                    writer.newLine(); // Add a new line after each string
                }
                System.out.println("ArrayList written to file successfully!");
            } catch (IOException e) {
                System.err.println("Error writing ArrayList to file: " + e.getMessage());
                e.printStackTrace();
            }
        
        // Write the ArrayList to the ids file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter  (IDS))) {
            for (String string : ids) {
                writer.write(string);
                writer.newLine(); // Add a new line after each string
            }
            System.out.println("ArrayList written to file successfully!");
        } catch (IOException e) {
            System.err.println("Error writing ArrayList to file: " + e. getMessage());
            e.printStackTrace();
        }
        // Write the ArrayList to the pmids file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter  (PMIDS))) {
            for (String string : pmids) {
                writer.write(string);
                writer.newLine(); // Add a new line after each string
            }
            System.out.println("ArrayList written to file successfully!");
        } catch (IOException e) {
            System.err.println("Error writing ArrayList to file: " + e. getMessage());
            e.printStackTrace();
        }
        
        
        // Write the ArrayList to the pmcid, id map file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(IDPMCID))) {
            for (String string : idPmcidMap) {
                writer.write(string);
                writer.newLine(); // Add a new line after each string
            }
            System.out.println("ArrayList written to file successfully!");
        } catch (IOException e) {
            System.err.println("Error writing ArrayList to file: " + e.getMessage());
            e.printStackTrace();
        }


        // Write the ArrayList to the pmid, doi map file

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PMIDS_DOIS))) {
            for (String string : pmidDoiMap) {
                writer.write(string);
                writer.newLine(); // Add a new line after each string
            }
            System.out.println("ArrayList written to file successfully!");
        } catch (IOException e) {
            System.err.println("Error writing ArrayList to file: " + e.getMessage());
            e.printStackTrace();
        }
            
            }
        
}


  
