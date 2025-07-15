package com.example.udtbe.global.token.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevCookie implements CookieConfig {

    @Override
    public Cookie createCookie(String token) {
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setPath("/");
        cookie.setDomain("banditbool.com");
        cookie.setMaxAge(60 * 180);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    @Override
    public void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setPath("/");
        cookie.setDomain("banditbool.com");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
    }

}
