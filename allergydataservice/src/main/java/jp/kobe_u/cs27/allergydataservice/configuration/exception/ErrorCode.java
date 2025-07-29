package jp.kobe_u.cs27.allergydataservice.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * エラーコード
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
  USER_DOES_NOT_EXIST(11),
  USER_ALREADY_EXISTS(12),
  ALLERGEN_ALREADY_EXISTS(22),
  RESTAURANT_DOES_NOT_EXIST(31),
  RESTAURANT_ALREADY_EXISTS(32),
  CONTROLLER_VALIDATION_ERROR(97),
  CONTROLLER_REJECTED(98),
  OTHER_ERROR(99);

  private final int code;
}
