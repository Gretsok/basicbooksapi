package fr.fergalmechin.basicbooksapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookRequest(
        @Schema(description = "Title of book", example = "Vingt Mille Lieues sous les mers")
        String title,
        @Schema(description = "Year of publication", example = "1869")
        Integer year,
        @Schema(description = "Author id", example = "1")
        Long authorId
) {}

