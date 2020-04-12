package com.example.loadGen;

import com.example.loadGen.domain.TestInstance;
import com.example.loadGen.domain.TestSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TestService {

  private HashMap<UUID, TestInstance> testInstancePool;

  private ExecutorService executorService;

  public TestService() {
    this.testInstancePool = new HashMap<>();
    this.executorService = null;
  }

  public HashMap<UUID, TestInstance> getTestInstances() {
    return testInstancePool;
  }

  public TestInstance newTestInstance(TestSession testSession) {
    var numberOfThreads = testSession.getNumberOfThreads();
    if(testInstancePool
        .entrySet()
        .stream()
        .anyMatch(instance
            -> instance.getValue().getStatus() == "Open")){
      return null;
    }
    TestInstance newTestInstance = new TestInstance(testSession);
    testInstancePool.put(newTestInstance.getTestId(), newTestInstance);
    log.info("Added a new test: " + newTestInstance.getTestId());
    this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    for(int i = 0; i < numberOfThreads; i++){
      executorService.execute(new TestSessionExecutor(newTestInstance, String.valueOf(i)));
    }
    return newTestInstance;
  };

  public void deleteTestInstance(TestInstance testInstance) {
    log.info("Terminating " + testInstance.getTestId());
    executorService.shutdownNow();
    testInstancePool.remove(testInstance.getTestId());
    log.info("Test " + testInstance.getTestId() + " is terminated and removed");
  }

  public void resetService() {
    if(this.executorService != null){
      executorService.shutdownNow();
    }
    this.executorService = Executors.newSingleThreadExecutor();
    this.testInstancePool = new HashMap<>();
  }

  private class TestSessionExecutor implements Runnable {

    private TestInstance testInstance;

    private String executorId;

    public TestSessionExecutor(TestInstance testInstance, String executorId) {
      this.testInstance = testInstance;
      this.executorId = executorId;
    }

    @Override
    public void run() {
     for (int i = 0; i < testInstance.getTestSession().getNumberOfMessages(); i++) {
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              log.info("Executor " + executorId + " is interrupted!");
              break;
            }
            log.info("Executor " + executorId + " - Operation: " + i);
          };
      testInstance.setStatus("Done");
      log.info("Executor " + executorId + " - Test " + testInstance.getTestId() + " is Done");
    }
  }

}
