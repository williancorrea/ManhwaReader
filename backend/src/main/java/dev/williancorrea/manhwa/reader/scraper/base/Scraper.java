package dev.williancorrea.manhwa.reader.scraper.base;

import java.io.IOException;
import dev.williancorrea.manhwa.reader.features.work.Work;

public interface Scraper<T> {

  void ScheduledSynchronization();

  void synchronizeByExternalId(String externalId);
  void synchronizeByExternalId(T workDto, Long pageIndex, Long pageTotal);
  void synchronizeByWork(Work work);

  void prepareSyncTitle(Work work, T workDto);
  void prepareSyncAttributes(Work work, T workDto);
  void prepareSynchronization(Work work, T workDto);
  void prepareSyncSynopses(Work work, T workDto);
  void prepareSyncLinks(Work work, T workDto);
  void prepareSyncTags(Work work, T workDto);
  void prepareSyncAuthors(Work work, T workDto);
  void prepareSyncCover(Work work, T workDto) throws IOException, InterruptedException;
  void prepareSyncRelationships(Work work, T workDto);
  void prepareSyncChapters(Work work, T workDto, Long pageIndex, Long pageTotal);
}
