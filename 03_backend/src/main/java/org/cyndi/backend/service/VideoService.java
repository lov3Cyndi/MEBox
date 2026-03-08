package org.cyndi.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.cyndi.backend.dto.CommentRequest;
import org.cyndi.backend.dto.VideoRequest;
import org.cyndi.backend.entity.Comment;
import org.cyndi.backend.entity.Video;

import java.util.List;

public interface VideoService {
    Page<Video> listVideos(int page, int size);
    Video getVideo(Long id);
    Video uploadVideo(Long userId, VideoRequest request);
    void deleteVideo(Long id, Long userId);
    void likeVideo(Long videoId, Long userId);
    void unlikeVideo(Long videoId, Long userId);
    boolean isLiked(Long videoId, Long userId);
    List<Comment> getComments(Long videoId);
    Comment addComment(Long videoId, Long userId, CommentRequest request);
}
