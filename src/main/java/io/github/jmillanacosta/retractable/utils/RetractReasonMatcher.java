package io.github.jmillanacosta.retractable.utils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.IOException;

public class RetractReasonMatcher {
    public static List<String> retractReasonMatcher(String response, String patternFilepath) {
        List<String> result = new ArrayList<String>();
        List<String> patterns = readPatterns(patternFilepath);
        // Iterate over the lines and match each pattern
        if (response != null) {
            for (String pattern : patterns) {
                Pattern p = Pattern.compile(pattern + "\\b.*?");
                Matcher m = p.matcher(response);
                while (m.find()) {
                    String sentenceWithMatch = m.group(1);
                    System.out.println("Sentence with match: " + sentenceWithMatch);
                    result.add(sentenceWithMatch);
                }
            }
        }
        return result;
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

