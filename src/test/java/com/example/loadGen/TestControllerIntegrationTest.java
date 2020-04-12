package com.example.loadGen;

import com.example.loadGen.domain.TestInstance;
import com.example.loadGen.domain.TestSession;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = LoadGenApplication.class
)
@AutoConfigureMockMvc

public class TestControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  private TestSession testSession;
  private Gson gson;
  private String json;

  @Before
  public void beforeTest() throws InterruptedException {
    this.testSession = new TestSession(5, "abc", 3);
    this.gson = new Gson();
    this.json = gson.toJson(testSession);
  }

  @After
  public void afterTest() throws Exception {
    mvc.perform(delete("/tests"));
  }

  @Test
  public void whenCreateInstance_shouldReturn200() throws Exception {
    mvc.perform(post("/test")
        .contentType(MediaType.APPLICATION_JSON).content(this.json))
        .andExpect(status().isOk());
  }

  @Test
  public void givenARunningInstance_whenCreateAnotherOne_shouldReturn400() throws Exception {
    mvc.perform(post("/test")
        .contentType(MediaType.APPLICATION_JSON).content(this.json))
        .andExpect(status().isOk());

    mvc.perform(post("/test")
        .contentType(MediaType.APPLICATION_JSON).content(this.json))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void whenDeleteAnInstance_shouldReturn200() throws Exception {
    var mvcResult = mvc.perform(post("/test")
        .contentType(MediaType.APPLICATION_JSON).content(this.json))
        .andExpect(status().isOk())
        .andReturn();
    var result = mvcResult.getResponse().getContentAsString();
    var testInstance = gson.fromJson(result, TestInstance.class);
    var testID = testInstance.getTestId();

    mvc.perform(delete("/tests/" + testID))
        .andExpect(status().isOk());
  }

  @Test
  public void whenDeleteAnInexistentInstance_shouldReturn400() throws Exception {
    UUID testID = UUID.randomUUID();

    mvc.perform(delete("/tests/" + testID))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void whenGetTestInstance_shouldReturn200() throws Exception {
    var mvcResult = mvc.perform(post("/test")
        .contentType(MediaType.APPLICATION_JSON).content(this.json))
        .andExpect(status().isOk())
        .andReturn();
    var result = mvcResult.getResponse().getContentAsString();
    var testInstance = gson.fromJson(result, TestInstance.class);
    var testID = testInstance.getTestId();

    mvc.perform(get("/tests/" + testID))
        .andExpect(status().isOk());
  }

  @Test
  public void whenGetTestInstances_shouldReturn200() throws Exception {
    var mvcResult = mvc.perform(post("/test")
        .contentType(MediaType.APPLICATION_JSON).content(this.json))
        .andExpect(status().isOk())
        .andReturn();
    var result = mvcResult.getResponse().getContentAsString();
    var testInstance = gson.fromJson(result, TestInstance.class);

    mvc.perform(get("/tests"))
        .andExpect(status().isOk());
  }
}
