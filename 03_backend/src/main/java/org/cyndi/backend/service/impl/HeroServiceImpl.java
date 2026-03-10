package org.cyndi.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.HeroMediaRequest;
import org.cyndi.backend.dto.RatingRequest;
import org.cyndi.backend.entity.Hero;
import org.cyndi.backend.entity.HeroMedia;
import org.cyndi.backend.entity.HeroRelation;
import org.cyndi.backend.entity.Rating;
import org.cyndi.backend.mapper.HeroMapper;
import org.cyndi.backend.mapper.HeroMediaMapper;
import org.cyndi.backend.mapper.HeroRelationMapper;
import org.cyndi.backend.mapper.RatingMapper;
import org.cyndi.backend.service.HeroService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeroServiceImpl implements HeroService {

    private final HeroMapper heroMapper;
    private final HeroMediaMapper heroMediaMapper;
    private final HeroRelationMapper heroRelationMapper;
    private final RatingMapper ratingMapper;

    @Override
    public Page<Hero> listHeroes(int page, int size, String role, String name) {
        Page<Hero> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Hero> wrapper = new LambdaQueryWrapper<>();
        if (role != null && !role.isEmpty()) {
            wrapper.eq(Hero::getRole, role);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(Hero::getName, name);
        }
        return heroMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Hero getHero(Long id) {
        return heroMapper.selectById(id);
    }

    @Override
    public List<String> getRoles() {
        return heroMapper.selectObjs(
            new LambdaQueryWrapper<Hero>()
                .select(Hero::getRole)
                .groupBy(Hero::getRole)
        ).stream().map(o -> (String) o).toList();
    }

    @Override
    public List<HeroRelation> getRelations(Long heroId) {
        LambdaQueryWrapper<HeroRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeroRelation::getHeroId, heroId);
        return heroRelationMapper.selectList(wrapper);
    }

    @Override
    public List<Rating> getRatings(Long heroId) {
        LambdaQueryWrapper<Rating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rating::getTargetType, "hero")
               .eq(Rating::getTargetId, heroId)
               .orderByDesc(Rating::getCreateTime);
        return ratingMapper.selectList(wrapper);
    }

    @Override
    public Rating rateHero(Long heroId, Long userId, RatingRequest request) {
        LambdaQueryWrapper<Rating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rating::getTargetType, "hero")
               .eq(Rating::getTargetId, heroId)
               .eq(Rating::getUserId, userId);
        Rating existingRating = ratingMapper.selectOne(wrapper);

        Rating rating;
        if (existingRating != null) {
            rating = existingRating;
            rating.setOverallRating(request.getOverallRating());
            rating.setDamageRating(request.getDamageRating());
            rating.setSurvivalRating(request.getSurvivalRating());
            rating.setUtilityRating(request.getUtilityRating());
            rating.setComment(request.getComment());
            ratingMapper.updateById(rating);
        } else {
            rating = new Rating();
            rating.setUserId(userId);
            rating.setTargetType("hero");
            rating.setTargetId(heroId);
            rating.setOverallRating(request.getOverallRating());
            rating.setDamageRating(request.getDamageRating());
            rating.setSurvivalRating(request.getSurvivalRating());
            rating.setUtilityRating(request.getUtilityRating());
            rating.setComment(request.getComment());
            ratingMapper.insert(rating);
        }
        return rating;
    }

    @Override
    public List<HeroMedia> getMedia(Long heroId) {
        LambdaQueryWrapper<HeroMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeroMedia::getHeroId, heroId);
        return heroMediaMapper.selectList(wrapper);
    }

    @Override
    public HeroMedia addMedia(Long heroId, HeroMediaRequest request) {
        HeroMedia media = new HeroMedia();
        media.setHeroId(heroId);
        media.setType(request.getType());
        media.setTitle(request.getTitle());
        media.setUrl(request.getUrl());
        heroMediaMapper.insert(media);
        return media;
    }

    @Override
    public Hero createHero(Hero hero) {
        heroMapper.insert(hero);
        return hero;
    }

    @Override
    public Hero updateHero(Long id, Hero hero) {
        Hero existingHero = heroMapper.selectById(id);
        if (existingHero == null) {
            throw new RuntimeException("英雄不存在");
        }
        hero.setId(id);
        heroMapper.updateById(hero);
        return heroMapper.selectById(id);
    }

    @Override
    public void deleteHero(Long id) {
        heroMapper.deleteById(id);
    }
}
