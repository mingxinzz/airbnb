package cn.guet.airbnb.mapper;

import cn.guet.airbnb.entity.RegionAvgPrice;
import cn.guet.airbnb.param.RegionAvgPriceParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegionAvgPriceMapper extends BaseMapper<RegionAvgPrice> {

    IPage<RegionAvgPrice> selectPage(Page<RegionAvgPrice> page, @Param("param") RegionAvgPriceParam param);
}
