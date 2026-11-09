package jp.kobe_u.cs27.allergydataservice.configuration.web;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

/**
 * メニュー画像のアップロード・配信に関する設定
 * アップロードされた画像はアプリの実行ディレクトリ配下のuploads/menuフォルダに保存する
 * (デプロイ環境によっては保存先の見直しが必要)
 */
@Configuration
public class MenuImageWebConfig implements WebMvcConfigurer {

  public static final Path UPLOAD_DIR = Paths
      .get("uploads", "menu")
      .toAbsolutePath()
      .normalize();

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/uploads/menu/**")
        .addResourceLocations("file:" + UPLOAD_DIR + "/");
  }

  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofMegabytes(10));
    factory.setMaxRequestSize(DataSize.ofMegabytes(10));
    return factory.createMultipartConfig();
  }

}
