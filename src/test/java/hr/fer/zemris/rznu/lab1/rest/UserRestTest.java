package hr.fer.zemris.rznu.lab1.rest;

import hr.fer.zemris.rznu.lab1.dao.UserDao;
import hr.fer.zemris.rznu.lab1.model.User;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UserRest.class)
public class UserRestTest {

    @MockBean
    private UserDao userDao;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void getUsersTest() throws Exception {
        when(userDao.findAll()).thenReturn(Lists.emptyList());
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser
    public void registerWithTakenUsername() throws Exception {
        when(userDao.findByUsername("perica")).thenReturn(new User());
        mockMvc.perform(post("/api/register")
                .requestAttr("username", "perica")
                .requestAttr("password", "pero123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void registerWithUniqueUsername() throws Exception {
        when(userDao.findByUsername("perica")).thenReturn(null);
        mockMvc.perform(post("/api/register")
                .requestAttr("username", "perica")
                .requestAttr("password", "pero123"))
                .andExpect(status().isBadRequest());
    }
}
