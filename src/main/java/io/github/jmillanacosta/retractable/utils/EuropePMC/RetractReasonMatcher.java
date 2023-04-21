package io.github.jmillanacosta.retractable.utils.EuropePMC;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
public class RetractReasonMatcher {
  
    public static List<LinkedHashMap<String, List<String>>> readPatternsFromYaml(String fileName) {
        List<LinkedHashMap<String, List<String>>> patternsList = null;

        try {
            File file = new File(fileName);
            InputStream inputStream = new FileInputStream(file);

            // Load YAML file
            Yaml yaml = new Yaml();
            patternsList = yaml.load(inputStream);

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return patternsList;
    }

    
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


    // Convert LinkedHashMap to JSON using Gson
    public static String convertToJson(Map<String, String> retractionReason) {
        Gson gson = new Gson();
        String json = gson.toJson(retractionReason);
        return json;
    }
}

