package jp.kobe_u.cs27.allergydataservice.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 店舗機能に関するエラーコード
 * 既存のErrorCodeとは独立して管理する
 */
@AllArgsConstructor
@Getter
public enum ShopErrorCode {
  SHOP_DOES_NOT_EXIST(111),
  SHOP_ALREADY_EXISTS(112),
  SHOP_INVALID_PASSWORD(113);

  private final int code;
}
