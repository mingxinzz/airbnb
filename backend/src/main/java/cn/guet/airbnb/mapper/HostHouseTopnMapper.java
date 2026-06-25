package cn.guet.airbnb.mapper;

import cn.guet.airbnb.entity.HostHouseTopn;
import cn.guet.airbnb.param.HostHouseTopnParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HostHouseTopnMapper extends BaseMapper<HostHouseTopn> {

    IPage<HostHouseTopn> selectPage(Page<HostHouseTopn> page, @Param("param") HostHouseTopnParam param);
}
