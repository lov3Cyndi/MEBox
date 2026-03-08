package org.cyndi.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("video_likes")
public class VideoLike {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
