package framework.controllers;

import framework.annotations.GET;
import framework.annotations.POST;
import framework.models.Article;
import framework.models.Comment;
import framework.models.Model;
import framework.views.View;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ArticleController {

    private final View view = new View();

    @GET("/articles/{id}")
    public String showArticle(String id, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int articleId = Integer.parseInt(id);
            Article article = Model.findById(Article.class, articleId);

            if (article != null) {
                List<Comment> comments = Model.findAllByArticleId(Comment.class, articleId);

                JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(servletContext);
                IWebExchange webExchange = webApplication.buildExchange(request, response);
                WebContext context = new WebContext(webExchange, request.getLocale());

                context.setVariable("article", article);
                context.setVariable("comments", comments);
                return view.render("article", context);
            } else {
                System.out.println("Article not found");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid article ID");
            return null;
        }
    }

    @GET("/articles/{id}/edit")
    public String editArticle(String id, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int articleId = Integer.parseInt(id);
            Article article = Model.findById(Article.class, articleId);

            if (article != null) {
                JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(servletContext);
                IWebExchange webExchange = webApplication.buildExchange(request, response);
                WebContext context = new WebContext(webExchange, request.getLocale());

                context.setVariable("article", article);
                return view.render("edit_article", context);
            } else {
                System.out.println("Article not found");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid article ID");
            return null;
        }
    }

    @POST("/articles/{id}/edit")
    public String updateArticle(String id, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int articleId = Integer.parseInt(id);
            Article article = Model.findById(Article.class, articleId);

            if (article != null) {
                String name = request.getParameter("name");
                String text = request.getParameter("text");

                article.setName(name);
                article.setText(text);

                Model.update(article);

                String contextPath = request.getContextPath();
                response.sendRedirect( contextPath + "/articles/" + articleId);
                return "";
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid article ID: " + id);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

    @POST("/articles/{id}/delete")
    public String deleteArticle(String id, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int articleId = Integer.parseInt(id);
            Article article = Model.findById(Article.class, articleId);

            if (article != null) {
                Model.delete(article);

                String contextPath = request.getContextPath();
                response.sendRedirect(contextPath + "/");
                return "";
            } else {
                return "";
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid article ID: " + id);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

    @GET("/articles/")
    public String newArticleForm(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(servletContext);
        IWebExchange webExchange = webApplication.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        return view.render("new_article", context);
    }

    @POST("/articles")
    public String createArticle(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            String name = request.getParameter("name");
            String text = request.getParameter("text");

            Article article = new Article();
            article.setName(name);
            article.setText(text);

            Model.save(article);

            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/articles/" + article.getId());
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

}
