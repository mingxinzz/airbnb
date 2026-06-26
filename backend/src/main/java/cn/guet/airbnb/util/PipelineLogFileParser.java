package cn.guet.airbnb.util;

import cn.guet.airbnb.dto.PipelineTaskLogVO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PipelineLogFileParser {

    private static final Pattern PIPE_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|(.*)$");

    private static final Pattern BRACKET_PATTERN = Pattern.compile(
            "^\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\] \\[([^\\]]+)\\] \\[([^\\]]+)\\] \\[([^\\]]+)\\] (.*)$");

    private static final Pattern SIMPLE_LEVEL_PATTERN = Pattern.compile(
            "^\\[(INFO|ERROR|WARN|DEBUG)\\]\\s*(.*)$");

    private PipelineLogFileParser() {
    }

    public static List<PipelineTaskLogVO> parse(String content) {
        if (!StringUtils.hasText(content)) {
            return List.of();
        }

        List<PipelineTaskLogVO> logs = new ArrayList<>();
        for (String rawLine : content.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            PipelineTaskLogVO vo = parseLine(line);
            if (vo != null) {
                logs.add(vo);
            }
        }
        return logs;
    }

    private static PipelineTaskLogVO parseLine(String line) {
        Matcher pipeMatcher = PIPE_PATTERN.matcher(line);
        if (pipeMatcher.matches()) {
            return buildVo(pipeMatcher.group(1), pipeMatcher.group(2), pipeMatcher.group(3),
                    pipeMatcher.group(4), pipeMatcher.group(5));
        }

        Matcher bracketMatcher = BRACKET_PATTERN.matcher(line);
        if (bracketMatcher.matches()) {
            return buildVo(bracketMatcher.group(1), bracketMatcher.group(2), bracketMatcher.group(3),
                    bracketMatcher.group(4), bracketMatcher.group(5));
        }

        Matcher simpleMatcher = SIMPLE_LEVEL_PATTERN.matcher(line);
        if (simpleMatcher.matches()) {
            return buildVo(null, simpleMatcher.group(1), null, null, simpleMatcher.group(2));
        }

        return buildVo(null, "INFO", null, null, line);
    }

    private static PipelineTaskLogVO buildVo(String logTime, String level, String stage,
                                               String status, String message) {
        PipelineTaskLogVO vo = new PipelineTaskLogVO();
        vo.setLogTime(logTime);
        vo.setLevel(level);
        vo.setStage(stage);
        vo.setStatus(status);
        vo.setMessage(message);
        return vo;
    }
}
