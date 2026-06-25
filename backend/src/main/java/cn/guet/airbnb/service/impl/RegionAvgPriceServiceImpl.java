package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.RegionAvgPrice;
import cn.guet.airbnb.mapper.RegionAvgPriceMapper;
import cn.guet.airbnb.param.RegionAvgPriceParam;
import cn.guet.airbnb.service.RegionAvgPriceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RegionAvgPriceServiceImpl extends ServiceImpl<RegionAvgPriceMapper, RegionAvgPrice>
        implements RegionAvgPriceService {

    @Override
    public RegionAvgPrice saveOrUpdate(RegionAvgPriceParam param) {
        if (!StringUtils.hasText(param.getNeighbourhoodCleansed())) {
            throw new BusinessException(400, "区域名称不能为空");
        }
        if (param.getAvgPrice() == null) {
            throw new BusinessException(400, "平均价格不能为空");
        }
        if (param.getMinPrice() == null) {
            throw new BusinessException(400, "最低价格不能为空");
        }
        if (param.getMaxPrice() == null) {
            throw new BusinessException(400, "最高价格不能为空");
        }
        if (param.getHouseCount() == null) {
            throw new BusinessException(400, "样本数量不能为空");
        }

        RegionAvgPrice entity = new RegionAvgPrice();
        BeanUtils.copyProperties(param, entity);
        super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void removeById(Long id) {
        RegionAvgPrice existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "记录不存在");
        }
        super.removeById(id);
    }

    @Override
    public RegionAvgPrice getById(Long id) {
        RegionAvgPrice entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return entity;
    }

    @Override
    public List<RegionAvgPrice> list(RegionAvgPriceParam param) {
        return lambdaQuery()
                .like(StringUtils.hasText(param.getKeyword()), RegionAvgPrice::getNeighbourhoodCleansed, param.getKeyword())
                .like(StringUtils.hasText(param.getNeighbourhoodCleansed()), RegionAvgPrice::getNeighbourhoodCleansed, param.getNeighbourhoodCleansed())
                .eq(param.getAvgPrice() != null, RegionAvgPrice::getAvgPrice, param.getAvgPrice())
                .eq(param.getMinPrice() != null, RegionAvgPrice::getMinPrice, param.getMinPrice())
                .eq(param.getMaxPrice() != null, RegionAvgPrice::getMaxPrice, param.getMaxPrice())
                .eq(param.getHouseCount() != null, RegionAvgPrice::getHouseCount, param.getHouseCount())
                .orderByDesc(RegionAvgPrice::getAvgPrice)
                .orderByDesc(RegionAvgPrice::getId)
                .list();
    }

    @Override
    public PageResult<RegionAvgPrice> page(RegionAvgPriceParam param) {
        Page<RegionAvgPrice> page = new Page<>(param.getPageNum(), param.getPageSize());
        return PageResult.of(baseMapper.selectPage(page, param));
    }
}
