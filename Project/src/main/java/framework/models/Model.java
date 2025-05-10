package framework.models;

import framework.DatabaseConnection;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Model {

    public abstract int getId();

    public abstract void setId(int id);

    private static String convertToSnakeCase(String camelCase) {
        StringBuilder snakeCase = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                snakeCase.append('_');
            }
            snakeCase.append(Character.toLowerCase(c));
        }
        return snakeCase.toString();
    }

    public static <T extends Model> void save(T instance) {
        Class<? extends Model> clazz = instance.getClass();
        String tableName = clazz.getSimpleName().toLowerCase() + "s";

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                columns.append(convertToSnakeCase(field.getName())).append(", ");
                values.append("?, ");
            }

            columns.setLength(columns.length() - 2);
            values.setLength(values.length() - 2);


            String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";

            try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                int index = 1;
                for (Field field : fields) {
                    Object value = field.get(instance);
                    if (value == null || value.equals(0)) {
                        if (field.getName().equals("authorId")) {
                            value = 1;
                        } else if (field.getName().equals("createdAt")) {
                            value = LocalDateTime.now();
                        }
                    }
                    statement.setObject(index++, value);
                }
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        instance.setId(id);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Model> void update(T instance) {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            Class<? extends Model> clazz = instance.getClass();
            String tableName = clazz.getSimpleName().toLowerCase() + "s";
            StringBuilder setClause = new StringBuilder();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                setClause.append(convertToSnakeCase(field.getName())).append(" = ?, ");
            }
            setClause.setLength(setClause.length() - 2);

            String sql = "UPDATE " + tableName + " SET " + setClause + "WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int index = 1;
                for (Field field : fields) {
                    statement.setObject(index++, field.get(instance));
                }
                statement.setInt(index, instance.getId());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Model> void delete(T instance) {
        Class<? extends Model> clazz = instance.getClass();
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, instance.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Model> T findById(Class<T> clazz, int id) {
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String columnName = convertToSnakeCase(field.getName());
                        field.set(instance, resultSet.getObject(columnName));
                    }
                    return instance;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Model> T findOneByField(Class<T> clazz, String fieldName, Object value) {
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        String sql = "SELECT * FROM " + tableName + " WHERE " + convertToSnakeCase(fieldName) + " = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String columnName = convertToSnakeCase(field.getName());
                        field.set(instance, resultSet.getObject(columnName));
                    }
                    return instance;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Model> List<T> findAll(Class<T> clazz) {
        List<T> results = new ArrayList<>();
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        String sql = "SELECT * FROM " + tableName;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                T instance = clazz.getDeclaredConstructor().newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String columnName = convertToSnakeCase(field.getName());
                    field.set(instance, resultSet.getObject(columnName));
                }
                results.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public static <T extends Model> List<T> findAllByArticleId(Class<T> clazz, int articleId) {
        List<T> results = new ArrayList<>();
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        String sql = "SELECT * FROM " + tableName + " WHERE article_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, articleId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String columnName = convertToSnakeCase(field.getName());
                        field.set(instance, resultSet.getObject(columnName));
                    }
                    results.add(instance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}
