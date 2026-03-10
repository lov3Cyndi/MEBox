package org.cyndi.backend.controller;

import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.HeroMediaRequest;
import org.cyndi.backend.dto.RatingRequest;
import org.cyndi.backend.entity.Hero;
import org.cyndi.backend.entity.HeroMedia;
import org.cyndi.backend.entity.HeroRelation;
import org.cyndi.backend.entity.Rating;
import org.cyndi.backend.security.UserDetailsImpl;
import org.cyndi.backend.service.HeroService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heroes")
@RequiredArgsConstructor
public class HeroController {

    private final HeroService heroService;

    @GetMapping
    public ResponseEntity<?> listHeroes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(heroService.listHeroes(page, size, role, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHero(@PathVariable Long id) {
        return ResponseEntity.ok(heroService.getHero(id));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(heroService.getRoles());
    }

    @GetMapping("/{id}/relations")
    public ResponseEntity<?> getRelations(@PathVariable Long id) {
        return ResponseEntity.ok(heroService.getRelations(id));
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<?> getRatings(@PathVariable Long id) {
        return ResponseEntity.ok(heroService.getRatings(id));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateHero(@PathVariable Long id, @RequestBody RatingRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(heroService.rateHero(id, userDetails.getId(), request));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<?> getMedia(@PathVariable Long id) {
        return ResponseEntity.ok(heroService.getMedia(id));
    }

    @PostMapping("/{id}/media")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addMedia(@PathVariable Long id, @RequestBody HeroMediaRequest request) {
        return ResponseEntity.ok(heroService.addMedia(id, request));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createHero(@RequestBody Hero hero) {
        return ResponseEntity.ok(heroService.createHero(hero));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateHero(@PathVariable Long id, @RequestBody Hero hero) {
        return ResponseEntity.ok(heroService.updateHero(id, hero));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteHero(@PathVariable Long id) {
        heroService.deleteHero(id);
        return ResponseEntity.ok().build();
    }
}
