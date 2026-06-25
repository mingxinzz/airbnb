package cn.guet.airbnb.mapper;

import cn.guet.airbnb.entity.PriceReviewRelation;
import cn.guet.airbnb.param.PriceReviewRelationParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PriceReviewRelationMapper extends BaseMapper<PriceReviewRelation> {

    IPage<PriceReviewRelation> selectPage(Page<PriceReviewRelation> page, @Param("param") PriceReviewRelationParam param);
}
