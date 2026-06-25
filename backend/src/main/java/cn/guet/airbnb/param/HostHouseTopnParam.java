package cn.guet.airbnb.param;

import cn.guet.airbnb.entity.HostHouseTopn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HostHouseTopnParam extends HostHouseTopn {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;
}
