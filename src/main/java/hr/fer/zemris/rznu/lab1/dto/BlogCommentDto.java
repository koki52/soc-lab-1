package hr.fer.zemris.rznu.lab1.dto;

import hr.fer.zemris.rznu.lab1.model.BlogComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogCommentDto {

    private String author;
    private String body;
    private String url;

    public static BlogCommentDto fromBlogComment(BlogComment comment) {
        return BlogCommentDto.builder()
                .author(comment.getAuthor().getUsername())
                .body(comment.getBody())
                .url(getUrl(comment))
                .build();
    }

    private static String getUrl(BlogComment comment) {
        return "localhost:8080/api/comments/" + comment.getId();
    }
}
