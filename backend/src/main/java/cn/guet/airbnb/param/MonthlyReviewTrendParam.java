package cn.guet.airbnb.param;

import cn.guet.airbnb.entity.MonthlyReviewTrend;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MonthlyReviewTrendParam extends MonthlyReviewTrend {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;
}
