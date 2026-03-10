package org.cyndi.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.cyndi.backend.dto.HeroMediaRequest;
import org.cyndi.backend.dto.RatingRequest;
import org.cyndi.backend.entity.Hero;
import org.cyndi.backend.entity.HeroMedia;
import org.cyndi.backend.entity.HeroRelation;
import org.cyndi.backend.entity.Rating;

import java.util.List;

public interface HeroService {
    Page<Hero> listHeroes(int page, int size, String role, String name);
    Hero getHero(Long id);
    List<String> getRoles();
    List<HeroRelation> getRelations(Long heroId);
    List<Rating> getRatings(Long heroId);
    Rating rateHero(Long heroId, Long userId, RatingRequest request);
    List<HeroMedia> getMedia(Long heroId);
    HeroMedia addMedia(Long heroId, HeroMediaRequest request);
    Hero createHero(Hero hero);
    Hero updateHero(Long id, Hero hero);
    void deleteHero(Long id);
}
