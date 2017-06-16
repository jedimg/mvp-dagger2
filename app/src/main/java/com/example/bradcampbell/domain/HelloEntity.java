package com.example.bradcampbell.domain;

import com.google.auto.value.AutoValue;
import android.os.Parcelable;

@AutoValue
public abstract class HelloEntity implements Parcelable {
  public abstract int value();
  public abstract long timestamp();

  public static HelloEntity create(int value, long timestamp) {
    return new AutoValue_HelloEntity(value, timestamp);
  }
}
