package com.example.bradcampbell.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.bradcampbell.App;
import com.example.bradcampbell.R;
import com.example.bradcampbell.presentation.HelloPresenter;
import com.example.bradcampbell.presentation.HelloView;

import javax.inject.Inject;

public class HelloFragment extends Fragment implements HelloView {
  @Inject HelloPresenter presenter;

  @Bind(R.id.text_view) TextView textView;
  @Bind(R.id.loading) View loadingView;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    App.getAppComponent(getActivity()).inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_hello1, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    presenter.setView(this);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    presenter.setView(null);
  }

  @Override public void display(CharSequence stuff) {
    textView.setText(stuff);
  }

  @Override public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
    textView.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    loadingView.setVisibility(View.GONE);
    textView.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.refresh) public void refresh() {
    presenter.refresh();
  }

  @OnClick(R.id.clear_memory_cache) public void clearMemoryCache() {
    presenter.clearMemoryCache();
  }

  @OnClick(R.id.clear_memory_and_disk_cache) public void clearMemoryAndDiskCache() {
    presenter.clearMemoryAndDiskCache();
  }
}
