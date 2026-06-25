package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("review_word_freq")
public class ReviewWordFreq {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("word")
    private String word;

    @TableField("freq")
    private Integer freq;
}
