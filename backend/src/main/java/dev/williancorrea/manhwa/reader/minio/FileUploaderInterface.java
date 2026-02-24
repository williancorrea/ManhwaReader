package dev.williancorrea.manhwa.reader.minio;

import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploaderInterface {

  List<String> findAllObjects();

  InputStream findObjectByName(String fileName);

  String findObjectByNamePresigned(String fileName);

  void remove(String nameObject);

  String uploadFile(MultipartFile file);

  String uploadStream(InputStream stream, String originalFileName, long size, String folderName);
}