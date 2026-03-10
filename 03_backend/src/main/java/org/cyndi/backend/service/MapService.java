package org.cyndi.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.cyndi.backend.dto.RatingRequest;
import org.cyndi.backend.entity.GameMap;
import org.cyndi.backend.entity.Rating;

import java.util.List;

public interface MapService {
    Page<GameMap> listMaps(int page, int size);
    GameMap getMap(Long id);
    List<Rating> getRatings(Long mapId);
    Rating rateMap(Long mapId, Long userId, RatingRequest request);
    GameMap createMap(GameMap map);
    GameMap updateMap(Long id, GameMap map);
    void deleteMap(Long id);
}
