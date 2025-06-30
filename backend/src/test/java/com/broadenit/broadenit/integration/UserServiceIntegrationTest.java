package com.broadenit.broadenit.integration;

import com.broadenit.broadenit.user.User;
import com.broadenit.broadenit.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void IntegrationRegister() throws Exception {

        String registrationRequest = """
            {
                "email": "123@gmail.com",
                "nickname": "test",
                "password": "123"
            }
        """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequest))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("123@gmail.com").orElseThrow();
        assertEquals("123@gmail.com", user.getEmail());
        assertEquals("test", user.getNickname());
        assertNotNull(user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$"));
    }

}
