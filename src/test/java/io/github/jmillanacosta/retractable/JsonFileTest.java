package io.github.jmillanacosta.retractable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.junit.Test;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertTrue;

public class JsonFileTest {

    @Test
    public void testJsonFile() throws IOException {
        Gson gson = new Gson();
        Reader reader = new FileReader("data/retracted.json");
        JsonElement json = gson.fromJson(reader, JsonElement.class);
        assertTrue("JSON file should be an array", json.isJsonArray());
        reader.close();
    }
}