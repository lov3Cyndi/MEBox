package org.cyndi.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hero_media")
public class HeroMedia {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long heroId;

    private String type;

    private String title;

    private String url;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
