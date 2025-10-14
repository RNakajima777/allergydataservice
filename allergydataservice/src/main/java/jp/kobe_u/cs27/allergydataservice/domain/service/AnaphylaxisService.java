package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.AnaphylaxisForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Anaphylaxis;
import jp.kobe_u.cs27.allergydataservice.domain.repository.AnaphylaxisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import jp.kobe_u.cs27.allergydataservice.configuration.exception.AnaphylaxisAlreadyExistsException;

@Service
@RequiredArgsConstructor
@Transactional
public class AnaphylaxisService {

    private final AnaphylaxisRepository anaphylaxisRepository;

    public Anaphylaxis getAnaphylaxis(String uid) {
        return anaphylaxisRepository.findByUid(uid).orElse(new Anaphylaxis(null, uid, 0, "", ""));
    }

    // 新規作成メソッド
    public Anaphylaxis createAnaphylaxis(AnaphylaxisForm form) {
        // uidで重複チェック
        anaphylaxisRepository.findByUid(form.getUid()).ifPresent(a -> {
            throw new AnaphylaxisAlreadyExistsException("Anaphylaxis data for uid " + form.getUid() + " already exists.");
        });

        Anaphylaxis anaphylaxis = new Anaphylaxis();
        anaphylaxis.setUid(form.getUid());
        anaphylaxis.setEpipen(form.getEpipen());
        anaphylaxis.setEmergency(form.getEmergency());
        anaphylaxis.setSupport(form.getSupport());

        return anaphylaxisRepository.save(anaphylaxis);
    }

    // 更新メソッド
    public Anaphylaxis updateAnaphylaxis(AnaphylaxisForm form) {
        Anaphylaxis anaphylaxis = anaphylaxisRepository.findById(form.getAnaid())
                .orElseThrow(() -> new RuntimeException("Anaphylaxis data not found for anaid " + form.getAnaid()));

        anaphylaxis.setEpipen(form.getEpipen());
        anaphylaxis.setEmergency(form.getEmergency());
        anaphylaxis.setSupport(form.getSupport());

        return anaphylaxisRepository.save(anaphylaxis);
    }
}
