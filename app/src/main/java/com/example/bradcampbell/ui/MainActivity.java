package com.example.bradcampbell.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.example.bradcampbell.App;
import com.example.bradcampbell.R;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
  public static final String FLAG_COMMIT_FRAGMENT = "commitFragment";

  @Inject @Nullable LayoutInflaterFactory layoutInflaterHook;

  public static Intent getStartIntent(Context context, boolean commitFragment) {
    Intent intent = new Intent(context, MainActivity.class);
    intent.putExtra(FLAG_COMMIT_FRAGMENT, commitFragment);
    return intent;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    App.getAppComponent(this).inject(this);

    if (layoutInflaterHook != null) {
      LayoutInflater layoutInflater = LayoutInflater.from(this);
      LayoutInflaterCompat.setFactory(layoutInflater, layoutInflaterHook);
    }

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = getIntent();
    boolean commitFragment = intent.getBooleanExtra(FLAG_COMMIT_FRAGMENT, true);
    if (savedInstanceState == null && commitFragment) {
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.root, new HelloFragment())
          .commit();
    }
  }
}
