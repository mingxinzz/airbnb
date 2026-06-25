package cn.guet.airbnb.param;

import cn.guet.airbnb.entity.CalendarPriceTrend;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CalendarPriceTrendParam extends CalendarPriceTrend {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;
}
