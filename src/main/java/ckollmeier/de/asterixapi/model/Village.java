package ckollmeier.de.asterixapi.model;

import lombok.With;

@With
public record Village(
        String id,
        String name
) {
}
