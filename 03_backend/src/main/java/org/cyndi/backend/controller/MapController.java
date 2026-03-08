package org.cyndi.backend.controller;

import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.RatingRequest;
import org.cyndi.backend.entity.GameMap;
import org.cyndi.backend.entity.Rating;
import org.cyndi.backend.security.UserDetailsImpl;
import org.cyndi.backend.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping
    public ResponseEntity<?> listMaps(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(mapService.listMaps(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMap(@PathVariable Long id) {
        return ResponseEntity.ok(mapService.getMap(id));
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<?> getRatings(@PathVariable Long id) {
        return ResponseEntity.ok(mapService.getRatings(id));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateMap(@PathVariable Long id, @RequestBody RatingRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(mapService.rateMap(id, userDetails.getId(), request));
    }
}
