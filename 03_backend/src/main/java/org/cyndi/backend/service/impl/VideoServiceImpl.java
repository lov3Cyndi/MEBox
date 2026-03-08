package org.cyndi.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.CommentRequest;
import org.cyndi.backend.dto.VideoRequest;
import org.cyndi.backend.entity.Comment;
import org.cyndi.backend.entity.Video;
import org.cyndi.backend.entity.VideoLike;
import org.cyndi.backend.mapper.CommentMapper;
import org.cyndi.backend.mapper.VideoLikeMapper;
import org.cyndi.backend.mapper.VideoMapper;
import org.cyndi.backend.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoMapper videoMapper;
    private final CommentMapper commentMapper;
    private final VideoLikeMapper videoLikeMapper;

    @Override
    public Page<Video> listVideos(int page, int size) {
        Page<Video> pageParam = new Page<>(page, size);
        return videoMapper.selectPage(pageParam, new LambdaQueryWrapper<Video>().orderByDesc(Video::getCreateTime));
    }

    @Override
    public Video getVideo(Long id) {
        return videoMapper.selectById(id);
    }

    @Override
    @Transactional
    public Video uploadVideo(Long userId, VideoRequest request) {
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setUrl(request.getUrl());
        video.setThumbnail(request.getThumbnail());
        video.setUserId(userId);
        video.setHeroId(request.getHeroId());
        video.setMapId(request.getMapId());
        video.setGameType(request.getGameType());
        video.setLikeCount(0);
        videoMapper.insert(video);
        return video;
    }

    @Override
    @Transactional
    public void deleteVideo(Long id, Long userId) {
        Video video = videoMapper.selectById(id);
        if (video != null && video.getUserId().equals(userId)) {
            videoMapper.deleteById(id);
        } else {
            throw new RuntimeException("无权限删除此视频");
        }
    }

    @Override
    @Transactional
    public void likeVideo(Long videoId, Long userId) {
        LambdaQueryWrapper<VideoLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoLike::getVideoId, videoId)
               .eq(VideoLike::getUserId, userId);
        if (videoLikeMapper.selectCount(wrapper) == 0) {
            VideoLike like = new VideoLike();
            like.setVideoId(videoId);
            like.setUserId(userId);
            videoLikeMapper.insert(like);

            Video video = videoMapper.selectById(videoId);
            if (video != null) {
                video.setLikeCount(video.getLikeCount() + 1);
                videoMapper.updateById(video);
            }
        }
    }

    @Override
    @Transactional
    public void unlikeVideo(Long videoId, Long userId) {
        LambdaQueryWrapper<VideoLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoLike::getVideoId, videoId)
               .eq(VideoLike::getUserId, userId);
        VideoLike like = videoLikeMapper.selectOne(wrapper);
        if (like != null) {
            videoLikeMapper.deleteById(like.getId());

            Video video = videoMapper.selectById(videoId);
            if (video != null && video.getLikeCount() > 0) {
                video.setLikeCount(video.getLikeCount() - 1);
                videoMapper.updateById(video);
            }
        }
    }

    @Override
    public boolean isLiked(Long videoId, Long userId) {
        LambdaQueryWrapper<VideoLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoLike::getVideoId, videoId)
               .eq(VideoLike::getUserId, userId);
        return videoLikeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Comment> getComments(Long videoId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getVideoId, videoId)
               .orderByAsc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }

    @Override
    public Comment addComment(Long videoId, Long userId, CommentRequest request) {
        Comment comment = new Comment();
        comment.setVideoId(videoId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        commentMapper.insert(comment);
        return comment;
    }
}
