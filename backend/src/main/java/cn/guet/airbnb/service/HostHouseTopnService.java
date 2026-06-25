package cn.guet.airbnb.service;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.HostHouseTopn;
import cn.guet.airbnb.param.HostHouseTopnParam;

import java.util.List;

public interface HostHouseTopnService {

    HostHouseTopn saveOrUpdate(HostHouseTopnParam param);

    void removeById(Long id);

    HostHouseTopn getById(Long id);

    List<HostHouseTopn> list(HostHouseTopnParam param);

    PageResult<HostHouseTopn> page(HostHouseTopnParam param);
}
