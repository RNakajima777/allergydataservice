package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.UserForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.UserValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.entity.User;
import jp.kobe_u.cs27.allergydataservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jp.kobe_u.cs27.allergydataservice.configuration.exception.ErrorCode.*;

/**
 * ユーザに関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository users;
  //private final FoodRepository foods;
  
  /**
   * // DI(Dependency Injecion)デザインパターン
   * @Autowired
   * public UserService(UserRepository users, HealthConditonRepository conditions){
   *   this.users = users;
   *   this.conditons = conditions;
   * }
   */

  /**
   * ユーザを登録する
   *
   * @param form UserForm
   * @return 登録したユーザの情報
   */
  public User createUser(UserForm form) {

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザが登録済みであった場合、例外を投げる
    if (users.existsById(uid)) {
      throw new UserValidationException(
          USER_ALREADY_EXISTS,
          "create the user",
          String.format(
              "user %s already exists",
              uid));
    }

    // ユーザをDBに登録し、登録したユーザの情報を戻り値として返す
    return users.save(new User(
        uid,
        form.getUsername(), 
        form.getSex(), 
        form.getBirthday()
        ));
  }

  /**
   * 指定したユーザがDBに登録済みかどうか確認する
   *
   * @param uid ユーザID
   * @return 指定したユーザが存在するかどうかの真偽値(存在する場合にtrue)
   */
  public boolean existsUser(String uid) {

    // 指定したユーザがDBに登録済みか確認し、結果を戻り値として返す
    return users.existsById(uid);
  }

  /**
   * ユーザの情報を取得する
   *
   * @param uid ユーザID
   * @return ユーザの情報
   */
  public User getUser(String uid) {

    // ユーザをDB上で検索し、存在すれば戻り値として返し、存在しなければ例外を投げる
    return users
        .findById(uid)
        .orElseThrow(() -> new UserValidationException(
            USER_DOES_NOT_EXIST,
            "get the user",
            String.format(
                "user %s does not exist",
                uid)));

  }

  /**
   * ユーザの情報を更新する
   *
   * @param form UserForm
   * @return 更新したユーザの情報
   */
  public User updateUser(UserForm form) {

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザが存在しない場合、例外を投げる
    if (!users.existsById(uid)) {
      throw new UserValidationException(
          USER_DOES_NOT_EXIST,
          "update the user",
          String.format(
              "user %s does not exist",
              uid));
    }

    // DB上のユーザ情報を更新し、新しいユーザ情報を戻り値として返す
    return users.save(new User(
        uid,
        form.getUsername(), 
        form.getSex(), 
        form.getBirthday()
        ));
  }

  /**
   * ユーザを削除する
   * 処理に失敗した場合、このメソッド中のDB操作はすべてロールバックされる
   *
   * @param uid ユーザID
   */
  @Transactional
  public void deleteUser(String uid) {

    // ユーザが存在しない場合、例外を投げる
    if (!users.existsById(uid)) {
      throw new UserValidationException(
          USER_DOES_NOT_EXIST,
          "delete the user",
          String.format(
              "user %s does not exist",
              uid));
    }

    // ユーザを削除する
    users.deleteById(uid);
    // ユーザに紐づいた体調記録を全て削除する
    //リポジトリ完成したら↓のコメントアウトを外す
    //foods.deleteByUid(uid);

  }

}
