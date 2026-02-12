package com.example.xdownloader.service;

import android.content.Context;
import com.example.xdownloader.data.repository.DownloadRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DownloadManager_Factory implements Factory<DownloadManager> {
  private final Provider<Context> contextProvider;

  private final Provider<DownloadRepository> downloadRepositoryProvider;

  public DownloadManager_Factory(Provider<Context> contextProvider,
      Provider<DownloadRepository> downloadRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.downloadRepositoryProvider = downloadRepositoryProvider;
  }

  @Override
  public DownloadManager get() {
    return newInstance(contextProvider.get(), downloadRepositoryProvider.get());
  }

  public static DownloadManager_Factory create(Provider<Context> contextProvider,
      Provider<DownloadRepository> downloadRepositoryProvider) {
    return new DownloadManager_Factory(contextProvider, downloadRepositoryProvider);
  }

  public static DownloadManager newInstance(Context context,
      DownloadRepository downloadRepository) {
    return new DownloadManager(context, downloadRepository);
  }
}
