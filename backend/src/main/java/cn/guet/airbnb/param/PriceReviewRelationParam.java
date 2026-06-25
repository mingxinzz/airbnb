package cn.guet.airbnb.param;

import cn.guet.airbnb.entity.PriceReviewRelation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PriceReviewRelationParam extends PriceReviewRelation {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;
}
