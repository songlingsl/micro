package com.songling.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 餐盘表（餐盘批量生成）
 * </p>
 *
 * @author songling
 * @since 2020-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Plate implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 餐盘id
     */
    @TableId
    private Long plateId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 餐盘状态 0生效 1失效
     */
    private Integer status;

    private String plateUrl;
}
