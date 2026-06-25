package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("calendar_price_trend")
public class CalendarPriceTrend {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("stat_month")
    private String statMonth;

    @TableField("avg_price")
    private BigDecimal avgPrice;

    @TableField("sample_count")
    private Integer sampleCount;
}
