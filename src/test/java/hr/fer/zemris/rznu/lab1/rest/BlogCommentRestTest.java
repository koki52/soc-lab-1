package hr.fer.zemris.rznu.lab1.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.zemris.rznu.lab1.dao.BlogCommentDao;
import hr.fer.zemris.rznu.lab1.dto.BlogCommentDto;
import hr.fer.zemris.rznu.lab1.model.BlogComment;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BlogCommentRest.class)
public class BlogCommentRestTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserUtil userUtil;

    @MockBean
    private BlogCommentDao blogCommentDao;

    @Test
    @WithMockUser
    public void getCommentTest() throws Exception {
        User user = getMockUser();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        when(blogCommentDao.findOne(1L)).thenReturn(comment);
        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(mapper.writeValueAsString(BlogCommentDto.fromBlogComment(comment))));
    }

    @Test
    @WithMockUser
    public void getAllCommentTest() throws Exception {
        User user = getMockUser();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        List<BlogCommentDto> comments = Lists.newArrayList(BlogCommentDto.fromBlogComment(comment));
        when(blogCommentDao.findAll()).thenReturn(Lists.newArrayList(comment));
        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(mapper.writeValueAsString(comments)));
    }

    @Test
    @WithMockUser
    public void updateCommentTest() throws Exception {
        User user = getMockUser();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        when(blogCommentDao.findOne(1L)).thenReturn(comment);
        when(userUtil.getCurrentUser()).thenReturn(user);
        mockMvc.perform(put("/api/comments/1")
                .content(mapper.writeValueAsString(BlogCommentDto.fromBlogComment(comment)))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void deleteCommentTest() throws Exception {
        User user = getMockUser();
        BlogComment comment = BlogComment.builder().author(user).body("Hello!").build();
        when(blogCommentDao.findOne(1L)).thenReturn(comment);
        when(userUtil.getCurrentUser()).thenReturn(user);
        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk());
    }

    private User getMockUser() {
        return User.builder().id(1L).username("perica").password("pero123").build();
    }
}
