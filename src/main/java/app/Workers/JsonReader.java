package app.Workers;

import app.DataStructures.JsonEntry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonReader {

    private ObjectMapper objectMapper;

    public JsonReader () {
        this.objectMapper = new ObjectMapper();
    }

    public LinkedHashMap<JsonEntry, Integer> readJson(String jsonPath) {

        // map cointaining entries and their count
        Map<JsonEntry, Integer> entryCountMap = new HashMap<>();

        // map containing entries sorted by count
        LinkedHashMap<JsonEntry, Integer> sortedByCountMap = new LinkedHashMap<>();

//        Map<JsonEntry, Integer> returnEntriesMap = new HashMap<>();


        try {
            // import JSON file
            JsonNode jsonNode = objectMapper.readTree(new File(jsonPath));

            // go through each entry in JSON file
            for (JsonNode entry : jsonNode) {

                try {
                    String title = entry.get("title").asText();
                    String url = entry.get("titleUrl").asText();

                    String author = entry.get("subtitles").get(0).get("name").asText();

                    // remove "Obejrzano: " or "Watched: " from title
                    if (title.startsWith("Obejrzano: ")) {
                        title = title.substring(11);
                    }

                    if (title.startsWith("Watched: ")) {
                        title = title.substring(9);
                    }

                    JsonEntry entryKey = new JsonEntry(title, url);
                    entryCountMap.put(entryKey, entryCountMap.getOrDefault(entryKey, 1) + 1);


                } catch (NullPointerException e) {
                    // missing titleUrl field - video that was removed from YouTube
                    continue;
                }

            }

            // sort map by count and put it into LinkedHashMap
            sortedByCountMap = entryCountMap.entrySet().stream()
                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

//                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
////                    .limit(returnCount)
//                    .forEach(entry -> {
//
////                        System.out.println(entry.getKey().getUrl()
////                                + "\n" + entry.getKey().getTitle() +
////                                ", Played: " + entry.getValue());
//
//                        returnEntriesMap.put(entry.getKey(), entry.getValue());
//                    });

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        // return sorted map with all entries
        return sortedByCountMap;

    }


    // combine entries with the same videoID from yt and ytmusic and sum their play counts
    public LinkedHashMap<JsonEntry, Integer> getCombinedEntries (LinkedHashMap<JsonEntry, Integer> historyEntries) {

        LinkedHashMap<JsonEntry, Integer> combinedEntries = new LinkedHashMap<>();

        historyEntries.forEach((k, v) -> {
            String urlLong = k.getUrl();
            String videoId = urlLong.substring(urlLong.length() - 11);

            Integer plays = v;

            JsonEntry newEntry = new JsonEntry(k.getTitle(), videoId);
            combinedEntries.put(newEntry, combinedEntries.getOrDefault(newEntry, 0) + plays);
        });

        LinkedHashMap<JsonEntry, Integer> finalCombinedEntries;

        // sort the combined entries by play count
        finalCombinedEntries = combinedEntries.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        return finalCombinedEntries;
    }


}
