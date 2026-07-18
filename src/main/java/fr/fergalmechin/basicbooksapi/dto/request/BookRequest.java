package fr.fergalmechin.basicbooksapi.dto.request;

public record BookRequest(String title, Integer year, Long authorId) {}
