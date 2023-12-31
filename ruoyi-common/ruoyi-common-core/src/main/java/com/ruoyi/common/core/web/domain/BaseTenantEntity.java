package com.ruoyi.common.core.web.domain;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity基类
 *
 * @author Lion Li
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTenantEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelProperty(value = "商户号")
    private String tenantName;

    /**
     * 商户ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Long tenantId;

}
