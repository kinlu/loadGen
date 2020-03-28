package com.example.loadGen.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component @AllArgsConstructor
public class TestInstancePool {

  @Getter @Setter
  private HashMap<UUID, TestInstance> testInstances;

}
