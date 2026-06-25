package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.MonthlyReviewTrend;
import cn.guet.airbnb.mapper.MonthlyReviewTrendMapper;
import cn.guet.airbnb.param.MonthlyReviewTrendParam;
import cn.guet.airbnb.service.MonthlyReviewTrendService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MonthlyReviewTrendServiceImpl extends ServiceImpl<MonthlyReviewTrendMapper, MonthlyReviewTrend>
        implements MonthlyReviewTrendService {

    @Override
    public MonthlyReviewTrend saveOrUpdate(MonthlyReviewTrendParam param) {
        if (!StringUtils.hasText(param.getReviewMonth())) {
            throw new BusinessException(400, "评论月份不能为空");
        }
        if (param.getReviewCount() == null) {
            throw new BusinessException(400, "评论数量不能为空");
        }

        MonthlyReviewTrend entity = new MonthlyReviewTrend();
        BeanUtils.copyProperties(param, entity);
        super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void removeById(Long id) {
        MonthlyReviewTrend existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "记录不存在");
        }
        super.removeById(id);
    }

    @Override
    public MonthlyReviewTrend getById(Long id) {
        MonthlyReviewTrend entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return entity;
    }

    @Override
    public List<MonthlyReviewTrend> list(MonthlyReviewTrendParam param) {
        return lambdaQuery()
                .like(StringUtils.hasText(param.getKeyword()), MonthlyReviewTrend::getReviewMonth, param.getKeyword())
                .like(StringUtils.hasText(param.getReviewMonth()), MonthlyReviewTrend::getReviewMonth, param.getReviewMonth())
                .eq(param.getReviewCount() != null, MonthlyReviewTrend::getReviewCount, param.getReviewCount())
                .orderByAsc(MonthlyReviewTrend::getReviewMonth)
                .orderByDesc(MonthlyReviewTrend::getId)
                .list();
    }

    @Override
    public PageResult<MonthlyReviewTrend> page(MonthlyReviewTrendParam param) {
        Page<MonthlyReviewTrend> page = new Page<>(param.getPageNum(), param.getPageSize());
        return PageResult.of(baseMapper.selectPage(page, param));
    }
}
