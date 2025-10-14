package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.UserForm;
import jp.kobe_u.cs27.allergydataservice.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import jp.kobe_u.cs27.allergydataservice.application.form.UidForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jp.kobe_u.cs27.allergydataservice.application.form.RestaurantForm;

@Controller
@RequiredArgsConstructor
public class PageController {
  private final UserService userService;

  @GetMapping("/")
  public String showLandingPage() {
    return "index";
  }

  @GetMapping("/user/signup")
  public String showUserRegistrationPage(Model model) {

    // UserFormをModelに追加する(Thymeleaf上ではuserForm)
    model.addAttribute(new UserForm());

    return "userRegister";
  }

  @GetMapping("/qr/{uid}")
  public String showFoodQRCode(@PathVariable("uid") String uid, Model model) {
      String qrDataUrl = "https://es4.eedept.kobe-u.ac.jp/allergydataservice/data/" + uid; // QRコードに埋め込むURL
      model.addAttribute("qrDataUrl", qrDataUrl);
      model.addAttribute("uid", uid);
      model.addAttribute(new RestaurantForm());
      return "foodQRPage"; // QRコードを表示するHTML
  }

}
