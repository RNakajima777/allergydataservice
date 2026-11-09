package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuCategoryForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Menu;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuCategory;
import jp.kobe_u.cs27.allergydataservice.domain.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * メニューカテゴリに関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class MenuCategoryService {

  private final MenuCategoryRepository categories;
  private final MenuService menuService;

  /**
   * カテゴリを登録する
   *
   * @param shopId 店舗ログインID
   * @param form   MenuCategoryForm
   * @return 登録したカテゴリの情報
   */
  public MenuCategory createCategory(String shopId, MenuCategoryForm form) {

    return categories.save(new MenuCategory(
        null,
        shopId,
        form.getCategoryName()));
  }

  /**
   * 指定した店舗が作成したカテゴリの一覧を取得する
   *
   * @param shopId 店舗ログインID
   * @return カテゴリの一覧
   */
  public List<MenuCategory> getCategoriesByShop(String shopId) {

    return categories.findByShopIdOrderByCategoryIdAsc(shopId);
  }

  /**
   * カテゴリの情報を取得する
   *
   * @param categoryId カテゴリId
   * @return カテゴリの情報
   */
  public MenuCategory getCategory(Long categoryId) {

    return categories
        .findById(categoryId)
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "category %d does not exist",
            categoryId)));
  }

  /**
   * カテゴリを削除する
   * カテゴリ内に登録されているメニュー(と、それに付随するアレルゲン紐付け・原産地情報)も併せて削除する
   *
   * @param categoryId カテゴリId
   */
  @Transactional
  public void deleteCategory(Long categoryId) {

    for (Menu menu : menuService.getMenusByCategory(categoryId)) {
      menuService.deleteMenu(menu.getMenuId());
    }

    categories.deleteById(categoryId);
  }

}
