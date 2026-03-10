package org.cyndi.backend.controller;

import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.CommentRequest;
import org.cyndi.backend.dto.VideoRequest;
import org.cyndi.backend.entity.Comment;
import org.cyndi.backend.entity.Video;
import org.cyndi.backend.security.UserDetailsImpl;
import org.cyndi.backend.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/videos";

    @GetMapping
    public ResponseEntity<?> listVideos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(videoService.listVideos(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideo(@PathVariable Long id, Authentication authentication) {
        Video video = videoService.getVideo(id);
        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        if (authentication != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            result.put("liked", videoService.isLiked(id, userDetails.getId()));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> uploadVideo(@RequestBody VideoRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(videoService.uploadVideo(userDetails.getId(), request));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideoFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "thumbnail", required = false) String thumbnail,
            @RequestParam(value = "heroId", required = false) Long heroId,
            @RequestParam(value = "mapId", required = false) Long mapId,
            @RequestParam(value = "gameType", required = false) String gameType,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            // 创建上传目录
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 构建访问URL
            String fileUrl = "/api/videos/files/" + newFilename;

            // 创建视频记录
            VideoRequest request = new VideoRequest();
            request.setTitle(title);
            request.setDescription(description);
            request.setUrl(fileUrl);
            request.setThumbnail(thumbnail);
            request.setHeroId(heroId);
            request.setMapId(mapId);
            request.setGameType(gameType != null ? gameType : "快速匹配");

            return ResponseEntity.ok(videoService.uploadVideo(userDetails.getId(), request));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "文件上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<?> getVideoFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
            if (Files.exists(filePath)) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(Files.readAllBytes(filePath));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        videoService.deleteVideo(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeVideo(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        videoService.likeVideo(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikeVideo(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        videoService.unlikeVideo(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody CommentRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(videoService.addComment(id, userDetails.getId(), request));
    }
}
