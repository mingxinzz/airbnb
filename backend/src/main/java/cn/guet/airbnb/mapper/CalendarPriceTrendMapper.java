package cn.guet.airbnb.mapper;

import cn.guet.airbnb.entity.CalendarPriceTrend;
import cn.guet.airbnb.param.CalendarPriceTrendParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CalendarPriceTrendMapper extends BaseMapper<CalendarPriceTrend> {

    IPage<CalendarPriceTrend> selectPage(Page<CalendarPriceTrend> page, @Param("param") CalendarPriceTrendParam param);
}
