package framework.views;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class View {
    private final TemplateEngine templateEngine;

    public View() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();

        templateEngine.setTemplateResolver(templateResolver);
    }

    public String render(String templateName, WebContext context) {
        return templateEngine.process(templateName, context);
    }
}
