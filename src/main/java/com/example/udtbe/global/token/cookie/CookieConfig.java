package com.example.udtbe.global.token.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieConfig {

    public Cookie createCookie(String token);

    public void deleteCookie(HttpServletResponse response);

    public Cookie createOnboardingCookie();
}
