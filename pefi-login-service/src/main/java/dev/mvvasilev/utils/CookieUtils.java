package dev.mvvasilev.utils;

import jakarta.servlet.http.Cookie;

public class CookieUtils {

    public static final String ACCESS_TOKEN_NAME = "pefi_token";

    public static final String REFRESH_TOKEN_NAME = "pefi_refresh_token";

    public static Cookie createAccessTokenCookie(String value) {
        var accessTokenCookie = new Cookie(ACCESS_TOKEN_NAME, value);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/api");

        return accessTokenCookie;
    }

    public static Cookie createRefreshTokenCookie(String value) {
        var refreshTokenCookie = new Cookie(REFRESH_TOKEN_NAME, value);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/refresh-token");

        return refreshTokenCookie;
    }

}
