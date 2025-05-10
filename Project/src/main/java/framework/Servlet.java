package framework;

import framework.controllers.ArticleController;
import framework.controllers.CommentController;
import framework.controllers.MainController;
import framework.controllers.UserController;
import framework.models.User;
import framework.views.View;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@WebServlet("/")
public class Servlet extends HttpServlet {
    private final Router router = new Router();
    private final View view = new View();

    @Override
    public void init() throws ServletException {
        MainController Maincontroller = new MainController();
        ArticleController articleController = new ArticleController();
        CommentController commentController = new CommentController();
        UserController userController = new UserController();

        router.addRoute(Maincontroller);
        router.addRoute(articleController);
        router.addRoute(commentController);
        router.addRoute(userController);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getServletPath();

        User user = UserController.getAuthenticatedUser(request);
        request.setAttribute("user", user != null ? user : new User());

        String responseContent = router.handleRequest(path, request, response, getServletContext());
        response.setContentType("text/html");

        if (responseContent != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(responseContent);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            JavaxServletWebApplication webApplication = JavaxServletWebApplication.buildApplication(getServletContext());
            IWebExchange webExchange = webApplication.buildExchange(request, response);
            WebContext context = new WebContext(webExchange, request.getLocale());

            String notFoundContent = view.render("404", context);
            response.getWriter().write(notFoundContent);
        }
    }

    @Override
    public void destroy() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
