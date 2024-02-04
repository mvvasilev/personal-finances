package dev.mvvasilev.common.web;

public record APIErrorDTO(String message, String errorCode, String stacktrace) { }
