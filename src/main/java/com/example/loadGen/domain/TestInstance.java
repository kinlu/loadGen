package com.example.loadGen.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
public class TestInstance {

  @Getter
  private UUID testId;

  @Getter @Setter
  private String status;

  @Getter
  private TestSession testSession;

  @Autowired
  public TestInstance(TestSession testSession) {
    this.testId = UUID.randomUUID();
    this.status = "Open";
    this.testSession = testSession;
  }
}
