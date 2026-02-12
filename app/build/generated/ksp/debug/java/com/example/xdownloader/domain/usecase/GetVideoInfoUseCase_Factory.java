package com.example.xdownloader.domain.usecase;

import com.example.xdownloader.data.repository.VideoRepository;
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
public final class GetVideoInfoUseCase_Factory implements Factory<GetVideoInfoUseCase> {
  private final Provider<VideoRepository> videoRepositoryProvider;

  private final Provider<ParseXLinkUseCase> parseXLinkUseCaseProvider;

  public GetVideoInfoUseCase_Factory(Provider<VideoRepository> videoRepositoryProvider,
      Provider<ParseXLinkUseCase> parseXLinkUseCaseProvider) {
    this.videoRepositoryProvider = videoRepositoryProvider;
    this.parseXLinkUseCaseProvider = parseXLinkUseCaseProvider;
  }

  @Override
  public GetVideoInfoUseCase get() {
    return newInstance(videoRepositoryProvider.get(), parseXLinkUseCaseProvider.get());
  }

  public static GetVideoInfoUseCase_Factory create(
      Provider<VideoRepository> videoRepositoryProvider,
      Provider<ParseXLinkUseCase> parseXLinkUseCaseProvider) {
    return new GetVideoInfoUseCase_Factory(videoRepositoryProvider, parseXLinkUseCaseProvider);
  }

  public static GetVideoInfoUseCase newInstance(VideoRepository videoRepository,
      ParseXLinkUseCase parseXLinkUseCase) {
    return new GetVideoInfoUseCase(videoRepository, parseXLinkUseCase);
  }
}
