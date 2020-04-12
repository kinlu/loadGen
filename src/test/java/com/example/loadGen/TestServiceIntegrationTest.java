package com.example.loadGen;

import com.example.loadGen.domain.TestInstance;
import com.example.loadGen.domain.TestSession;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class TestServiceIntegrationTest {

  @TestConfiguration
  static class TestServiceImpl {

    @Bean
    public TestService testService(){
      return new TestService();
    }
  }

  @Autowired
  TestService testService;

  @After
  public void cleanUpState() throws InterruptedException {
    testService.resetService();
  }

  @Test
  public void shouldCreateNewInstance(){
    var numberOfMessage = 10;
    var messageType = "ABC";
    var numberOfThread = 4;
    TestSession testSession = new TestSession(numberOfMessage, messageType, numberOfThread);
    TestInstance testInstance = testService.newTestInstance(testSession);
    assertThat(testInstance.getTestId().toString()).isNotBlank();
    assertThat(testInstance.getStatus()).isEqualTo("Open");
    assertThat(testInstance.getTestSession().getNumberOfMessages()).isEqualTo(numberOfMessage);
    assertThat(testInstance.getTestSession().getMessageType()).isEqualTo(messageType);
  }

  @Test
  public void shouldHaveInstanceExecuted() throws InterruptedException {
    var numberOfMessage = 1;
    var messageType = "ABC";
    var numberOfThread = 4;
    TestSession testSession = new TestSession(numberOfMessage, messageType, numberOfThread);
    TestInstance testInstance = testService.newTestInstance(testSession);
    assertThat(testInstance.getTestId().toString()).isNotBlank();
    Thread.sleep(11000);
    assertThat(testInstance.getStatus()).isEqualTo("Done");
  }

  @Test
  public void shouldGetTestInstances() {
    var numberOfMessage = 1;
    var messageType = "ABC";
    var numberOfThread = 1;
    TestSession testSession = new TestSession(numberOfMessage, messageType, numberOfThread);
    TestInstance testInstance = testService.newTestInstance(testSession);
    assertThat(testService.getTestInstances().containsKey(testInstance.getTestId())).isTrue();
    assertThat(testService.getTestInstances().containsValue(testInstance)).isTrue();
  }

  @Test
  public void shouldDeleteTestInstance() throws InterruptedException {
    var numberOfMessage = 10;
    var messageType = "ABC";
    var numberOfThread = 1;
    TestSession testSession = new TestSession(numberOfMessage, messageType, numberOfThread);
    TestInstance testInstance = testService.newTestInstance(testSession);
    testService.deleteTestInstance(testInstance);
    assertThat(testService.getTestInstances().isEmpty()).isTrue();
  }
}
