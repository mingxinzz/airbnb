package cn.guet.airbnb.controller;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.common.Result;
import cn.guet.airbnb.entity.CalendarPriceTrend;
import cn.guet.airbnb.entity.PriceReviewRelation;
import cn.guet.airbnb.entity.RegionAvgPrice;
import cn.guet.airbnb.param.CalendarPriceTrendParam;
import cn.guet.airbnb.param.PriceReviewRelationParam;
import cn.guet.airbnb.param.RegionAvgPriceParam;
import cn.guet.airbnb.service.CalendarPriceTrendService;
import cn.guet.airbnb.service.PriceReviewRelationService;
import cn.guet.airbnb.service.RegionAvgPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price")
@RequiredArgsConstructor
public class PriceController {

    private final RegionAvgPriceService regionAvgPriceService;
    private final PriceReviewRelationService priceReviewRelationService;
    private final CalendarPriceTrendService calendarPriceTrendService;

    @PostMapping("/region-avg/save")
    public Result<RegionAvgPrice> saveRegionAvgPrice(@RequestBody RegionAvgPriceParam param) {
        return Result.success(regionAvgPriceService.saveOrUpdate(param));
    }

    @DeleteMapping("/region-avg/{id}")
    public Result<Void> deleteRegionAvgPrice(@PathVariable Long id) {
        regionAvgPriceService.removeById(id);
        return Result.success();
    }

    @GetMapping("/region-avg/{id}")
    public Result<RegionAvgPrice> getRegionAvgPrice(@PathVariable Long id) {
        return Result.success(regionAvgPriceService.getById(id));
    }

    @PostMapping("/region-avg/list")
    public Result<List<RegionAvgPrice>> listRegionAvgPrice(@RequestBody RegionAvgPriceParam param) {
        return Result.success(regionAvgPriceService.list(param));
    }

    @PostMapping("/region-avg/page")
    public Result<PageResult<RegionAvgPrice>> pageRegionAvgPrice(@RequestBody RegionAvgPriceParam param) {
        return Result.success(regionAvgPriceService.page(param));
    }

    @PostMapping("/review-scatter/save")
    public Result<PriceReviewRelation> savePriceReviewRelation(@RequestBody PriceReviewRelationParam param) {
        return Result.success(priceReviewRelationService.saveOrUpdate(param));
    }

    @DeleteMapping("/review-scatter/{id}")
    public Result<Void> deletePriceReviewRelation(@PathVariable Long id) {
        priceReviewRelationService.removeById(id);
        return Result.success();
    }

    @GetMapping("/review-scatter/{id}")
    public Result<PriceReviewRelation> getPriceReviewRelation(@PathVariable Long id) {
        return Result.success(priceReviewRelationService.getById(id));
    }

    @PostMapping("/review-scatter/list")
    public Result<List<PriceReviewRelation>> listPriceReviewRelation(@RequestBody PriceReviewRelationParam param) {
        return Result.success(priceReviewRelationService.list(param));
    }

    @PostMapping("/review-scatter/page")
    public Result<PageResult<PriceReviewRelation>> pagePriceReviewRelation(@RequestBody PriceReviewRelationParam param) {
        return Result.success(priceReviewRelationService.page(param));
    }

    @PostMapping("/trend/save")
    public Result<CalendarPriceTrend> saveCalendarPriceTrend(@RequestBody CalendarPriceTrendParam param) {
        return Result.success(calendarPriceTrendService.saveOrUpdate(param));
    }

    @DeleteMapping("/trend/{id}")
    public Result<Void> deleteCalendarPriceTrend(@PathVariable Long id) {
        calendarPriceTrendService.removeById(id);
        return Result.success();
    }

    @GetMapping("/trend/{id}")
    public Result<CalendarPriceTrend> getCalendarPriceTrend(@PathVariable Long id) {
        return Result.success(calendarPriceTrendService.getById(id));
    }

    @PostMapping("/trend/list")
    public Result<List<CalendarPriceTrend>> listCalendarPriceTrend(@RequestBody CalendarPriceTrendParam param) {
        return Result.success(calendarPriceTrendService.list(param));
    }

    @PostMapping("/trend/page")
    public Result<PageResult<CalendarPriceTrend>> pageCalendarPriceTrend(@RequestBody CalendarPriceTrendParam param) {
        return Result.success(calendarPriceTrendService.page(param));
    }
}
