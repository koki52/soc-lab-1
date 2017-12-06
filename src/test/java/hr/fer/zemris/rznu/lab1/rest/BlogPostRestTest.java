package hr.fer.zemris.rznu.lab1.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.zemris.rznu.lab1.dao.BlogPostDao;
import hr.fer.zemris.rznu.lab1.dao.UserDao;
import hr.fer.zemris.rznu.lab1.dto.BlogPostDto;
import hr.fer.zemris.rznu.lab1.model.BlogPost;
import hr.fer.zemris.rznu.lab1.model.User;
import hr.fer.zemris.rznu.lab1.util.UserUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BlogPostRest.class)
public class BlogPostRestTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserUtil userUtil;

    @MockBean
    private BlogPostDao blogPostDao;

    @MockBean
    private UserDao userDao;

    @Test
    @WithMockUser
    public void getAllTest() throws Exception {
        User user = getMockUser();
        List<BlogPost> posts = getMockPosts(user);
        when(userDao.findByUsername("perica")).thenReturn(user);
        when(blogPostDao.findAll())
                .thenReturn(posts);
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(mapper.writeValueAsString(toDto(posts))));
    }

    @Test
    @WithMockUser
    public void findOnePostTest() throws Exception {
        User user = getMockUser();
        BlogPost post = BlogPost.builder().author(user).id(1L).title("Blah").body("Yaargh!").build();
        when(userDao.findByUsername("perica")).thenReturn(user);
        when(blogPostDao.findOne(1L)).thenReturn(post);
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(mapper.writeValueAsString(BlogPostDto.fromBlogPost(post))));
    }

    @Test
    @WithMockUser
    public void newPostTest() throws Exception {
        User user = getMockUser();
        when(userUtil.getCurrentUser()).thenReturn(user);
        BlogPost blogPost = BlogPost.builder().title("bla").body("lala").build();
        mockMvc.perform(post("/api/posts")
                .content(mapper.writeValueAsString(blogPost))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
        verify(blogPostDao, times(1)).save(any(BlogPost.class));
    }

    @Test
    @WithMockUser
    public void updateTest() throws Exception {
        User user = getMockUser();
        when(userUtil.getCurrentUser()).thenReturn(user);
        BlogPost blogPost = BlogPost.builder().author(user).title("bla").body("lala").build();
        when(blogPostDao.findOne(1L))
                .thenReturn(blogPost);
        mockMvc.perform(put("/api/posts/1")
                .content(mapper.writeValueAsString(BlogPostDto.fromBlogPost(blogPost)))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(blogPostDao, times(1)).save(any(BlogPost.class));
    }

    @Test
    @WithMockUser
    public void deleteTest() throws Exception {
        User user = getMockUser();
        when(userUtil.getCurrentUser()).thenReturn(user);
        BlogPost blogPost = BlogPost.builder().author(user).title("bla").body("lala").build();
        when(blogPostDao.findOne(1L))
                .thenReturn(blogPost);
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isOk());
        verify(blogPostDao, times(1)).delete(any(BlogPost.class));
    }

    @Test
    @WithMockUser
    public void deleteNotAuthorTest() throws Exception {
        User user = getMockUser();
        User author = User.builder().id(2L).username("marin").password("bla").build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        BlogPost blogPost = BlogPost.builder().author(author).title("bla").body("lala").build();
        when(blogPostDao.findOne(1L))
                .thenReturn(blogPost);
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isForbidden());
        verify(blogPostDao, times(0)).delete(any(BlogPost.class));
    }

    private List<BlogPost> getMockPosts(User user) {
        List<BlogPost> posts = new ArrayList<>();
        posts.add(BlogPost.builder().id(1L).title("First").body("First").author(user).build());
        posts.add(BlogPost.builder().id(2L).title("Second").body("Second").author(user).build());

        return posts;
    }

    private User getMockUser() {
        return User.builder().id(1L).username("perica").password("pero123").build();
    }

    private List<BlogPostDto> toDto(List<BlogPost> posts) {
        return posts.stream().map(BlogPostDto::fromBlogPost).collect(Collectors.toList());
    }
}
