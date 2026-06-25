package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.RoomTypeRatio;
import cn.guet.airbnb.mapper.RoomTypeRatioMapper;
import cn.guet.airbnb.param.RoomTypeRatioParam;
import cn.guet.airbnb.service.RoomTypeRatioService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RoomTypeRatioServiceImpl extends ServiceImpl<RoomTypeRatioMapper, RoomTypeRatio>
        implements RoomTypeRatioService {

    @Override
    public RoomTypeRatio saveOrUpdate(RoomTypeRatioParam param) {
        if (!StringUtils.hasText(param.getRoomType())) {
            throw new BusinessException(400, "房型不能为空");
        }
        if (param.getHouseCount() == null) {
            throw new BusinessException(400, "房源数量不能为空");
        }
        if (param.getRatio() == null) {
            throw new BusinessException(400, "占比不能为空");
        }

        RoomTypeRatio entity = new RoomTypeRatio();
        BeanUtils.copyProperties(param, entity);
        super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void removeById(Long id) {
        RoomTypeRatio existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "记录不存在");
        }
        super.removeById(id);
    }

    @Override
    public RoomTypeRatio getById(Long id) {
        RoomTypeRatio entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return entity;
    }

    @Override
    public List<RoomTypeRatio> list(RoomTypeRatioParam param) {
        return lambdaQuery()
                .like(StringUtils.hasText(param.getKeyword()), RoomTypeRatio::getRoomType, param.getKeyword())
                .like(StringUtils.hasText(param.getRoomType()), RoomTypeRatio::getRoomType, param.getRoomType())
                .eq(param.getHouseCount() != null, RoomTypeRatio::getHouseCount, param.getHouseCount())
                .eq(param.getRatio() != null, RoomTypeRatio::getRatio, param.getRatio())
                .orderByDesc(RoomTypeRatio::getHouseCount)
                .orderByDesc(RoomTypeRatio::getId)
                .list();
    }

    @Override
    public PageResult<RoomTypeRatio> page(RoomTypeRatioParam param) {
        Page<RoomTypeRatio> page = new Page<>(param.getPageNum(), param.getPageSize());
        return PageResult.of(baseMapper.selectPage(page, param));
    }
}
