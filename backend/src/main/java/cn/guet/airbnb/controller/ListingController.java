package cn.guet.airbnb.controller;

import cn.guet.airbnb.common.PageResult;
import cn.guet.airbnb.common.Result;
import cn.guet.airbnb.entity.HostHouseTopn;
import cn.guet.airbnb.entity.RegionHouseCount;
import cn.guet.airbnb.entity.RoomTypeRatio;
import cn.guet.airbnb.param.HostHouseTopnParam;
import cn.guet.airbnb.param.RegionHouseCountParam;
import cn.guet.airbnb.param.RoomTypeRatioParam;
import cn.guet.airbnb.service.HostHouseTopnService;
import cn.guet.airbnb.service.RegionHouseCountService;
import cn.guet.airbnb.service.RoomTypeRatioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listing")
@RequiredArgsConstructor
public class ListingController {

    private final RegionHouseCountService regionHouseCountService;
    private final RoomTypeRatioService roomTypeRatioService;
    private final HostHouseTopnService hostHouseTopnService;

    @PostMapping("/region-count/save")
    public Result<RegionHouseCount> saveRegionHouseCount(@RequestBody RegionHouseCountParam param) {
        return Result.success(regionHouseCountService.saveOrUpdate(param));
    }

    @DeleteMapping("/region-count/{id}")
    public Result<Void> deleteRegionHouseCount(@PathVariable Long id) {
        regionHouseCountService.removeById(id);
        return Result.success();
    }

    @GetMapping("/region-count/{id}")
    public Result<RegionHouseCount> getRegionHouseCount(@PathVariable Long id) {
        return Result.success(regionHouseCountService.getById(id));
    }

    @PostMapping("/region-count/list")
    public Result<List<RegionHouseCount>> listRegionHouseCount(@RequestBody RegionHouseCountParam param) {
        return Result.success(regionHouseCountService.list(param));
    }

    @PostMapping("/region-count/page")
    public Result<PageResult<RegionHouseCount>> pageRegionHouseCount(@RequestBody RegionHouseCountParam param) {
        return Result.success(regionHouseCountService.page(param));
    }

    @PostMapping("/room-type-ratio/save")
    public Result<RoomTypeRatio> saveRoomTypeRatio(@RequestBody RoomTypeRatioParam param) {
        return Result.success(roomTypeRatioService.saveOrUpdate(param));
    }

    @DeleteMapping("/room-type-ratio/{id}")
    public Result<Void> deleteRoomTypeRatio(@PathVariable Long id) {
        roomTypeRatioService.removeById(id);
        return Result.success();
    }

    @GetMapping("/room-type-ratio/{id}")
    public Result<RoomTypeRatio> getRoomTypeRatio(@PathVariable Long id) {
        return Result.success(roomTypeRatioService.getById(id));
    }

    @PostMapping("/room-type-ratio/list")
    public Result<List<RoomTypeRatio>> listRoomTypeRatio(@RequestBody RoomTypeRatioParam param) {
        return Result.success(roomTypeRatioService.list(param));
    }

    @PostMapping("/room-type-ratio/page")
    public Result<PageResult<RoomTypeRatio>> pageRoomTypeRatio(@RequestBody RoomTypeRatioParam param) {
        return Result.success(roomTypeRatioService.page(param));
    }

    @PostMapping("/host-topn/save")
    public Result<HostHouseTopn> saveHostHouseTopn(@RequestBody HostHouseTopnParam param) {
        return Result.success(hostHouseTopnService.saveOrUpdate(param));
    }

    @DeleteMapping("/host-topn/{id}")
    public Result<Void> deleteHostHouseTopn(@PathVariable Long id) {
        hostHouseTopnService.removeById(id);
        return Result.success();
    }

    @GetMapping("/host-topn/{id}")
    public Result<HostHouseTopn> getHostHouseTopn(@PathVariable Long id) {
        return Result.success(hostHouseTopnService.getById(id));
    }

    @PostMapping("/host-topn/list")
    public Result<List<HostHouseTopn>> listHostHouseTopn(@RequestBody HostHouseTopnParam param) {
        return Result.success(hostHouseTopnService.list(param));
    }

    @PostMapping("/host-topn/page")
    public Result<PageResult<HostHouseTopn>> pageHostHouseTopn(@RequestBody HostHouseTopnParam param) {
        return Result.success(hostHouseTopnService.page(param));
    }
}
