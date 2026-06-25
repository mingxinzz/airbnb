package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("host_house_topn")
public class HostHouseTopn {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("host_id")
    private Long hostId;

    @TableField("host_name")
    private String hostName;

    @TableField("house_count")
    private Integer houseCount;
}
