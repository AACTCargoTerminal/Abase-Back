package com.aact.infraservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ExcelDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchMasterDTO {

        private List<SchDTO> list;
        private List<SchHeaderDTO> headers;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchDTO {

        private String yyyy;
        private String mon;
        private String teamCode;
        private String teamName;
        private String terminalCode;
        private byte[] createUser;
        private byte[] approveUser;
        private String holiDay;

        private List<SchRowDTO> rows;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchHeaderDTO {

        private String day;
        private String dayName;
        private String mon;
        private String holidayCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchRowDTO {

        private String userSid;
        private String userName;
        private String userId;
        private String useHoliDay;
        private String useAnn;

        private List<SchCellDTO> cells;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchCellDTO {

        private String day;
        private String workTypeCode;
        private String addWorkHour;
        private String seq;
        private String tmpTerminalCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyDTO {

        private String date;
        private String terminalCode;
        private String teamName;
        private String teamCode;
        private byte[] approveSign;
        private String approveName;
        private double sumAddHour;
        private double sumNightHour;
        private double sumHoliHour;
        private double sumHoliAddHour;
        private List<DailyRowDTO> rowList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyRowDTO {

        private String date;
        private BigDecimal seq;
        private String userName;
        private String terminalCode;
        private String teamCode;
        private String workTypeName;
        private String reqStartTime;
        private String reqEndTime;
        private double addHour;
        private double nightHour;
        private double holiHour;
        private double holiAddHour;
        private String remark;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PeriodDTO {

        private String toDate;
        private String fromDate;
        private String terminalCode;
        private String teamCode;
        private byte[] approveSign;
        private String approveName;
        private double sumAddHour;
        private double sumNightHour;
        private double sumHoliHour;
        private List<DailyRowDTO> rowList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailDTO {

        private String yyyy;
        private String mon;
        private String teamCode;
        private String teamName;
        private String terminalCode;
        private double sumAddHour;
        private double sumNightHour;
        private double sumHoliHour;
        private double sumHoliAddHour;
        private List<DetailRowDTO> rowList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailRowDTO {

        private String userSid;
        private String userName;
        private double sumAddHour;
        private double sumNightHour;
        private double sumHoliHour;
        private double sumHoliAddHour;
        private List<DetailCellDTO> cellList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailCellDTO {

        private String seq;
        private String day;
        private String workTypeName;
        private String schStart;
        private String schEnd;
        private String capsStart;
        private String capsEnd;
        private double addHour;
        private double nightHour;
        private double holiHour;
        private double holiAddHour;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoverDTO {

        private String groupCode;
        private String groupName;
        private String[] groupLine;
        private List<CoverRowDTO> rowList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoverRowDTO {

        private String deptCode;
        private String deptName;
        private double beforeAddHour;
        private double beforeNightHour;
        private double beforeHoliHour;
        private double nowAddHour;
        private double nowNightHour;
        private double nowHoliHour;

    }
}
