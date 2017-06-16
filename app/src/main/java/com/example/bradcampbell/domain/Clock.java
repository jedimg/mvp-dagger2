package com.example.bradcampbell.domain;

public interface Clock {
  long millis();

  Clock REAL = System::currentTimeMillis;
}
