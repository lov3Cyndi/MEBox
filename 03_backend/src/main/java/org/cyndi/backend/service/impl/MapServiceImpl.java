package org.cyndi.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.RatingRequest;
import org.cyndi.backend.entity.GameMap;
import org.cyndi.backend.entity.Rating;
import org.cyndi.backend.mapper.GameMapMapper;
import org.cyndi.backend.mapper.RatingMapper;
import org.cyndi.backend.service.MapService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final GameMapMapper mapMapper;
    private final RatingMapper ratingMapper;

    @Override
    public Page<GameMap> listMaps(int page, int size) {
        Page<GameMap> pageParam = new Page<>(page, size);
        return mapMapper.selectPage(pageParam, new LambdaQueryWrapper<>());
    }

    @Override
    public GameMap getMap(Long id) {
        return mapMapper.selectById(id);
    }

    @Override
    public List<Rating> getRatings(Long mapId) {
        LambdaQueryWrapper<Rating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rating::getTargetType, "map")
               .eq(Rating::getTargetId, mapId)
               .orderByDesc(Rating::getCreateTime);
        return ratingMapper.selectList(wrapper);
    }

    @Override
    public Rating rateMap(Long mapId, Long userId, RatingRequest request) {
        LambdaQueryWrapper<Rating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rating::getTargetType, "map")
               .eq(Rating::getTargetId, mapId)
               .eq(Rating::getUserId, userId);
        Rating existingRating = ratingMapper.selectOne(wrapper);

        Rating rating;
        if (existingRating != null) {
            rating = existingRating;
            rating.setOverallRating(request.getOverallRating());
            rating.setComment(request.getComment());
            ratingMapper.updateById(rating);
        } else {
            rating = new Rating();
            rating.setUserId(userId);
            rating.setTargetType("map");
            rating.setTargetId(mapId);
            rating.setOverallRating(request.getOverallRating());
            rating.setComment(request.getComment());
            ratingMapper.insert(rating);
        }
        return rating;
    }

    @Override
    public GameMap createMap(GameMap map) {
        mapMapper.insert(map);
        return map;
    }

    @Override
    public GameMap updateMap(Long id, GameMap map) {
        GameMap existingMap = mapMapper.selectById(id);
        if (existingMap == null) {
            throw new RuntimeException("地图不存在");
        }
        map.setId(id);
        mapMapper.updateById(map);
        return mapMapper.selectById(id);
    }

    @Override
    public void deleteMap(Long id) {
        mapMapper.deleteById(id);
    }
}
