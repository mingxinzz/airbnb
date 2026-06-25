package cn.guet.airbnb.service;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.CalendarPriceTrend;
import cn.guet.airbnb.param.CalendarPriceTrendParam;

import java.util.List;

public interface CalendarPriceTrendService {

    CalendarPriceTrend saveOrUpdate(CalendarPriceTrendParam param);

    void removeById(Long id);

    CalendarPriceTrend getById(Long id);

    List<CalendarPriceTrend> list(CalendarPriceTrendParam param);

    PageResult<CalendarPriceTrend> page(CalendarPriceTrendParam param);
}
