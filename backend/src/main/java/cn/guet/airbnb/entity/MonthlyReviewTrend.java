package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("monthly_review_trend")
public class MonthlyReviewTrend {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("review_month")
    private String reviewMonth;

    @TableField("review_count")
    private Integer reviewCount;
}
