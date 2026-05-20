package com.aact.infraservice.service;

import com.aact.common.BizException;
import com.aact.common.ResponseDTO;
import com.aact.common.ServiceBase;
import com.aact.infraservice.dto.CapsEnterDTO;
import com.aact.infraservice.dto.CapsTimeDTO;
import com.aact.infraservice.dto.CapsUserDTO;
import com.aact.infraservice.repo.CapsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CapsService extends ServiceBase {
    private final CapsMapper capsMapper;

    public ResponseDTO<?> findUsersWithGroup(String start, String end, String name, String id, String[] modes) {

        List<CapsUserDTO> dbRet = capsMapper.findUsersWithGroup(start, end, name, id, modes);

        ResponseDTO<List<CapsUserDTO>> ret = ResponseDTO.<List<CapsUserDTO>>builder().errFlag("N").errMsg("조회완료")
                .data(dbRet).build();

        return okOrThrow("findUsersWithGroup", ret);

    }

    // findEnterToUser

    public ResponseDTO<?> findEnterToUser(String id, String[] modes, String start, String end) {

        List<CapsEnterDTO> dbRet = capsMapper.findEnterToUser(id, modes, start, end);

        ResponseDTO<List<CapsEnterDTO>> ret = ResponseDTO.<List<CapsEnterDTO>>builder().errFlag("N").errMsg("조회완료")
                .data(dbRet).build();

        return okOrThrow("findEnterToUser", ret);

    }

    public ResponseDTO<CapsTimeDTO.CapsRangeResult> findDateToId(String id, String date, String start, String end, int plusDay) {
        String capsStartDate = "";
        String capsEndDate = "";
        String capsStartTime = "";
        String capsEndTime = "";
        String capsOrgStartTime = "";
        String capsOrgEndTime = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.parse(date, formatter);
        List<CapsEnterDTO> dbRet = capsMapper.findDateToId(id, date);
        List<CapsEnterDTO> tmpEnter = extractBy1HourGap(dbRet);
        if (tmpEnter.size() > 3) {
            throw new BizException("findDateToId", "캡스데이터가 너무 많습니다. ( " + tmpEnter.size() + " )");
        }


        if (!start.isEmpty() && !end.isEmpty()) {
            if (start.length() == 2) {
                start += "00";
            }
            if (end.length() == 2) {
                end += "00";
            }
            boolean flag = false;
            ClosestResult tmpRet = null;
            if (tmpEnter.size() == 3) {
                tmpRet = findClosest(tmpEnter.get(1), date, start);
                if (tmpRet != null) {
                    if (tmpRet.data.getE_mode().equals("1") || tmpRet.data.getE_mode().equals("3")) {
                        if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30) {
                            capsStartDate = date;
                            capsStartTime = start;
                            flag = true;
                        } else {
                            capsStartDate = date;
                            capsStartTime = roundTime(tmpRet.data.getE_time());
                        }
                        capsOrgStartTime = tmpRet.data.getE_time_raw();
                    }

                }
                tmpRet = findClosest(tmpEnter.get(2), now.plusDays(plusDay).format(formatter), end);
                if (tmpRet != null) {
                    if (tmpRet.data.getE_mode().equals("2") || tmpRet.data.getE_mode().equals("3")) {
                        if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30 && flag) {
                            throw new BizException("getWorkCapsData", "OT 해당사항 없음.");
                        } else if (tmpRet.diffMinutes > 30) {
                            capsEndDate = date;
                            capsEndTime = floorTo30(tmpRet.data.getE_time());
                            capsOrgEndTime = tmpRet.data.getE_time_raw();
                        }
                    }
                }
            } else if (tmpEnter.size() == 2) {
                if (tmpEnter.get(0).getE_mode().equals("1")) {
                    tmpRet = findClosest(tmpEnter.get(0), date, start);
                    if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30) {
                        capsStartDate = date;
                        capsStartTime = start;
                        flag = true;
                    } else {
                        capsStartDate = date;
                        capsStartTime = roundTime(tmpRet.data.getE_time());
                    }
                    capsOrgStartTime = tmpRet.data.getE_time_raw();
                    tmpRet = findClosest(tmpEnter.get(1), now.plusDays(plusDay).format(formatter), end);
                    if (tmpRet != null) {
                        if (tmpRet.data.getE_mode().equals("2") || tmpRet.data.getE_mode().equals("3")) {
                            if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30 && flag) {
                                throw new BizException("getWorkCapsData", "OT 해당사항 없음.");
                            } else if (tmpRet.diffMinutes > 30) {
                                capsEndDate = date;
                                capsEndTime = floorTo30(tmpRet.data.getE_time());
                                capsOrgEndTime = tmpRet.data.getE_time_raw();
                            }
                        }
                    }
                } else if (tmpEnter.get(0).getE_mode().equals("2")) {
                    tmpRet = findClosest(tmpEnter.get(1), date, start);
                    if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30) {
                        capsStartDate = date;
                        capsStartTime = start;
                        flag = true;
                    } else {
                        capsStartDate = date;
                        capsStartTime = roundTime(tmpRet.data.getE_time());
                    }
                    capsOrgStartTime = tmpRet.data.getE_time_raw();
                    LocalDate next = LocalDate.parse(date, formatter).plusDays(1);
                    List<CapsEnterDTO> tmpCaps = extractBy1HourGap(capsMapper.findDateToId(id, next.format(formatter)));
                    if (!tmpCaps.isEmpty()) {
                        tmpRet = findClosest(tmpCaps.get(0), now.plusDays(plusDay).format(formatter), end);
                        if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30 && flag) {
                            throw new BizException("getWorkCapsData", "OT 해당사항 없음.");
                        } else if (tmpRet.diffMinutes > 30) {
                            capsEndDate = next.format(formatter);
                            capsEndTime = floorTo30(tmpRet.data.getE_time());
                            capsOrgEndTime = tmpRet.data.getE_time_raw();
                        }
                    }
                } else {
                    tmpRet = findClosest(tmpEnter.get(0), date, start);
                    int diff1 = tmpRet.diffMinutes;
                    tmpRet = findClosest(tmpEnter.get(1), date, start);
                    int diff2 = tmpRet.diffMinutes;
                    if (diff1 < 30 && diff2 < 30) { // 두번째 출근으로 봄
                        tmpRet = findClosest(tmpEnter.get(1), date, start);
                        if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30) {
                            capsStartDate = date;
                            capsStartTime = start;
                            flag = true;
                        } else {
                            capsStartDate = date;
                            capsStartTime = roundTime(tmpRet.data.getE_time());
                        }
                        capsOrgStartTime = tmpRet.data.getE_time_raw();
                        LocalDate next = LocalDate.parse(date, formatter).plusDays(1);
                        List<CapsEnterDTO> tmpCaps = extractBy1HourGap(capsMapper.findDateToId(id, next.format(formatter)));
                        if (!tmpCaps.isEmpty()) {
                            tmpRet = findClosest(tmpCaps.get(0), now.plusDays(plusDay).format(formatter), end);
                            if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30 && flag) {
                                throw new BizException("getWorkCapsData", "OT 해당사항 없음.");
                            } else if (tmpRet.diffMinutes > 30) {
                                capsEndDate = next.format(formatter);
                                capsEndTime = floorTo30(tmpRet.data.getE_time());
                                capsOrgEndTime = tmpRet.data.getE_time_raw();
                            }
                        }
                    } else {
                        tmpRet = findClosest(tmpEnter.get(0), date, start);
                        if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30) {
                            capsStartDate = date;
                            capsStartTime = start;
                            flag = true;
                        } else if (tmpRet.diffMinutes <= -30) {
                            capsStartDate = date;
                            capsStartTime = roundTime(tmpRet.data.getE_time());
                        }
                        capsOrgStartTime = tmpRet.data.getE_time_raw();
                        tmpRet = findClosest(tmpEnter.get(1), now.plusDays(plusDay).format(formatter), end);
                        if (tmpRet != null) {
                            if (tmpRet.data.getE_mode().equals("2") || tmpRet.data.getE_mode().equals("3")) {
                                if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30 && flag) {
                                    throw new BizException("getWorkCapsData", "OT 해당사항 없음.");
                                } else if (tmpRet.diffMinutes > 30) {
                                    capsEndDate = date;
                                    capsEndTime = floorTo30(tmpRet.data.getE_time());
                                    capsOrgEndTime = tmpRet.data.getE_time_raw();
                                }
                            }
                        }
                    }
                }
            } else if (tmpEnter.size() == 1) {
                if (!tmpEnter.get(0).getE_mode().equals("2")) {
                    tmpRet = findClosest(tmpEnter.get(0), date, start);
                    if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30) {
                        capsStartDate = date;
                        capsStartTime = start;
                        flag = true;
                    } else {
                        capsStartDate = date;
                        capsStartTime = roundTime(tmpRet.data.getE_time());
                    }
                    capsOrgStartTime = tmpRet.data.getE_time_raw();
                }
                LocalDate next = LocalDate.parse(date, formatter).plusDays(1);
                List<CapsEnterDTO> tmpCaps = extractBy1HourGap(capsMapper.findDateToId(id, next.format(formatter)));
                if (!tmpCaps.isEmpty()) {
                    tmpRet = findClosest(tmpCaps.get(0), now.plusDays(plusDay).format(formatter), end);
                    if (tmpRet.diffMinutes > -30 && tmpRet.diffMinutes < 30 && flag) {
                        throw new BizException("getWorkCapsData", "OT 해당사항 없음.");
                    } else if (tmpRet.diffMinutes > 30) {
                        capsEndDate = next.format(formatter);
                        capsEndTime = floorTo30(tmpRet.data.getE_time());
                        capsOrgEndTime = tmpRet.data.getE_time_raw();
                    }
                }
            }


        } else {

            if (tmpEnter.size() == 3) {
                capsStartDate = tmpEnter.get(1).getE_date();
                capsStartTime = roundTime(tmpEnter.get(1).getE_time());
                capsOrgStartTime = tmpEnter.get(1).getE_time().substring(0,4);
                capsEndDate = tmpEnter.get(2).getE_date();
                capsEndTime = floorTo30(tmpEnter.get(2).getE_time());
                capsOrgEndTime = tmpEnter.get(2).getE_time().substring(0,4);

            } else if (tmpEnter.size() == 2) {

                if (tmpEnter.get(0).getE_mode().equals("1")) {
                    capsStartDate = tmpEnter.get(0).getE_date();
                    capsStartTime = roundTime(tmpEnter.get(0).getE_time());
                    capsOrgStartTime = tmpEnter.get(0).getE_time().substring(0,4);
                    capsEndDate = tmpEnter.get(1).getE_date();
                    capsEndTime = floorTo30(tmpEnter.get(1).getE_time());
                    capsOrgEndTime = tmpEnter.get(1).getE_time().substring(0,4);
                } else if (tmpEnter.get(0).getE_mode().equals("2")) {
                    capsStartDate = tmpEnter.get(1).getE_date();
                    capsStartTime = roundTime(tmpEnter.get(1).getE_time());
                    capsOrgStartTime = tmpEnter.get(1).getE_time().substring(0,4);
                    LocalDate next = LocalDate.parse(date, formatter).plusDays(1);
                    List<CapsEnterDTO> tmpCaps = extractBy1HourGap(capsMapper.findDateToId(id, next.format(formatter)));
                    if (!tmpCaps.isEmpty()) {
                        capsEndDate = tmpCaps.get(0).getE_date();
                        capsEndTime = floorTo30(tmpCaps.get(0).getE_time());
                        capsOrgEndTime = tmpCaps.get(0).getE_time().substring(0,4);
                    }
                } else {
                    if (tmpEnter.get(1).getE_mode().equals("1")) {
                        capsStartDate = tmpEnter.get(1).getE_date();
                        capsStartTime = roundTime(tmpEnter.get(1).getE_time());
                        capsOrgStartTime = tmpEnter.get(1).getE_time().substring(0,4);
                        LocalDate next = LocalDate.parse(date, formatter).plusDays(1);
                        List<CapsEnterDTO> tmpCaps = extractBy1HourGap(capsMapper.findDateToId(id, next.format(formatter)));
                        if (!tmpCaps.isEmpty()) {
                            capsEndDate = tmpCaps.get(0).getE_date();
                            capsEndTime = floorTo30(tmpCaps.get(0).getE_time());
                            capsOrgEndTime = tmpCaps.get(0).getE_time().substring(0,4);
                        }
                    } else if (tmpEnter.get(1).getE_mode().equals("2")) {
                        capsStartDate = tmpEnter.get(0).getE_date();
                        capsStartTime = roundTime(tmpEnter.get(0).getE_time());
                        capsOrgStartTime = tmpEnter.get(0).getE_time().substring(0,4);
                        capsEndDate = tmpEnter.get(1).getE_date();
                        capsEndTime = floorTo30(tmpEnter.get(1).getE_time());
                        capsOrgEndTime = tmpEnter.get(1).getE_time().substring(0,4);
                    }
                }

            } else if (tmpEnter.size() == 1) {
                capsStartDate = tmpEnter.get(0).getE_date();
                capsStartTime = roundTime(tmpEnter.get(0).getE_time());
                capsOrgStartTime = tmpEnter.get(0).getE_time().substring(0,4);
                LocalDate next = LocalDate.parse(date, formatter).plusDays(1);
                List<CapsEnterDTO> tmpCaps = extractBy1HourGap(capsMapper.findDateToId(id, next.format(formatter)));
                if (!tmpCaps.isEmpty()) {
                    capsEndDate = tmpCaps.get(0).getE_date();
                    capsEndTime = floorTo30(tmpCaps.get(0).getE_time());
                    capsOrgEndTime = tmpEnter.get(0).getE_time().substring(0,4);
                }
            }


        }

        ResponseDTO<CapsTimeDTO.CapsRangeResult> ret = ResponseDTO.<CapsTimeDTO.CapsRangeResult>builder().errMsg("").errFlag("N").data(new CapsTimeDTO.CapsRangeResult(capsStartDate, capsStartTime, capsEndDate, capsEndTime, capsOrgStartTime, capsOrgEndTime)).build();

        return okOrThrow("findDateToId", ret);

    }

    List<CapsEnterDTO> extractBy1HourGap(List<CapsEnterDTO> list) {

        if (list == null || list.isEmpty()) return Collections.emptyList();

        // 시간순 정렬 (이미 정렬돼있으면 생략 가능)
        list.sort(Comparator.comparing(v -> v.getE_time()));

        List<CapsEnterDTO> result = new ArrayList<>();

        CapsEnterDTO prev = null;

        for (CapsEnterDTO cur : list) {

            if (prev == null) {
                result.add(cur); // 첫 값은 무조건 추가
                prev = cur;
                continue;
            }

            int prevMin = toMinutes(prev.getE_time());
            int curMin = toMinutes(cur.getE_time());

            if (curMin - prevMin >= 60) {
                result.add(cur); // 1시간 이상 차이 → 새 그룹 시작
            }

            prev = cur;
        }

        return result;
    }

    private int toMinutes(String time) {
        String t = time.substring(0, 4); // HHmm
        int h = Integer.parseInt(t.substring(0, 2));
        int m = Integer.parseInt(t.substring(2, 4));
        return h * 60 + m;
    }

    ClosestResult findClosest(CapsEnterDTO dto, String targetDate, String targetTime) {
        if (dto == null || dto.getE_date() == null || dto.getE_time() == null || dto.getE_time().length() < 4) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        LocalDateTime target = LocalDateTime.parse(targetDate + targetTime, formatter);
        String originalTime = dto.getE_time().substring(0, 4);
        String hhmm = dto.getE_time().substring(0, 4);
        LocalDateTime time = LocalDateTime.parse(dto.getE_date() + hhmm, formatter);

        long diff = Duration.between(target, time).toMinutes();

        CapsEnterDTO newDto = new CapsEnterDTO();
        newDto.setE_date(dto.getE_date());
        newDto.setE_time(hhmm); // 초 제거
        newDto.setE_time_raw(originalTime);
        newDto.setE_mode(dto.getE_mode());
        newDto.setG_id(dto.getG_id());

        return new ClosestResult(newDto, (int) diff);
    }

    String roundTime(String hhmmss) {
        if (hhmmss == null || hhmmss.length() < 4) return null;

        int hour = Integer.parseInt(hhmmss.substring(0, 2));
        int minute = Integer.parseInt(hhmmss.substring(2, 4));
        int second = hhmmss.length() >= 6 ? Integer.parseInt(hhmmss.substring(4, 6)) : 0;

        // 초까지 포함해서 분 반올림
        if (second >= 30) {
            minute += 1;
        }

        // 30분 단위 반올림
        if (minute <= 30) {
            minute = 30;
        } else {
            minute = 0;
            hour += 1;
        }

        // 24시 넘어가면 00시
        if (hour == 24) {
            hour = 0;
        }

        return String.format("%02d%02d", hour, minute);
    }

    String floorTo30(String hhmmss) {
        if (hhmmss == null || hhmmss.length() < 4) return null;

        int hour = Integer.parseInt(hhmmss.substring(0, 2));
        int minute = Integer.parseInt(hhmmss.substring(2, 4));
        int second = hhmmss.length() >= 6 ? Integer.parseInt(hhmmss.substring(4, 6)) : 0;

        // 초 반영 (30초 이상이면 분 +1)
        if (second >= 30) {
            minute += 1;
        }

        // 60분 처리
        if (minute == 60) {
            minute = 0;
            hour += 1;
        }

        // 🔥 핵심 조건
        if (minute >= 55 || minute < 25) {
            minute = 0;
        } else {
            minute = 30;
        }

        // 24시 처리
        if (hour == 24) {
            hour = 0;
        }

        return String.format("%02d%02d", hour, minute);
    }

    record ClosestResult(CapsEnterDTO data, int diffMinutes) {
    }
}
