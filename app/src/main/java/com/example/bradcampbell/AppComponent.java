package com.example.bradcampbell;

import com.example.bradcampbell.data.HelloDiskCache;
import com.example.bradcampbell.domain.HelloModel;
import com.example.bradcampbell.ui.HelloFragment;
import com.example.bradcampbell.ui.MainActivity;
import dagger.Component;

import javax.inject.Singleton;

@Component(
    modules = AppModule.class
)
@Singleton
public interface AppComponent {
  HelloModel getHelloModel();
  HelloDiskCache getHelloDiskCache();
  MainActivity inject(MainActivity activity);
  HelloFragment inject(HelloFragment fragment);
}
