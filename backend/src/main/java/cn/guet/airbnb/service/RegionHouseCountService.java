package cn.guet.airbnb.service;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.RegionHouseCount;
import cn.guet.airbnb.param.RegionHouseCountParam;

import java.util.List;

public interface RegionHouseCountService {

    RegionHouseCount saveOrUpdate(RegionHouseCountParam param);

    void removeById(Long id);

    RegionHouseCount getById(Long id);

    List<RegionHouseCount> list(RegionHouseCountParam param);

    PageResult<RegionHouseCount> page(RegionHouseCountParam param);
}
