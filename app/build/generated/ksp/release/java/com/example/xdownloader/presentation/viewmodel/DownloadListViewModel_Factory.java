package com.example.xdownloader.presentation.viewmodel;

import com.example.xdownloader.service.DownloadManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DownloadListViewModel_Factory implements Factory<DownloadListViewModel> {
  private final Provider<DownloadManager> downloadManagerProvider;

  public DownloadListViewModel_Factory(Provider<DownloadManager> downloadManagerProvider) {
    this.downloadManagerProvider = downloadManagerProvider;
  }

  @Override
  public DownloadListViewModel get() {
    return newInstance(downloadManagerProvider.get());
  }

  public static DownloadListViewModel_Factory create(
      Provider<DownloadManager> downloadManagerProvider) {
    return new DownloadListViewModel_Factory(downloadManagerProvider);
  }

  public static DownloadListViewModel newInstance(DownloadManager downloadManager) {
    return new DownloadListViewModel(downloadManager);
  }
}
