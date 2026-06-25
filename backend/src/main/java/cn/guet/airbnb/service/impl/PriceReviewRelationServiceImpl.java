package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.entity.PriceReviewRelation;
import cn.guet.airbnb.mapper.PriceReviewRelationMapper;
import cn.guet.airbnb.param.PriceReviewRelationParam;
import cn.guet.airbnb.service.PriceReviewRelationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PriceReviewRelationServiceImpl extends ServiceImpl<PriceReviewRelationMapper, PriceReviewRelation>
        implements PriceReviewRelationService {

    @Override
    public PriceReviewRelation saveOrUpdate(PriceReviewRelationParam param) {
        if (param.getListingId() == null) {
            throw new BusinessException(400, "房源ID不能为空");
        }
        if (param.getPrice() == null) {
            throw new BusinessException(400, "价格不能为空");
        }

        PriceReviewRelation entity = new PriceReviewRelation();
        BeanUtils.copyProperties(param, entity);
        super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void removeById(Long id) {
        PriceReviewRelation existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "记录不存在");
        }
        super.removeById(id);
    }

    @Override
    public PriceReviewRelation getById(Long id) {
        PriceReviewRelation entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return entity;
    }

    @Override
    public List<PriceReviewRelation> list(PriceReviewRelationParam param) {
        return lambdaQuery()
                .and(StringUtils.hasText(param.getKeyword()), wrapper -> wrapper
                        .like(PriceReviewRelation::getNeighbourhoodCleansed, param.getKeyword())
                        .or()
                        .like(PriceReviewRelation::getRoomType, param.getKeyword()))
                .like(StringUtils.hasText(param.getNeighbourhoodCleansed()), PriceReviewRelation::getNeighbourhoodCleansed, param.getNeighbourhoodCleansed())
                .like(StringUtils.hasText(param.getRoomType()), PriceReviewRelation::getRoomType, param.getRoomType())
                .eq(param.getListingId() != null, PriceReviewRelation::getListingId, param.getListingId())
                .eq(param.getPrice() != null, PriceReviewRelation::getPrice, param.getPrice())
                .eq(param.getNumberOfReviews() != null, PriceReviewRelation::getNumberOfReviews, param.getNumberOfReviews())
                .eq(param.getReviewScoresRating() != null, PriceReviewRelation::getReviewScoresRating, param.getReviewScoresRating())
                .orderByDesc(PriceReviewRelation::getId)
                .list();
    }

    @Override
    public PageResult<PriceReviewRelation> page(PriceReviewRelationParam param) {
        Page<PriceReviewRelation> page = new Page<>(param.getPageNum(), param.getPageSize());
        return PageResult.of(baseMapper.selectPage(page, param));
    }
}
