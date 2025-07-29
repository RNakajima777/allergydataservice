package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.RestaurantForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Restaurant;
import jp.kobe_u.cs27.allergydataservice.domain.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;

    @PostMapping("/restaurant/register")
    public String registerRestaurant(
            RedirectAttributes attributes,
            @ModelAttribute @Validated RestaurantForm form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.restaurantForm", bindingResult);
            attributes.addFlashAttribute("restaurantForm", form);
            return "redirect:/qr/" + form.getUid();
        }

        service.createRestaurant(form);

        attributes.addAttribute("uid", form.getUid());
        return "redirect:/qr/{uid}";
    }

    @GetMapping("/restaurant/list/{uid}")
    public String showRestaurantListPage(@PathVariable("uid") String uid, Model model) {
        List<Restaurant> memos = service.getRestaurantListByUid(uid);
        model.addAttribute("memos", memos);
        model.addAttribute("uid", uid);
        // 編集用に空のフォームオブジェクトも渡しておく
        if (!model.containsAttribute("restaurantForm")) {
            model.addAttribute("restaurantForm", new RestaurantForm());
        }
        return "restaurantMemoList";
    }

    @PostMapping("/restaurant/update/{restaurantid}")
    public String updateRestaurant(
            @PathVariable("restaurantid") Long restaurantid,
            @ModelAttribute @Validated RestaurantForm form,
            BindingResult bindingResult,
            RedirectAttributes attributes) {


        String uid = service.getRestaurantByRestaurantid(restaurantid).getUid();
        attributes.addAttribute("uid", uid);

        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.restaurantForm", bindingResult);
            attributes.addFlashAttribute("restaurantForm", form);
            return "redirect:/restaurant/list/{uid}";
        }

        service.updateRestaurant(form, restaurantid);

        return "redirect:/restaurant/list/{uid}";
    }

    @PostMapping("/restaurant/delete/{restaurantid}")
    public String deleteRestaurant(@PathVariable("restaurantid") Long restaurantid, RedirectAttributes attributes) {
        String uid = service.getRestaurantByRestaurantid(restaurantid).getUid();
        service.deleteRestaurantByRestaurantid(restaurantid);
        attributes.addAttribute("uid", uid);
        return "redirect:/restaurant/list/{uid}";
    }
}
