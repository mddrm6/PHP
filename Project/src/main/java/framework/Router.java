package framework;

import framework.annotations.GET;
import framework.annotations.POST;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {
    private final Map<Pattern, RouteInfo> getRoutes = new HashMap<>();
    private final Map<Pattern, RouteInfo> postRoutes = new HashMap<>();

    public void addRoute(Object controller) {
        Method[] methods = controller.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(GET.class)) {
                GET getAnnotation = method.getAnnotation(GET.class);
                String path = getAnnotation.value();
                Pattern pattern = Pattern.compile(path.replaceAll("\\{.*?\\}", "([^/]+)"));
                getRoutes.put(pattern, new RouteInfo(controller, method, path));
            } else if (method.isAnnotationPresent(POST.class)) {
                POST postAnnotation = method.getAnnotation(POST.class);
                String path = postAnnotation.value();
                Pattern pattern = Pattern.compile(path.replaceAll("\\{.*?\\}", "([^/]+)"));
                postRoutes.put(pattern, new RouteInfo(controller, method, path));
            }
        }
    }

    public String handleRequest(String path, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        String methodType = request.getMethod();
        Map<Pattern, RouteInfo> routes = methodType.equalsIgnoreCase("GET") ? getRoutes : postRoutes;
        for (Map.Entry<Pattern, RouteInfo> entry : routes.entrySet()) {
            Pattern pattern = entry.getKey();
            Matcher matcher = pattern.matcher(path);

            if (matcher.matches()) {
                RouteInfo routeInfo = entry.getValue();

                Object[] args = extractArgs(routeInfo.routePath, path);

                Object[] methodArgs = new Object[args.length + 3];

                System.arraycopy(args, 0, methodArgs, 0, args.length);

                methodArgs[args.length] = request;
                methodArgs[args.length + 1] = response;
                methodArgs[args.length + 2] = servletContext;
                System.out.println(Arrays.toString(args));
                try {
                    if (routeInfo.method.getParameterCount() == args.length + 3) {
                        return (String) routeInfo.method.invoke(routeInfo.controller, methodArgs);
                    } else {
                        System.err.println("Mismatched argument count for " + routeInfo.method.getName());
                        return "Internal Server Error";
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return "Internal Server Error";
                }
            }
        }
        return null;
    }

    private Object[] extractArgs(String routePath, String path) {
        Pattern pattern = Pattern.compile(routePath.replaceAll("\\{.*?\\}", "([^/]+)"));
        Matcher matcher = pattern.matcher(path);

        if (matcher.matches()) {
            int groupCount = matcher.groupCount();
            Object[] args = new Object[groupCount];
            for (int i = 1; i <= groupCount; i++) {
                args[i - 1] = matcher.group(i);
            }
            return args;
        }

        return new Object[0];
    }

    private static class RouteInfo {
        private final Object controller;
        private final Method method;
        private final String routePath;

        public RouteInfo(Object controller, Method method, String routePath) {
            this.controller = controller;
            this.method = method;
            this.routePath = routePath;
        }
    }
}
