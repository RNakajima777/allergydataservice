package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.AnaphylaxisForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Anaphylaxis;
import jp.kobe_u.cs27.allergydataservice.domain.service.AnaphylaxisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AnaphylaxisController {

    private final AnaphylaxisService anaphylaxisService;

    @GetMapping("/anaphylaxis/{uid}")
    public String showAnaphylaxisPage(@PathVariable("uid") String uid, Model model) {
        Anaphylaxis anaphylaxis = anaphylaxisService.getAnaphylaxis(uid);

        AnaphylaxisForm form = new AnaphylaxisForm();
        form.setAnaid(anaphylaxis.getAnaid());
        form.setUid(anaphylaxis.getUid());
        form.setEpipen(anaphylaxis.getEpipen());
        form.setEmergency(anaphylaxis.getEmergency());
        form.setSupport(anaphylaxis.getSupport());

        model.addAttribute("anaphylaxisForm", form);
        return "anaphylaxis";
    }

    @PostMapping("/anaphylaxis/register")
    public String registerAnaphylaxis(@ModelAttribute AnaphylaxisForm form, RedirectAttributes redirectAttributes) {
        if (form.getAnaid() == null) {
            // 新規作成
            anaphylaxisService.createAnaphylaxis(form);
        } else {
            // 更新
            anaphylaxisService.updateAnaphylaxis(form);
        }
        redirectAttributes.addAttribute("uid", form.getUid());
        return "redirect:/anaphylaxis/{uid}";
    }
}
