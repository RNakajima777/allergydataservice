package jp.kobe_u.cs27.allergydataservice.domain.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jp.kobe_u.cs27.allergydataservice.configuration.web.MenuImageWebConfig;

/**
 * メニュー画像をディスクに保存するサービスクラス
 */
@Service
public class MenuImageStorageService {

  /**
   * アップロードされた画像をディスクに保存する
   *
   * @param image アップロードされた画像(未選択の場合はnullまたは空)
   * @return 保存した画像を配信するパス。画像が未選択の場合はnull
   */
  public String store(MultipartFile image) {

    if (image == null || image.isEmpty()) {
      return null;
    }

    try {
      Files.createDirectories(MenuImageWebConfig.UPLOAD_DIR);

      String originalFilename = image.getOriginalFilename();
      String extension = "";
      if (originalFilename != null && originalFilename.contains(".")) {
        extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
      }

      String filename = UUID.randomUUID() + extension;
      Path target = MenuImageWebConfig.UPLOAD_DIR.resolve(filename);
      image.transferTo(target);

      return "/uploads/menu/" + filename;
    } catch (IOException e) {
      throw new IllegalStateException("failed to store the uploaded menu image", e);
    }
  }

}
