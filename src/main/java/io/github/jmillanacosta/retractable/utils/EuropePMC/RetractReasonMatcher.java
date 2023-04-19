package io.github.jmillanacosta.retractable.utils.EuropePMC;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.IOException;

//TODO for now it's grabbing (almost) the whole <body> for al XMLs. Future: use patterns to classify the outputs
public class RetractReasonMatcher {
    public static Map<String, String> retractReasonMatcher(String response, String patternFilepath) {
        List<String> patterns = readPatterns(patternFilepath);
        Map<String, String>  hit_patterns = new HashMap<String, String>();
        // Iterate over the lines and match each pattern
        if (response != null) {
            for (String pattern : patterns) {
                Pattern p = Pattern.compile(pattern + "\\b.*?");
                Matcher m = p.matcher(response);
                while (m.find()) {
                    String sentenceWithMatch = m.group(1);
                    System.out.println("Sentence with match: " + sentenceWithMatch);
                    hit_patterns.put(pattern, sentenceWithMatch);
                }
            }
        }
        return hit_patterns;
    }
    
    
        
        //return result;
    

    public static List<String> splitIntoLines(String text) {
        List<String> lines = new ArrayList<>();
        String[] split = text.split("\\r?\\n");
        for (String line : split) {
            lines.add(line.trim());
        }
        return lines;
    }

    public static String removeXmlTags(String text) {
        return text.replaceAll("<[^>]*>", "");
    }

    public static List<String> readPatterns(String filename) {
        List<String> patterns = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                patterns.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patterns;
    }
}

