package com.example.xdownloader.data.repository;

import com.example.xdownloader.data.api.XVideoApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class VideoRepository_Factory implements Factory<VideoRepository> {
  private final Provider<XVideoApiService> apiServiceProvider;

  public VideoRepository_Factory(Provider<XVideoApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public VideoRepository get() {
    return newInstance(apiServiceProvider.get());
  }

  public static VideoRepository_Factory create(Provider<XVideoApiService> apiServiceProvider) {
    return new VideoRepository_Factory(apiServiceProvider);
  }

  public static VideoRepository newInstance(XVideoApiService apiService) {
    return new VideoRepository(apiService);
  }
}
