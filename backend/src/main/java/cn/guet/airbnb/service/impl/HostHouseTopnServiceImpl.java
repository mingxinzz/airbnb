package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.HostHouseTopn;
import cn.guet.airbnb.mapper.HostHouseTopnMapper;
import cn.guet.airbnb.param.HostHouseTopnParam;
import cn.guet.airbnb.service.HostHouseTopnService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class HostHouseTopnServiceImpl extends ServiceImpl<HostHouseTopnMapper, HostHouseTopn>
        implements HostHouseTopnService {

    @Override
    public HostHouseTopn saveOrUpdate(HostHouseTopnParam param) {
        if (param.getHostId() == null) {
            throw new BusinessException(400, "房东ID不能为空");
        }
        if (!StringUtils.hasText(param.getHostName())) {
            throw new BusinessException(400, "房东名称不能为空");
        }
        if (param.getHouseCount() == null) {
            throw new BusinessException(400, "房源数量不能为空");
        }

        HostHouseTopn entity = new HostHouseTopn();
        BeanUtils.copyProperties(param, entity);
        super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void removeById(Long id) {
        HostHouseTopn existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "记录不存在");
        }
        super.removeById(id);
    }

    @Override
    public HostHouseTopn getById(Long id) {
        HostHouseTopn entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return entity;
    }

    @Override
    public List<HostHouseTopn> list(HostHouseTopnParam param) {
        return lambdaQuery()
                .like(StringUtils.hasText(param.getKeyword()), HostHouseTopn::getHostName, param.getKeyword())
                .like(StringUtils.hasText(param.getHostName()), HostHouseTopn::getHostName, param.getHostName())
                .eq(param.getHostId() != null, HostHouseTopn::getHostId, param.getHostId())
                .eq(param.getHouseCount() != null, HostHouseTopn::getHouseCount, param.getHouseCount())
                .orderByDesc(HostHouseTopn::getHouseCount)
                .orderByDesc(HostHouseTopn::getId)
                .list();
    }

    @Override
    public PageResult<HostHouseTopn> page(HostHouseTopnParam param) {
        Page<HostHouseTopn> page = new Page<>(param.getPageNum(), param.getPageSize());
        return PageResult.of(baseMapper.selectPage(page, param));
    }
}
