package cn.guet.airbnb.controller;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.common.Result;
import cn.guet.airbnb.entity.MonthlyReviewTrend;
import cn.guet.airbnb.param.MonthlyReviewTrendParam;
import cn.guet.airbnb.service.MonthlyReviewTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final MonthlyReviewTrendService monthlyReviewTrendService;

    @PostMapping("/month-trend/save")
    public Result<MonthlyReviewTrend> saveMonthlyReviewTrend(@RequestBody MonthlyReviewTrendParam param) {
        return Result.success(monthlyReviewTrendService.saveOrUpdate(param));
    }

    @DeleteMapping("/month-trend/{id}")
    public Result<Void> deleteMonthlyReviewTrend(@PathVariable Long id) {
        monthlyReviewTrendService.removeById(id);
        return Result.success();
    }

    @GetMapping("/month-trend/{id}")
    public Result<MonthlyReviewTrend> getMonthlyReviewTrend(@PathVariable Long id) {
        return Result.success(monthlyReviewTrendService.getById(id));
    }

    @PostMapping("/month-trend/list")
    public Result<List<MonthlyReviewTrend>> listMonthlyReviewTrend(@RequestBody MonthlyReviewTrendParam param) {
        return Result.success(monthlyReviewTrendService.list(param));
    }

    @PostMapping("/month-trend/page")
    public Result<PageResult<MonthlyReviewTrend>> pageMonthlyReviewTrend(@RequestBody MonthlyReviewTrendParam param) {
        return Result.success(monthlyReviewTrendService.page(param));
    }
}
