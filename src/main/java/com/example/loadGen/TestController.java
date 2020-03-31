package com.example.loadGen;

import com.example.loadGen.domain.TestInstance;
import com.example.loadGen.domain.TestSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

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
    return testService.newTestInstance(testSession);
  };

  @GetMapping("/tests/{id}")
  TestInstance getTestInstance(@PathVariable UUID id) {
    return testService.getTestInstances().get(id);
  };

  @DeleteMapping("tests/{id}")
  void deleteTEstInstance(@PathVariable UUID id) {
    testService.deleteTestInstance(testService.getTestInstances().get(id));
  }
}
