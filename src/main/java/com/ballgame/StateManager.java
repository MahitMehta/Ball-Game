package com.ballgame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class StateManager {
    private final String TEMP_DIR;
    private final String STORE_FILENAME; 
    private JsonObject body = null; 
    
    private final Gson gson;

    public static enum STATE_KEYS {
        HIGH_SCORE
    }

    public StateManager() {
        gson = new Gson();

        TEMP_DIR = "./";

        STORE_FILENAME = "ballgame.state.json";

        File stateFile = new File(
            Paths.get(TEMP_DIR, STORE_FILENAME).toString()
        );

        try {
            if (!stateFile.exists()) {
                stateFile.createNewFile();

                this.updateKeyWithPrimitive(STATE_KEYS.HIGH_SCORE, new JsonPrimitive(0));
            }
            
            body = getJSONBody();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateKeyWithPrimitive(STATE_KEYS key, Object value) {
        try {
            Map<String, Object> map = new HashMap<>();

            Writer writer = new FileWriter(Paths.get(TEMP_DIR, STORE_FILENAME).toString());

            if (this.body != null) {
                Set<Entry<String, JsonElement>> existingKeys = this.body.entrySet(); 
            
                for (Entry<String, JsonElement> e : existingKeys) {
                    map.put(e.getKey(), e.getValue());
                }
            }

            map.put(key.toString().toLowerCase(), value);
            new Gson().toJson(map, writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getHighScore() {
       try {
            if (this.body == null) throw new IOException();
            return this.body.get(STATE_KEYS.HIGH_SCORE.toString().toLowerCase()).getAsInt();
       } catch (IOException e) {
           e.printStackTrace();
           return -1; 
       }
    }

    public void refreshJsonBody() {
        try {
            this.body = this.getJSONBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject getJSONBody() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(TEMP_DIR, STORE_FILENAME));
        JsonObject body = gson.fromJson(reader, JsonObject.class);
        return body; 
    }
}
