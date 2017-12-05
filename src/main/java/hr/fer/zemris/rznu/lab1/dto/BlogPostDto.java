package hr.fer.zemris.rznu.lab1.dto;

import hr.fer.zemris.rznu.lab1.model.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPostDto {

    private String author;
    private String title;
    private String body;

    public static BlogPostDto fromBlogPost(BlogPost blogPost) {
        return BlogPostDto.builder()
                .author(blogPost.getAuthor().getUsername())
                .title(blogPost.getTitle())
                .body(blogPost.getBody())
                .build();
    }
}
