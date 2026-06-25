package cn.guet.airbnb.param;

import cn.guet.airbnb.entity.RoomTypeRatio;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoomTypeRatioParam extends RoomTypeRatio {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;
}
