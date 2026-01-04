package com.example.Crossfire;

import com.example.Crossfire.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        // If no user is in session, or they aren't an ADMIN, kick them to login
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect("/login?error=UnauthorizedAccess");
            return false;
        }

        return true; // Access granted
    }
}