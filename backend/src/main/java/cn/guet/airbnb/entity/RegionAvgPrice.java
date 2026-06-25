package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("region_avg_price")
public class RegionAvgPrice {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("neighbourhood_cleansed")
    private String neighbourhoodCleansed;

    @TableField("avg_price")
    private BigDecimal avgPrice;

    @TableField("min_price")
    private BigDecimal minPrice;

    @TableField("max_price")
    private BigDecimal maxPrice;

    @TableField("house_count")
    private Integer houseCount;
}
