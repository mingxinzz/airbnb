package cn.guet.airbnb.service;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.RegionAvgPrice;
import cn.guet.airbnb.param.RegionAvgPriceParam;

import java.util.List;

public interface RegionAvgPriceService {

    RegionAvgPrice saveOrUpdate(RegionAvgPriceParam param);

    void removeById(Long id);

    RegionAvgPrice getById(Long id);

    List<RegionAvgPrice> list(RegionAvgPriceParam param);

    PageResult<RegionAvgPrice> page(RegionAvgPriceParam param);
}
