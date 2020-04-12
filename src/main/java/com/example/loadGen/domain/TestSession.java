package com.example.loadGen.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class TestSession {

  @Getter @Setter
  private int numberOfMessages;

  @Getter @Setter
  private String messageType;

  @Getter @Setter
  private int numberOfThreads;
}
