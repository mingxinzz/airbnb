package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.CalendarPriceTrend;
import cn.guet.airbnb.mapper.CalendarPriceTrendMapper;
import cn.guet.airbnb.param.CalendarPriceTrendParam;
import cn.guet.airbnb.service.CalendarPriceTrendService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CalendarPriceTrendServiceImpl extends ServiceImpl<CalendarPriceTrendMapper, CalendarPriceTrend>
        implements CalendarPriceTrendService {

    @Override
    public CalendarPriceTrend saveOrUpdate(CalendarPriceTrendParam param) {
        if (!StringUtils.hasText(param.getStatMonth())) {
            throw new BusinessException(400, "统计月份不能为空");
        }
        if (param.getAvgPrice() == null) {
            throw new BusinessException(400, "平均价格不能为空");
        }
        if (param.getSampleCount() == null) {
            throw new BusinessException(400, "样本数量不能为空");
        }

        CalendarPriceTrend entity = new CalendarPriceTrend();
        BeanUtils.copyProperties(param, entity);
        super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void removeById(Long id) {
        CalendarPriceTrend existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "记录不存在");
        }
        super.removeById(id);
    }

    @Override
    public CalendarPriceTrend getById(Long id) {
        CalendarPriceTrend entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return entity;
    }

    @Override
    public List<CalendarPriceTrend> list(CalendarPriceTrendParam param) {
        return lambdaQuery()
                .like(StringUtils.hasText(param.getKeyword()), CalendarPriceTrend::getStatMonth, param.getKeyword())
                .like(StringUtils.hasText(param.getStatMonth()), CalendarPriceTrend::getStatMonth, param.getStatMonth())
                .eq(param.getAvgPrice() != null, CalendarPriceTrend::getAvgPrice, param.getAvgPrice())
                .eq(param.getSampleCount() != null, CalendarPriceTrend::getSampleCount, param.getSampleCount())
                .orderByAsc(CalendarPriceTrend::getStatMonth)
                .orderByDesc(CalendarPriceTrend::getId)
                .list();
    }

    @Override
    public PageResult<CalendarPriceTrend> page(CalendarPriceTrendParam param) {
        Page<CalendarPriceTrend> page = new Page<>(param.getPageNum(), param.getPageSize());
        return PageResult.of(baseMapper.selectPage(page, param));
    }
}
