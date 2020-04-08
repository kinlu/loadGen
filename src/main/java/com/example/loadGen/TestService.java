package com.example.loadGen;

import com.example.loadGen.domain.TestInstance;
import com.example.loadGen.domain.TestSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Service
@Slf4j
public class TestService {

  private HashMap<UUID, TestInstance> testInstancePool;

  private ExecutorService executorService;

  private Future futureTestTask;

  public TestService() {
    this.testInstancePool = new HashMap<>();
    this.executorService = Executors.newSingleThreadExecutor();
  }

  public HashMap<UUID, TestInstance> getTestInstances() {
    return testInstancePool;
  }

  public TestInstance newTestInstance(TestSession testSession) {
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
    futureTestTask = this.executorService.submit(new TestSessionExecutor(newTestInstance));
    return newTestInstance;
  };

  public void deleteTestInstance(TestInstance testInstance) throws InterruptedException {
    log.info("Terminating " + testInstance.getTestId());
    futureTestTask.cancel(true);
    while(!futureTestTask.isCancelled()) {
      Thread.sleep(100);}
    testInstancePool.remove(testInstance.getTestId());
    log.info("Test " + testInstance.getTestId() + " is terminated and removed");
  }

  public void resetService() throws InterruptedException {
    if(this.futureTestTask != null){
      this.futureTestTask.cancel(true);
      while(!futureTestTask.isCancelled() && !futureTestTask.isDone()) {
        Thread.sleep(100);
      }
    }
    this.executorService = Executors.newSingleThreadExecutor();
    this.testInstancePool = new HashMap<>();
  }

  private class TestSessionExecutor implements Runnable {

    private TestInstance testInstance;

    public TestSessionExecutor(TestInstance testInstance) {
      this.testInstance = testInstance;
    }

    @Override
    public void run() {
      IntStream
          .range(0, testInstance.getTestSession().getNumberOfMessages())
          .parallel()
          .forEach(i -> {
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            log.info("Operation: " + i);
          });
      testInstance.setStatus("Done");
      log.info("Test " + testInstance.getTestId() + " is Done");
    }
  }

}
