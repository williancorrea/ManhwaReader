package dev.williancorrea.manhwa.reader.features.library;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class LibraryService {

  private final LibraryRepository repository;

  public LibraryService(@Lazy LibraryRepository repository) {
    this.repository = repository;
  }

  public List<Library> findAll() {
    return repository.findAll();
  }

  public Optional<Library> findById(UUID id) {
    return repository.findById(id);
  }

  public Library save(Library entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public Optional<Library> findByUserAndWork(User user, Work work) {
    return repository.findByUser_IdAndWork_Id(user.getId(), work.getId());
  }

  @Transactional
  public Library saveOrUpdate(User user, Work work, LibraryStatus status) {
    Library library = repository.findByUser_IdAndWork_Id(user.getId(), work.getId())
        .orElse(Library.builder().user(user).work(work).build());
    library.setStatus(status);
    return repository.save(library);
  }

  @Transactional
  public void deleteByUserAndWork(User user, Work work) {
    repository.deleteByUserIdAndWorkId(user.getId(), work.getId());
  }
}

