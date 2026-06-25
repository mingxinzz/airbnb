package cn.guet.airbnb.mapper;

import cn.guet.airbnb.entity.RoomTypeRatio;
import cn.guet.airbnb.param.RoomTypeRatioParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomTypeRatioMapper extends BaseMapper<RoomTypeRatio> {

    IPage<RoomTypeRatio> selectPage(Page<RoomTypeRatio> page, @Param("param") RoomTypeRatioParam param);
}
