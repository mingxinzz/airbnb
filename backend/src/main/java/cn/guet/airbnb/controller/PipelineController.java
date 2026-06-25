package cn.guet.airbnb.controller;

import cn.guet.airbnb.common.Result;
import cn.guet.airbnb.dto.PipelineTaskLogVO;
import cn.guet.airbnb.dto.PipelineTaskVO;
import cn.guet.airbnb.service.PipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pipeline")
@RequiredArgsConstructor
public class PipelineController {

    private final PipelineService pipelineService;

    @PostMapping("/upload")
    public Result<PipelineTaskVO> upload(@RequestParam("listingsFile") MultipartFile listingsFile,
                                         @RequestParam("calendarFile") MultipartFile calendarFile,
                                         @RequestParam("reviewsFile") MultipartFile reviewsFile) {
        return Result.success(pipelineService.upload(listingsFile, calendarFile, reviewsFile));
    }

    @PostMapping("/{taskId}/start")
    public Result<PipelineTaskVO> start(@PathVariable String taskId) {
        return Result.success(pipelineService.start(taskId));
    }

    @GetMapping("/{taskId}/status")
    public Result<PipelineTaskVO> status(@PathVariable String taskId) {
        return Result.success(pipelineService.getStatus(taskId));
    }

    @GetMapping("/{taskId}/log")
    public Result<List<PipelineTaskLogVO>> log(@PathVariable String taskId) {
        return Result.success(pipelineService.getLogs(taskId));
    }

    @PostMapping("/upload-and-start")
    public Result<PipelineTaskVO> uploadAndStart(@RequestParam("listingsFile") MultipartFile listingsFile,
                                                 @RequestParam("calendarFile") MultipartFile calendarFile,
                                                 @RequestParam("reviewsFile") MultipartFile reviewsFile) {
        return Result.success(pipelineService.uploadAndStart(listingsFile, calendarFile, reviewsFile));
    }
}
