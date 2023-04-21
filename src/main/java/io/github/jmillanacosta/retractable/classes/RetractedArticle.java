package io.github.jmillanacosta.retractable.classes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.jmillanacosta.retractable.utils.EuropePMC.RetractReasonMatcher;



public class RetractedArticle {
    public String id;
    public String pmcid;
    public String articleAbstract;
    public String articleFullText;
    public String url;
    public Map<String, String> retractionReason;
    public final static String USER_AGENT = "Mozilla/5.0";
    public final static String BASE_URL = "https://www.ebi.ac.uk/europepmc/webservices/rest/%s/fullTextXML";

    public void setPmcId(String pmcid) {
        this.pmcid = pmcid;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setArticleAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }

    public void setArticleFullText(String articleFullText) {
        this.articleFullText = articleFullText;
    }

    public void setURL(String url){
        this.url = url;
    }
    public String getId() {
        return this.id;
    }
    public String getPmcid() {
        return this.pmcid;
    }

    public String getArticleAbstract() {
        return this.articleAbstract;
    }

    public void setArticleFullText() throws Exception {
        System.out.println(this.pmcid);
        String fullTextXMLUrl = String.format(BASE_URL, this.pmcid);
        URL obj = new URL(fullTextXMLUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        try {
            // handle non 200 range responses
            if (responseCode < 200 || responseCode >= 300) {
                System.out.println("Failed to retrieve data. Response code: " + responseCode);
                
            } else {
                InputStream inputStream = con.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader in = new BufferedReader(inputStreamReader);
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                String bodyContent = "";
                String regex = "<body(.*?)</body";
                Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(response.toString());
                if (matcher.find()) {
                    bodyContent = matcher.group(1);
                }
                
                String cleanedText = bodyContent
                        
                        .replaceAll("<.*?>", "") // remove all XML tags
                        .replaceAll("&[#\\w]+?;", "") // remove HTML entities
                        .replaceAll("\\s+", " ") // replace multiple whitespaces with a single space
                        .replaceAll(">","") // left from the body tag if <body>
                        .trim(); // remove leading/trailing whitespaces
                
                this.articleFullText = cleanedText;
                
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
    
    
    
    

    public String getURL() {
        return this.url.toString();
    }
    public void setRetractionReason() {
        //TODO Only works for EUropePMC, should be moved to class EuropePMC/JSONUtils and make this method a switch
        // Get text if available (if PMCID is given)
        // search for i.e., keywords like "The author", "Reason for retraction"
        // TODO try to automate adding tags (author provided explanation, ethical concern... how?)
        //TODO compile patterns outside of the loop and put them in an ArrayList
        String text = this.articleFullText;
        Map<String, String> hit_patterns = new HashMap<String, String>();
        ArrayList<String> result = new ArrayList<String>();
    
        // Read patterns from YAML file
        List<LinkedHashMap<String, List<String>>> patterns = RetractReasonMatcher.readPatternsFromYaml("src/main/resources/patterns.yml");
    
        // Iterate over the patterns and match each pattern
        if (text != null && !text.isEmpty()) {
            if (text.length() < 70000) {
                // TODO remove the length, only temp fix for large fulltexts that i suspect are not retraction notices
                System.out.println("Looking for retraction reason in article text...");
                for (LinkedHashMap<String, List<String>> patternMap : patterns) {
                    for (Map.Entry<String, List<String>> entry : patternMap.entrySet()) {
                        String key = entry.getKey();
                        List<String> patternList = entry.getValue();
                        for (String pattern : patternList) {
                            Pattern p = Pattern.compile(pattern);
                            Matcher m = p.matcher(text);
                            while (m.find()) {
                                String sentenceWithMatch = m.group(0);
                                result.add(sentenceWithMatch);
                                hit_patterns.put(key, sentenceWithMatch);
                            }
                        }
                    }
                }
                System.out.println("Done looking for patterns");
                ArrayList<String> uniqueStrings = new ArrayList<String>(); // HashSet to keep track of unique strings
    
                // Add all unique strings from ArrayList to HashSet
                for (String s : result) {
                    uniqueStrings.add(s);
                }
                Collections.sort(uniqueStrings, (s1, s2) -> Integer.compare(s2.length(), s1.length()));
    
                System.out.println("Build the StringBuilder");
                // Join unique strings into a single string
                StringBuilder sb = new StringBuilder();
                for (String s : uniqueStrings) {
                    if (sb.indexOf(s) == -1) { // check if the string is not already present in the StringBuilder
                        sb.append(s);
                        sb.append(" "); // add a space separator between each unique string
                    }
                }
    
                if (!hit_patterns.isEmpty()) {
                    this.retractionReason = hit_patterns;
                }
            }
        }
    }
    

    public Map<String, String> getRetractionReason(){
        return this.retractionReason;
    }

    public String getArticleFullText(){
        return this.getArticleFullText();
    }
}
