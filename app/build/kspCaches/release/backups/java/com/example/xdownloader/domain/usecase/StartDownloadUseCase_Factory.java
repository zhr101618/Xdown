package com.example.xdownloader.domain.usecase;

import com.example.xdownloader.data.repository.DownloadRepository;
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
public final class StartDownloadUseCase_Factory implements Factory<StartDownloadUseCase> {
  private final Provider<DownloadRepository> downloadRepositoryProvider;

  public StartDownloadUseCase_Factory(Provider<DownloadRepository> downloadRepositoryProvider) {
    this.downloadRepositoryProvider = downloadRepositoryProvider;
  }

  @Override
  public StartDownloadUseCase get() {
    return newInstance(downloadRepositoryProvider.get());
  }

  public static StartDownloadUseCase_Factory create(
      Provider<DownloadRepository> downloadRepositoryProvider) {
    return new StartDownloadUseCase_Factory(downloadRepositoryProvider);
  }

  public static StartDownloadUseCase newInstance(DownloadRepository downloadRepository) {
    return new StartDownloadUseCase(downloadRepository);
  }
}
