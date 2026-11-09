package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.ShopForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.ShopValidationException;
import jp.kobe_u.cs27.allergydataservice.configuration.security.PasswordHasher;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuCategory;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Shop;
import jp.kobe_u.cs27.allergydataservice.domain.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jp.kobe_u.cs27.allergydataservice.configuration.exception.ShopErrorCode.*;

/**
 * 店舗に関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class ShopService {

  private final ShopRepository shops;
  private final MenuCategoryService categoryService;

  /**
   * 店舗を登録する
   *
   * @param form ShopForm
   * @return 登録した店舗の情報
   */
  public Shop createShop(ShopForm form) {

    // 店舗ログインIDを変数に格納する
    final String shopId = form.getShopId();

    // 店舗が登録済みであった場合、例外を投げる
    if (shops.existsById(shopId)) {
      throw new ShopValidationException(
          SHOP_ALREADY_EXISTS,
          "create the shop",
          String.format(
              "shop %s already exists",
              shopId));
    }

    // パスワードをソルト付きでハッシュ化する
    final String salt = PasswordHasher.generateSalt();
    final String hash = PasswordHasher.hash(form.getPassword(), salt);

    // 店舗をDBに登録し、登録した店舗の情報を戻り値として返す
    return shops.save(new Shop(
        shopId,
        form.getShopName(),
        hash,
        salt));
  }

  /**
   * 指定した店舗がDBに登録済みかどうか確認する
   *
   * @param shopId 店舗ログインID
   * @return 指定した店舗が存在するかどうかの真偽値(存在する場合にtrue)
   */
  public boolean existsShop(String shopId) {

    // 指定した店舗がDBに登録済みか確認し、結果を戻り値として返す
    return shops.existsById(shopId);
  }

  /**
   * 店舗の情報を取得する
   *
   * @param shopId 店舗ログインID
   * @return 店舗の情報
   */
  public Shop getShop(String shopId) {

    // 店舗をDB上で検索し、存在すれば戻り値として返し、存在しなければ例外を投げる
    return shops
        .findById(shopId)
        .orElseThrow(() -> new ShopValidationException(
            SHOP_DOES_NOT_EXIST,
            "get the shop",
            String.format(
                "shop %s does not exist",
                shopId)));
  }

  /**
   * 店舗ログインIDとパスワードを照合する
   *
   * @param shopId      店舗ログインID
   * @param rawPassword 平文パスワード
   * @return 認証に成功した店舗の情報
   */
  public Shop authenticate(String shopId, String rawPassword) {

    // 店舗をDBから取得する
    // 店舗が登録済みかどうかの確認も兼ねている
    Shop shop = getShop(shopId);

    // パスワードが一致しない場合、例外を投げる
    if (!PasswordHasher.matches(rawPassword, shop.getPasswordSalt(), shop.getPasswordHash())) {
      throw new ShopValidationException(
          SHOP_INVALID_PASSWORD,
          "authenticate the shop",
          String.format(
              "password for shop %s does not match",
              shopId));
    }

    return shop;
  }

  /**
   * 店舗を削除する
   * 店舗が作成したカテゴリ(と、それに紐づくメニュー・アレルゲン紐付け・原産地情報)も併せて削除する
   *
   * @param shopId 店舗ログインID
   */
  @Transactional
  public void deleteShop(String shopId) {

    // 店舗が存在しない場合、例外を投げる
    // 店舗が登録済みかどうかの確認も兼ねている
    getShop(shopId);

    for (MenuCategory category : categoryService.getCategoriesByShop(shopId)) {
      categoryService.deleteCategory(category.getCategoryId());
    }

    shops.deleteById(shopId);
  }

}
