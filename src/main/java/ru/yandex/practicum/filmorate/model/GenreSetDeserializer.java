package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class GenreSetDeserializer extends JsonDeserializer<Set<Genre>> {

    @Override
    public Set<Genre> deserialize(
            JsonParser parser,
            DeserializationContext context
    ) throws IOException {

        JsonNode node = parser.getCodec().readTree(parser);

        Set<Genre> genres = new LinkedHashSet<>();

        if (node.isArray()) {
            for (JsonNode genreNode : node) {
                Genre genre = parser.getCodec().treeToValue(
                        genreNode,
                        Genre.class
                );

                genres.add(genre);
            }
        }

        return genres;
    }
}