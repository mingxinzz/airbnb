package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("region_house_count")
public class RegionHouseCount {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("neighbourhood_cleansed")
    private String neighbourhoodCleansed;

    @TableField("house_count")
    private Integer houseCount;
}
