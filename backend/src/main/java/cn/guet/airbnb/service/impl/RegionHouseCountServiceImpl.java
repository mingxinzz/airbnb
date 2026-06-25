package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.RegionHouseCount;
import cn.guet.airbnb.mapper.RegionHouseCountMapper;
import cn.guet.airbnb.param.RegionHouseCountParam;
import cn.guet.airbnb.service.RegionHouseCountService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RegionHouseCountServiceImpl extends ServiceImpl<RegionHouseCountMapper, RegionHouseCount>
        implements RegionHouseCountService {

    @Override
    public RegionHouseCount saveOrUpdate(RegionHouseCountParam param) {
        if (!StringUtils.hasText(param.getNeighbourhoodCleansed())) {
            throw new BusinessException(400, "区域名称不能为空");
        }
        if (param.getHouseCount() == null) {
            throw new BusinessException(400, "房源数量不能为空");
        }

        RegionHouseCount entity = new RegionHouseCount();
        BeanUtils.copyProperties(param, entity);
        super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void removeById(Long id) {
        RegionHouseCount existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "记录不存在");
        }
        super.removeById(id);
    }

    @Override
    public RegionHouseCount getById(Long id) {
        RegionHouseCount entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return entity;
    }

    @Override
    public List<RegionHouseCount> list(RegionHouseCountParam param) {
        return lambdaQuery()
                .like(StringUtils.hasText(param.getKeyword()), RegionHouseCount::getNeighbourhoodCleansed, param.getKeyword())
                .like(StringUtils.hasText(param.getNeighbourhoodCleansed()), RegionHouseCount::getNeighbourhoodCleansed, param.getNeighbourhoodCleansed())
                .eq(param.getHouseCount() != null, RegionHouseCount::getHouseCount, param.getHouseCount())
                .orderByDesc(RegionHouseCount::getHouseCount)
                .orderByDesc(RegionHouseCount::getId)
                .list();
    }

    @Override
    public PageResult<RegionHouseCount> page(RegionHouseCountParam param) {
        Page<RegionHouseCount> page = new Page<>(param.getPageNum(), param.getPageSize());
        return PageResult.of(baseMapper.selectPage(page, param));
    }
}
