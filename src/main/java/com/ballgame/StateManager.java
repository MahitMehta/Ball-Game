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
        HIGH_SCORE,
        LIVES
    }

    public StateManager() {
        gson = new Gson();

        TEMP_DIR = "./";

        STORE_FILENAME = "ballgame.state.json";

        File stateFile = new File(Paths.get(TEMP_DIR, STORE_FILENAME).toString());

        try {
            if (!stateFile.exists()) {
                stateFile.createNewFile();
                body = getJSONBody();
                this.updateKeyWithPrimitive(STATE_KEYS.HIGH_SCORE, new JsonPrimitive(0));
                this.updateKeyWithPrimitive(STATE_KEYS.LIVES, new JsonPrimitive(1));
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

            if (this.body == null) this.body = this.getJSONBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonElement getKey(STATE_KEYS key) {
        try {
            if (this.body == null)
                throw new IOException();
            return this.body.get(key.toString().toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getHighScore() {
        JsonElement ele = getKey(STATE_KEYS.HIGH_SCORE);
        return ele != null ? ele.getAsInt() : -1; 
    }

    public int getLives() {
        JsonElement ele = getKey(STATE_KEYS.LIVES);
        return ele != null ? ele.getAsInt() : -1; 
    }

    public void refreshJsonBody() {
        try {
            this.body = this.getJSONBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject getJSONBody() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(TEMP_DIR, STORE_FILENAME));
        JsonObject body = gson.fromJson(reader, JsonObject.class);
        return body;
    }
}
