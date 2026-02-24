package dev.williancorrea.manhwa.reader.minio;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import dev.williancorrea.manhwa.reader.exception.ObjectNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/uploads")
public class UploadResource {

  private final FileUploaderInterface fileUploaderInterface;

  public UploadResource(FileUploaderInterface fileUploaderInterface) {
    this.fileUploaderInterface = fileUploaderInterface;
  }

  @GetMapping
  public ResponseEntity<List<String>> findAll() {
    return ResponseEntity.ok().body(fileUploaderInterface.findAllObjects());
  }

  @GetMapping("/{fileName}")
  public ResponseEntity<byte[]> findOne(@PathVariable String fileName) {
    try (InputStream inputStream = fileUploaderInterface.findObjectByName(fileName)) {
      byte[] file = inputStream.readAllBytes();
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.ALL_VALUE)
          .body(file);
    } catch (IOException e) {
      throw new ObjectNotFoundException("Error listing object: " + e.getMessage());
    }
  }

  @GetMapping("/{fileName}/Presigned")
  public ResponseEntity<String> findOnePresigned(@PathVariable String fileName) {
    return ResponseEntity.ok().body(fileUploaderInterface.findObjectByNamePresigned(fileName));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> upload(@RequestPart(value = "fileName") MultipartFile fileName) {
    return ResponseEntity.ok().body(fileUploaderInterface.uploadFile(fileName));
  }

  @DeleteMapping("/{fileName}")
  public ResponseEntity<String> delete(@PathVariable String fileName) {
    fileUploaderInterface.remove(fileName);
    return ResponseEntity.noContent().build();
  }

}