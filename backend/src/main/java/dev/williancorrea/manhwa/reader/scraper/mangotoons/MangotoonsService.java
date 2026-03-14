package dev.williancorrea.manhwa.reader.scraper.mangotoons;

import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.scraper.mangotoons.client.MangoClient;
import dev.williancorrea.manhwa.reader.scraper.mangotoons.client.MangoClient1;
import dev.williancorrea.manhwa.reader.scraper.mangotoons.client.MangoClientEncrypted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MangotoonsService {
  
  public final WorkService workService;
  public final MangoClient mangoClient;
  public final MangoClientEncrypted mangoClientEncrypted;

  private static final String CDN_MANOTOONS = "https://cdn.mangotoons.com/obras";

//  @PostConstruct
  public void start() throws Exception {

    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTI1MTksImVtYWlsIjoid2lsbGlhbi52YWdAZ21haWwuY29tIiwiaXNfYWRtaW4iOmZhbHNlLCJpYXQiOjE3NzMwNzg3ODIsImV4cCI6MTc3MzY4MzU4Mn0.oCfn2Yf-cqtMFu8vlO4tHeXLDVLJd4q2ZWHOCOSP_5U";

    MangoClient1 client = new MangoClient1(token);
    log.info("Tags: {}", client.getTags());
    log.info("Formatos: {}", client.getFormatos());
    log.info("Status: {}", client.getStatus());
    log.info("DATA: {}", client.getRecentes("23")); 

    var c = mangoClient.findAll(
        token,
        100,
        1,
        null,
        null,
        null
    );
    log.info("DATA: {}", c);
    
    var c2 = mangoClientEncrypted.findByStatus(token);    
    log.info("DATA: {}", c2);
}
}
