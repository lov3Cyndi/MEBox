package org.cyndi.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("heroes")
public class Hero {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String role;

    private String description;

    private String portrait;

    private String story;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
