package dev.williancorrea.manhwa.reader.minio;

import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploaderInterface {

  List<String> findAllObjects();

  InputStream findObjectByName(String fileName);

  String findObjectByNamePresigned(String fileName);

  void remove(String nameObject);

  String getFileAsBase64(String fileName);

  String uploadFile(MultipartFile file);

  String uploadStream(InputStream stream, String originalFileName, HttpHeaders headers, String folderName);
}