package dev.williancorrea.manhwa.reader.features.chapter.notify;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ChapterNotifyService {

  private final ChapterNotifyRepository repository;

  public ChapterNotifyService(@Lazy ChapterNotifyRepository repository) {
    this.repository = repository;
  }

  public ChapterNotify save(ChapterNotify entity) {
    return repository.save(entity);
  }
}


