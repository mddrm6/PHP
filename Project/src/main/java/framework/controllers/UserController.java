package framework.controllers;

import framework.annotations.GET;
import framework.annotations.POST;
import framework.models.User;
import framework.models.Model;
import framework.views.View;
import org.mindrot.jbcrypt.BCrypt;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class UserController {

    private final View view = new View();

    @GET("/users/register")
    public String registerForm(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(servletContext);
        IWebExchange webExchange = webApplication.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        return view.render("register", context);
    }

    @POST("/users/register")
    public String register(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (password.length() < 8) {
            request.setAttribute("error", "Password must be at least 8 characters long.");
            return registerForm(request, response, servletContext);
        }

        User existingUserByNickname = Model.findOneByField(User.class, "nickname", nickname);
        User existingUserByEmail = Model.findOneByField(User.class, "email", email);

        if (existingUserByNickname != null || existingUserByEmail != null) {
            request.setAttribute("error", "Nickname or email already exists.");
            return registerForm(request, response, servletContext);
        }

        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setConfirmed(false);
        user.setRole("user");

        Model.save(user);

        try {
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/users/login");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @GET("/users/login")
    public String loginForm(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(servletContext);
        IWebExchange webExchange = webApplication.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        return view.render("login", context);
    }

    @POST("/users/login")
    public String login(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = Model.findOneByField(User.class, "email", email);

        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            String authToken = UUID.randomUUID().toString();
            user.setAuthToken(authToken);
            Model.update(user);

            Cookie cookie = new Cookie("authToken", authToken);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);

            try {
                String contextPath = request.getContextPath();
                response.sendRedirect(contextPath + "/");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        } else {
            request.setAttribute("error", "Invalid email or password.");
            return loginForm(request, response, servletContext);
        }
    }

    @GET("/users/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("authToken")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        try {
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static User getAuthenticatedUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("authToken")) {
                    return User.authenticate(cookie.getValue());
                }
            }
        }
        return null;
    }
}
