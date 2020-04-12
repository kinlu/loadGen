package com.example.loadGen;

import com.example.loadGen.domain.TestInstance;
import com.example.loadGen.domain.TestSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@RestController
public class TestController {

  @Autowired
  private TestService testService;

  @GetMapping("/tests")
  HashMap<UUID, TestInstance> all(){
    return testService.getTestInstances();
  }

  @PostMapping("/test")
  TestInstance newTestInstance(@RequestBody TestSession testSession){
    var response =  testService.newTestInstance(testSession);
    if(response == null) {
      log.warn("Another test instance is running.  Please request again after it's finish");
      throw new TestInstanceIsRunningException();
    }
    return response;
  };

  @GetMapping("/tests/{id}")
  TestInstance getTestInstance(@PathVariable UUID id) {
    return testService.getTestInstances().get(id);
  };

  @DeleteMapping("/tests/{id}")
  void deleteTestInstance(@PathVariable UUID id) {
    var testInstance = testService.getTestInstances().get(id);
    if(testInstance == null) {
      log.warn("Test instance " + id + " can not be found!");
      throw new TestInstanceNotFoundException();
    } else {
      testService.deleteTestInstance(testInstance);
    };
  }

  @DeleteMapping("/tests")
  void deleteAllTestInstance() {
    testService.resetService();
  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="The test instance is not found")
  class TestInstanceNotFoundException extends RuntimeException {
    public TestInstanceNotFoundException() {super("The test instance is not found", null, false, false);};
  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Another test instance is running")
  class TestInstanceIsRunningException extends RuntimeException {
    public TestInstanceIsRunningException() {super("Another test instance is running", null, false, false);};
  }
}
