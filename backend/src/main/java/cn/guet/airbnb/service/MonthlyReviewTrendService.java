package cn.guet.airbnb.service;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.MonthlyReviewTrend;
import cn.guet.airbnb.param.MonthlyReviewTrendParam;

import java.util.List;

public interface MonthlyReviewTrendService {

    MonthlyReviewTrend saveOrUpdate(MonthlyReviewTrendParam param);

    void removeById(Long id);

    MonthlyReviewTrend getById(Long id);

    List<MonthlyReviewTrend> list(MonthlyReviewTrendParam param);

    PageResult<MonthlyReviewTrend> page(MonthlyReviewTrendParam param);
}
