package hr.fer.zemris.rznu.lab1.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.zemris.rznu.lab1.dao.BlogCommentDao;
import hr.fer.zemris.rznu.lab1.dao.BlogPostDao;
import hr.fer.zemris.rznu.lab1.dto.BlogCommentDto;
import hr.fer.zemris.rznu.lab1.model.BlogComment;
import hr.fer.zemris.rznu.lab1.model.BlogPost;
import hr.fer.zemris.rznu.lab1.model.User;
import hr.fer.zemris.rznu.lab1.util.UserUtil;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NestedBlogCommentRest.class)
public class NestedBlogCommentRestTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserUtil userUtil;

    @MockBean
    private BlogPostDao blogPostDao;

    @MockBean
    private BlogCommentDao blogCommentDao;

    @Test
    @WithMockUser
    public void getCommentTest() throws Exception {
        User user = getMockUser();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        when(blogCommentDao.findByBlogPost_IdAndBlogPost_Author_UsernameAndId(1L, "perica",1L))
                .thenReturn(comment);
        mockMvc.perform(get("/api/users/perica/posts/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(mapper.writeValueAsString(BlogCommentDto.fromBlogComment(comment))));
    }

    @Test
    @WithMockUser
    public void getAllCommentTest() throws Exception {
        User user = getMockUser();
        BlogPost post = BlogPost.builder().author(user).title("bla").body("lala").build();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        List<BlogCommentDto> comments = Lists.newArrayList(BlogCommentDto.fromBlogComment(comment));
        when(blogPostDao.findByAuthor_UsernameAndId("perica", 1L)).thenReturn(post);
        when(blogCommentDao.findAllByBlogPost_Id(1L)).thenReturn(Lists.newArrayList(comment));
        mockMvc.perform(get("/api/users/perica/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(mapper.writeValueAsString(comments)));
    }

    @Test
    @WithMockUser
    public void updateCommentTest() throws Exception {
        User user = getMockUser();
        BlogPost post = BlogPost.builder().author(user).title("bla").body("lala").build();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        when(blogCommentDao.findByBlogPost_IdAndBlogPost_Author_UsernameAndId(1L, "perica", 1L)).thenReturn(comment);
        when(blogPostDao.findByAuthor_UsernameAndId("perica", 1L)).thenReturn(post);
        when(userUtil.getCurrentUser()).thenReturn(user);
        mockMvc.perform(put("/api/users/perica/posts/1/comments/1")
                .content(mapper.writeValueAsString(BlogCommentDto.fromBlogComment(comment)))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void deleteCommentTest() throws Exception {
        User user = getMockUser();
        BlogPost post = BlogPost.builder().author(user).title("bla").body("lala").build();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        when(blogPostDao.findByAuthor_UsernameAndId("perica", 1L)).thenReturn(post);
        when(blogCommentDao.findByBlogPost_IdAndBlogPost_Author_UsernameAndId(1L, "perica", 1L)).thenReturn(comment);
        when(userUtil.getCurrentUser()).thenReturn(user);
        mockMvc.perform(delete("/api/users/perica/posts/1/comments/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void postCommentTest() throws Exception {
        User user = getMockUser();
        BlogPost post = BlogPost.builder().author(user).title("bla").body("lala").build();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        when(blogPostDao.findByAuthor_UsernameAndId("perica", 1L)).thenReturn(post);
        when(userUtil.getCurrentUser()).thenReturn(user);
        mockMvc.perform(post("/api/users/perica/posts/1/comments")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isCreated());
    }

    private User getMockUser() {
        return User.builder().id(1L).username("perica").password("pero123").build();
    }
}
