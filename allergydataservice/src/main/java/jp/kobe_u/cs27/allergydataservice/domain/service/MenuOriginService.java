package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuForm;
import jp.kobe_u.cs27.allergydataservice.application.form.MenuOriginForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuOrigin;
import jp.kobe_u.cs27.allergydataservice.domain.repository.MenuOriginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * メニューの原産地情報に関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class MenuOriginService {

  private final MenuOriginRepository origins;

  /**
   * 原産地情報を登録する
   * 食材名が未入力の行は登録しない
   *
   * @param form   MenuForm
   * @param menuId メニューId
   */
  public void createOrigins(MenuForm form, Long menuId) {

    List<MenuOriginForm> originForms = form.getOriginForms();

    if (originForms == null) {
      return;
    }

    for (MenuOriginForm originForm : originForms) {
      if (originForm.getIngredientName() == null || originForm.getIngredientName().isBlank()) {
        continue;
      }

      origins.save(new MenuOrigin(
          null,
          menuId,
          originForm.getIngredientName(),
          originForm.getProducingArea()));
    }
  }

  /**
   * 指定したメニューの原産地情報の一覧を取得する
   *
   * @param menuId メニューId
   * @return 原産地情報の一覧
   */
  public List<MenuOrigin> getOriginsByMenu(Long menuId) {

    return origins.findByMenuId(menuId);
  }

  /**
   * 指定したメニューの原産地情報をすべて削除する
   *
   * @param menuId メニューId
   */
  @Transactional
  public void deleteByMenu(Long menuId) {

    origins.deleteByMenuId(menuId);
  }

}
