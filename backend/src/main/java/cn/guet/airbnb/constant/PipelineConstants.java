package cn.guet.airbnb.constant;

public final class PipelineConstants {

    private PipelineConstants() {
    }

    public static final String LISTINGS_FILE = "listings.csv";
    public static final String CALENDAR_FILE = "calendar.csv";
    public static final String REVIEWS_FILE = "reviews.csv";

    public static final class TaskStatus {
        public static final String CREATED = "CREATED";
        public static final String RUNNING = "RUNNING";
        public static final String FINISHED = "FINISHED";
        public static final String FAILED = "FAILED";

        private TaskStatus() {
        }
    }

    public static final class TaskStage {
        public static final String CREATED = "CREATED";
        public static final String UPLOAD_RAW_TO_HDFS = "UPLOAD_RAW_TO_HDFS";
        public static final String CLEAN_DATA = "CLEAN_DATA";
        public static final String UPLOAD_CLEAN_TO_HDFS = "UPLOAD_CLEAN_TO_HDFS";
        public static final String HIVE_DWD = "HIVE_DWD";
        public static final String HIVE_ADS = "HIVE_ADS";
        public static final String SQOOP_EXPORT = "SQOOP_EXPORT";
        public static final String FINISHED = "FINISHED";
        public static final String FAILED = "FAILED";

        private TaskStage() {
        }
    }

    public static final class LogLevel {
        public static final String INFO = "INFO";
        public static final String ERROR = "ERROR";

        private LogLevel() {
        }
    }
}
