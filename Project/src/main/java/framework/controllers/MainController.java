package framework.controllers;

import framework.annotations.GET;
import framework.models.Article;
import framework.models.Model;
import framework.views.View;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class MainController {
    private final View view = new View();

    @GET("/")
    public String home(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        List<Article> articles = Model.findAll(Article.class);
        System.out.println("Found " + articles.size() + " articles.");

        JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(servletContext);
        IWebExchange webExchange = webApplication.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        context.setVariable("articles", articles);

        return view.render("index", context);
    }
}
