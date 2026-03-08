package org.cyndi.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hero_relations")
public class HeroRelation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long heroId;

    private Long relatedHeroId;

    private String relationType;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
