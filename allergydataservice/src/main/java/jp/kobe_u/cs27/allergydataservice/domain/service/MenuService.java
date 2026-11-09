package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Menu;
import jp.kobe_u.cs27.allergydataservice.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * メニューに関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class MenuService {

  private final MenuRepository menus;
  private final MenuAllergenService menuAllergenService;
  private final MenuOriginService menuOriginService;

  /**
   * メニューを登録する
   * メニュー本体に加え、アレルゲンの含有・コンタミ状況、原産地情報も併せて登録する
   *
   * @param form   MenuForm
   * @param shopId 店舗ログインID
   * @return 登録したメニューの情報
   */
  public Menu createMenu(MenuForm form, String shopId) {

    Menu savedMenu = menus.save(new Menu(
        null,
        shopId,
        form.getCategoryId(),
        form.getMenuName(),
        form.getImagePath(),
        form.getPrice(),
        form.getComment()));

    Long menuId = savedMenu.getMenuId();

    menuAllergenService.createMenuAllergens(form, menuId);
    menuOriginService.createOrigins(form, menuId);

    return savedMenu;
  }

  /**
   * メニューの情報を取得する
   *
   * @param menuId メニューId
   * @return メニューの情報
   */
  public Menu getMenu(Long menuId) {

    return menus
        .findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "menu %d does not exist",
            menuId)));
  }

  /**
   * 指定したカテゴリに登録されているメニューの一覧を取得する
   *
   * @param categoryId カテゴリId
   * @return メニューの一覧
   */
  public List<Menu> getMenusByCategory(Long categoryId) {

    return menus.findByCategoryIdOrderByMenuIdAsc(categoryId);
  }

  /**
   * メニューを削除する
   * 付随するアレルゲン紐付け・原産地情報も併せて削除する
   *
   * @param menuId メニューId
   */
  @Transactional
  public void deleteMenu(Long menuId) {

    menuAllergenService.deleteByMenu(menuId);
    menuOriginService.deleteByMenu(menuId);
    menus.deleteById(menuId);
  }

}
