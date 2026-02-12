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
public final class PauseDownloadUseCase_Factory implements Factory<PauseDownloadUseCase> {
  private final Provider<DownloadRepository> downloadRepositoryProvider;

  public PauseDownloadUseCase_Factory(Provider<DownloadRepository> downloadRepositoryProvider) {
    this.downloadRepositoryProvider = downloadRepositoryProvider;
  }

  @Override
  public PauseDownloadUseCase get() {
    return newInstance(downloadRepositoryProvider.get());
  }

  public static PauseDownloadUseCase_Factory create(
      Provider<DownloadRepository> downloadRepositoryProvider) {
    return new PauseDownloadUseCase_Factory(downloadRepositoryProvider);
  }

  public static PauseDownloadUseCase newInstance(DownloadRepository downloadRepository) {
    return new PauseDownloadUseCase(downloadRepository);
  }
}
