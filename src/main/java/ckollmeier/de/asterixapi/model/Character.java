package ckollmeier.de.asterixapi.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "characters")
public record Character(
        String id,
        String name,
        int age,
        String profession
) {
}
