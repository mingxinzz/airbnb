package cn.guet.airbnb.service;

import cn.guet.airbnb.dto.PipelineTaskLogVO;
import cn.guet.airbnb.dto.PipelineTaskVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PipelineService {

    PipelineTaskVO upload(MultipartFile listingsFile, MultipartFile calendarFile, MultipartFile reviewsFile);

    PipelineTaskVO start(String taskId);

    PipelineTaskVO getStatus(String taskId);

    List<PipelineTaskLogVO> getLogs(String taskId);

    PipelineTaskVO uploadAndStart(MultipartFile listingsFile, MultipartFile calendarFile, MultipartFile reviewsFile);
}
