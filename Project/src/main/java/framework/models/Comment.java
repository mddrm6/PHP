package framework.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Comment extends Model{
    private int id;
    private int authorId;
    private int articleId;
    private String text;
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(int id, int authorId, int articleId, String text, LocalDateTime createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.articleId = articleId;
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

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
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

    public Date getCreatedAtAsDate() {
        return Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
