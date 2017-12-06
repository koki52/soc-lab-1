package hr.fer.zemris.rznu.lab1.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.zemris.rznu.lab1.dao.UserDao;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UserRest.class)
public class UserRestTest {

    @MockBean
    private UserDao userDao;

    @MockBean
    private UserUtil userUtil;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

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
        mockMvc.perform(post("/api/users")
                .requestAttr("username", "perica")
                .requestAttr("password", "pero123"))
                .andExpect(status().isBadRequest());
        verify(userDao, times(0)).save(any(User.class));
    }

    @Test
    @WithMockUser
    public void registerWithUniqueUsername() throws Exception {
        when(userDao.findByUsername("perica")).thenReturn(null);
        User user = User.builder().username("perica").password("pero123").build();
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @WithMockUser
    public void updatePassword() throws Exception {
        User user = User.builder().username("perica").password("pero123").build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        verify(userDao, times(1)).save(user);
    }
}
