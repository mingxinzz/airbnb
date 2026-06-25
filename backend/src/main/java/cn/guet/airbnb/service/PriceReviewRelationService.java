package cn.guet.airbnb.service;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.PriceReviewRelation;
import cn.guet.airbnb.param.PriceReviewRelationParam;

import java.util.List;

public interface PriceReviewRelationService {

    PriceReviewRelation saveOrUpdate(PriceReviewRelationParam param);

    void removeById(Long id);

    PriceReviewRelation getById(Long id);

    List<PriceReviewRelation> list(PriceReviewRelationParam param);

    PageResult<PriceReviewRelation> page(PriceReviewRelationParam param);
}
