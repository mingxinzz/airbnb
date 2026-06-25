package cn.guet.airbnb.mapper;

import cn.guet.airbnb.entity.RegionHouseCount;
import cn.guet.airbnb.param.RegionHouseCountParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegionHouseCountMapper extends BaseMapper<RegionHouseCount> {

    IPage<RegionHouseCount> selectPage(Page<RegionHouseCount> page, @Param("param") RegionHouseCountParam param);
}
