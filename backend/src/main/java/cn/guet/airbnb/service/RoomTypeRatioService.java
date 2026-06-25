package cn.guet.airbnb.service;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.RoomTypeRatio;
import cn.guet.airbnb.param.RoomTypeRatioParam;

import java.util.List;

public interface RoomTypeRatioService {

    RoomTypeRatio saveOrUpdate(RoomTypeRatioParam param);

    void removeById(Long id);

    RoomTypeRatio getById(Long id);

    List<RoomTypeRatio> list(RoomTypeRatioParam param);

    PageResult<RoomTypeRatio> page(RoomTypeRatioParam param);
}
