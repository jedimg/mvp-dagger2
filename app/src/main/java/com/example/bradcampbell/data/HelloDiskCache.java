package com.example.bradcampbell.data;

import android.content.SharedPreferences;

import com.example.bradcampbell.domain.HelloEntity;
import rx.Observable;

import javax.inject.Inject;

public class HelloDiskCache {
  static final int NO_VALUE = -1;
  static final String KEY_DATA = "value-xyz-data";
  static final String KEY_TIMESTAMP = "value-xyz-timestamp";

  private SharedPreferences prefs;

  @Inject public HelloDiskCache(SharedPreferences sharedPreferences) {
    this.prefs = sharedPreferences;
  }

  public Observable<HelloEntity> getEntity() {
    return Observable.defer(() -> {
      Observable<HelloEntity> result;
      int data = prefs.getInt(KEY_DATA, NO_VALUE);
      if (data != NO_VALUE) {
        long timestamp = prefs.getLong(KEY_TIMESTAMP, 0L);
        result = Observable.just(HelloEntity.create(data, timestamp));
      } else {
        result = Observable.just(null);
      }
      return result;
    });
  }

  public Observable<Boolean> saveEntity(HelloEntity value) {
    return Observable.defer(() -> {
      SharedPreferences.Editor editor = prefs.edit();
      editor.putInt(KEY_DATA, value.value());
      editor.putLong(KEY_TIMESTAMP, value.timestamp());
      return Observable.just(editor.commit());
    });
  }

  public void clear() {
    SharedPreferences.Editor editor = prefs.edit();
    editor.remove(KEY_DATA);
    editor.remove(KEY_TIMESTAMP);
    editor.apply();
  }
}
