package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("room_type_ratio")
public class RoomTypeRatio {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("room_type")
    private String roomType;

    @TableField("house_count")
    private Integer houseCount;

    @TableField("ratio")
    private BigDecimal ratio;
}
