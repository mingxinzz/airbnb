package cn.guet.airbnb.mapper;

import cn.guet.airbnb.entity.MonthlyReviewTrend;
import cn.guet.airbnb.param.MonthlyReviewTrendParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MonthlyReviewTrendMapper extends BaseMapper<MonthlyReviewTrend> {

    IPage<MonthlyReviewTrend> selectPage(Page<MonthlyReviewTrend> page, @Param("param") MonthlyReviewTrendParam param);
}
