package framework.models;

import java.time.LocalDateTime;

public class Article extends Model {
    private int id;
    private int authorId;
    private String name;
    private String text;
    private LocalDateTime createdAt;

    public Article() {}

    public Article(int id, int authorId, String name, String text, LocalDateTime createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.name = name;
        this.text = text;
        this.createdAt = createdAt;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}