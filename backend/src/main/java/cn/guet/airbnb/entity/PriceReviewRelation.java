package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("price_review_relation")
public class PriceReviewRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("listing_id")
    private Long listingId;

    @TableField("neighbourhood_cleansed")
    private String neighbourhoodCleansed;

    @TableField("room_type")
    private String roomType;

    @TableField("price")
    private BigDecimal price;

    @TableField("number_of_reviews")
    private Integer numberOfReviews;

    @TableField("review_scores_rating")
    private Double reviewScoresRating;
}
