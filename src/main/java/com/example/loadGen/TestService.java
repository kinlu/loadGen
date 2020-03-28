package com.example.loadGen;

import com.example.loadGen.domain.TestInstance;
import com.example.loadGen.domain.TestInstancePool;
import com.example.loadGen.domain.TestSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class TestService {

  @Autowired
  private TestInstancePool testInstancePool;

  public HashMap<UUID, TestInstance> getTestInstances() {
    return testInstancePool.getTestInstances();
  }

  public void newTestInstance(TestSession testSession) {
    TestInstance newTestInstance = new TestInstance(testSession);
    testInstancePool.getTestInstances().put(newTestInstance.getTestId(), newTestInstance);
  };

  public void deleteTestInstance(TestInstance testInstance){
    testInstancePool.getTestInstances().remove(testInstance.getTestId());
  }
}
