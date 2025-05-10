package framework.controllers;

import framework.annotations.GET;
import framework.annotations.POST;
import framework.models.Comment;
import framework.models.Model;
import framework.views.View;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommentController {

    private final View view = new View();

    @POST("/articles/{id}/comments")
    public String addComment(String id, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int articleId = Integer.parseInt(id);
            String text = request.getParameter("text");

            Comment comment = new Comment();
            comment.setArticleId(articleId);
            comment.setText(text);

            Model.save(comment);

            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/articles/" + articleId + "#comment" + comment.getId());
            return "";
        } catch (NumberFormatException e) {
            System.err.println("Invalid article ID: " + id);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

    @GET("/articles/{articleId}/comments/{commentId}/edit")
    public String editCommentForm(String articleId, String commentId, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int commentIdInt = Integer.parseInt(commentId);
            Comment comment = Model.findById(Comment.class, commentIdInt);

            if (comment != null) {
                JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(servletContext);
                IWebExchange webExchange = webApplication.buildExchange(request, response);
                WebContext context = new WebContext(webExchange, request.getLocale());

                context.setVariable("comment", comment);
                return view.render("edit_comment", context);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid comment ID: " + commentId);
            e.printStackTrace();
            return null;
        }
    }

    @POST("/articles/{articleId}/comments/{commentId}/edit")
    public String updateComment(String articleId, String commentId, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int commentIdInt = Integer.parseInt(commentId);
            Comment comment = Model.findById(Comment.class, commentIdInt);

            if (comment != null) {
                String text = request.getParameter("text");
                comment.setText(text);

                Model.update(comment);

                String contextPath = request.getContextPath();
                response.sendRedirect(contextPath + "/articles/" + articleId + "#comment" + commentIdInt);
            }
            return "";
        } catch (NumberFormatException e) {
            System.err.println("Invalid comment ID: " + commentId);
            e.printStackTrace();
            return "Invalid comment ID";
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

    @POST("/articles/{articleId}/comments/{commentId}/delete")
    public String deleteComment(String articleId, String commentId, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        try {
            int commentIdInt = Integer.parseInt(commentId);
            Comment comment = Model.findById(Comment.class, commentIdInt);

            if (comment != null) {
                Model.delete(comment);

                String contextPath = request.getContextPath();
                response.sendRedirect(contextPath + "/articles/" + articleId);
                return "";
            } else {
                return "Invalid comment ID";
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid comment ID: " + commentId);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }
}
