package com.aact.authservice.dto;

import com.aact.common.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InfraUser {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SaveUserDTO(
            @JsonProperty("USER_SID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "*",label = "사용자") BigDecimal userSid,
            @JsonProperty("USER_ID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "*",label = "ID") String userId,
            @JsonProperty("USER_ID_CHANGE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "") String userIdChange,
            @JsonProperty("USER_PASSWORD") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "*",label = "암호") String userPass,
            @JsonProperty("USER_PASSWORD_HP") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String userPassHp,
            @JsonProperty("USER_NAME1") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String userName1,
            @JsonProperty("USER_NAME2") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String userName2,
            @JsonProperty("TEAM_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "*",label = "부서") String teamCode,
            @JsonProperty("TEAM_DATE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "*",label = "부서적용일") String teamDate,
            @JsonProperty("JOIN_DAY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "*",label = "입사일") String joinDay,
            @JsonProperty("GROUP_JOIN_DAY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String groupJoinDay,
            @JsonProperty("TERMINAL_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs(value = "*",label = "터미널") String terminalCode,
            @JsonProperty("TERMINAL_NAME") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String terminalName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserRelDTO(
            @JsonProperty("USER_SID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid,
            @JsonProperty("CLASS_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String classCode,
            @JsonProperty("CODE_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String codeCode,
            @JsonProperty("YYYY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0000") String yyyy,
            @JsonProperty("VALUE1") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value1,
            @JsonProperty("VALUE2") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value2,
            @JsonProperty("VALUE3") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value3,
            @JsonProperty("VALUE4") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value4) {
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserGroupDTO {

        @JsonProperty("teamGroup")
        private List<TeamGroupDTO> teamGroup;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamGroupDTO {

        @JsonProperty("teamName")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "")
        private String teamName;

        @JsonProperty("teamCode")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "팀 코드")
        private String teamCode;

        @JsonProperty("userArray")
        private List<TeamUserGroupDTO> userArray;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamUserGroupDTO {

        @JsonProperty("terminalCode")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "터미널 코드")
        private String teminalCode;

        @JsonProperty("userId")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "사번")
        private String userId;

        @JsonProperty("pass")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "패스워드")
        private String pass;

        @JsonProperty("groupJoinDate")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "")
        private String groupJoinDate;

        @JsonProperty("joinDate")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "입사일")
        private String joinDate;

        @JsonProperty("userName")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "")
        private String userName;

        @JsonProperty("workType")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "N")
        private String workType;

        @JsonProperty("workType2")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "N")
        private String workType2;

        @JsonProperty("positionList")
        private List<TeamUserPositionDTO> positionList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamUserPositionDTO {
        @JsonProperty("terminalCode")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "터미널 코드")
        private String teminalCode;

        @JsonProperty("teamCode")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "팀 코드")
        private String teamCode;

        @JsonProperty("teamName")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "")
        private String teamName;

        @JsonProperty("position")
        @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
        @EmptyAsSupport.EmptyAs(value = "*",label = "직책")
        private String position;
    }

    public static ResponseDTO<List<TeamGroupDTO>> changeGroup(DbDto dto,List<Map<String, Object>> hrpat,List<Map<String,Object>> postn){
        ResponseDTO<List<TeamGroupDTO>> ret = null;
        try{
            ret = ResponseDTO.<List<TeamGroupDTO>>builder().errFlag("N").errMsg("완료").data(new ArrayList<>()).build();

            List<Map<String, DbTypeDTO>> dt = dto.getResult().get(0);
            List<Map<String, DbTypeDTO>> dt2 = dto.getResult().get(1);

            if(dt.isEmpty()){
                throw new Exception("HR 설정된 근무자가 없습니다.");
            }

            Map<BigDecimal, Map<String, DbTypeDTO>> userMap =
                    dt.stream()
                            .collect(Collectors.toMap(
                                    v -> Util.getDecimal(v.get("USER_SID").getObj()),
                                    v -> v,
                                    (a, b) -> a
                            ));

            List<Map<String,DbTypeDTO>> hrpatDt2 = dt2.stream().filter(v->v.get("CLASS_CODE").getObj().equals("HRPAT")).toList();

            for(Map<String,DbTypeDTO> row : hrpatDt2){
                BigDecimal userSid =
                        Util.getDecimal(row.get("USER_SID").getObj());

                Map<String, DbTypeDTO> tmpDt = userMap.get(userSid);
                if(tmpDt==null){
                    continue;
                }
                String userId = Util.getStrChk(tmpDt.get("USER_ID").getObj());
                String userPassword = Util.getStrChk(tmpDt.get("USER_PASSWORD").getObj());

                Map<String,Object> hrpatSelect = hrpat.stream().filter(v->v.get("CODE_CODE").equals(row.get("CODE_CODE").getObj())).findFirst().orElse(null);

                if(hrpatSelect==null){
                    throw new Exception(row.get("CODE_CODE").getObj()+"없는 팀 코드 입니다.");
                }
                String teamCode = Util.getStrChk(hrpatSelect.get("CODE_CODE"));
                String teamName = Util.getStrChk(hrpatSelect.get("CODE_NAME"));

                TeamGroupDTO tmpTeamGroup = ret.getData().stream().filter(v->v.getTeamCode().equals(teamCode)).findFirst().orElse(null);

                if(tmpTeamGroup == null){
                    tmpTeamGroup = new TeamGroupDTO();
                    tmpTeamGroup.setTeamCode(teamCode);
                    tmpTeamGroup.setTeamName(teamName);
                    tmpTeamGroup.setUserArray(new ArrayList<>());
                    ret.getData().add(tmpTeamGroup);
                }

                TeamUserGroupDTO tmpUserGroup = tmpTeamGroup.userArray.stream().filter(v->v.getUserId().equals(userId)).findFirst().orElse(null);

                if(tmpUserGroup == null){
                    tmpUserGroup = new TeamUserGroupDTO();
                    tmpUserGroup.setUserId(userId);
                    tmpUserGroup.setPass(userPassword);
                    tmpUserGroup.setUserName(Util.getStrChk(tmpDt.get("USER_NAME").getObj()));

                    Map<String,DbTypeDTO> trmcdDt2 = dt2.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("TRMCD")&&v.get("USER_SID").getObj().equals(userSid)).findFirst().orElse(null);
                    if(trmcdDt2==null){
                        throw new Exception(userId+"는 근무지가 없습니다.");
                    }
                    tmpUserGroup.setTeminalCode(Util.getStrChk(trmcdDt2.get("CODE_CODE").getObj()));

                    Map<String,DbTypeDTO> groupJoin = dt2.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRWDT")
                                    &&v.get("USER_SID").getObj().equals(userSid)
                                    &&v.get("CODE_CODE").getObj().equals("C"))
                            .findFirst().orElse(null);
                    if(groupJoin != null){
                        String date = Util.getStrChk(groupJoin.get("VALUE1").getObj());
                        if(date.length()==8){
                            tmpUserGroup.setGroupJoinDate(date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
                        }

                    }

                    Map<String,DbTypeDTO> join = dt2.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRWDT")
                                    &&v.get("USER_SID").getObj().equals(userSid)
                                    &&v.get("CODE_CODE").getObj().equals("A"))
                            .findFirst().orElse(null);
                    if(join == null){
                        throw new Exception(userId+"는 입사일이 없습니다.");
                    }
                    String date = Util.getStrChk(join.get("VALUE1").getObj());
                    if(date.length() == 8){
                        tmpUserGroup.setJoinDate(date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
                    }


                    Map<String,DbTypeDTO> workType = dt2.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRWKT")
                                    &&v.get("USER_SID").getObj().equals(userSid)
                                    &&v.get("CODE_CODE").getObj().equals("A"))
                            .findFirst().orElse(null);
                    if(workType != null){
                        tmpUserGroup.setWorkType("Y");
                    }else{
                        tmpUserGroup.setWorkType("N");
                    }

                    Map<String,DbTypeDTO> workType2 = dt2.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRWKT")
                                    &&v.get("USER_SID").getObj().equals(userSid)
                                    &&v.get("CODE_CODE").getObj().equals("C"))
                            .findFirst().orElse(null);
                    if(workType2 != null){
                        tmpUserGroup.setWorkType2("Y");
                    }else{
                        tmpUserGroup.setWorkType2("N");
                    }

                    List<Map<String,DbTypeDTO>> postionList = dt2.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRTAU")
                                    &&v.get("USER_SID").getObj().equals(userSid))
                            .toList();
                    if(!postionList.isEmpty()){
                        tmpUserGroup.setPositionList(new ArrayList<>());

                        for(Map<String,DbTypeDTO> positionRow : postionList){
                            TeamUserPositionDTO tmpPosition = new TeamUserPositionDTO();

                            String code = Util.getStrChk(positionRow.get("CODE_CODE").getObj());
                            String terminal = Util.getStrChk(positionRow.get("VALUE1").getObj());
                            String position = Util.getStrChk(positionRow.get("VALUE2").getObj());

                            Map<String,Object> tmpHrpat = hrpat.stream().filter(v->v.get("CODE_CODE").equals(code)).findFirst().orElse(null);
                            if(tmpHrpat==null){
                                throw new Exception(userId+"("+code+")"+"는 알수없는 팀의 권한을 가지고 있습니다.");
                            }
                            tmpPosition.setTeamCode(code);
                            tmpPosition.setTeamName(Util.getStrChk(tmpHrpat.get("CODE_NAME")));
                            tmpPosition.setTeminalCode(terminal);

                            Map<String,Object> tmpPostn = postn.stream().filter(v->v.get("CODE_CODE").equals(position)).findFirst().orElse(null);
                            if(tmpPostn==null){
                                throw new Exception(userId+"("+position+")"+"는 알수없는 직책을 가지고 있습니다.");
                            }
                            tmpPosition.setPosition(Util.getStrChk(tmpPostn.get("CODE_NAME")));

                            tmpUserGroup.getPositionList().add(tmpPosition);
                        }
                    }

                    tmpTeamGroup.userArray.add(tmpUserGroup);
                }


            }



        } catch (Exception e) {
            ret = ResponseDTO.<List<TeamGroupDTO>>builder().errFlag("Y").errMsg(e.getMessage()).build();
        }
        return ret;
    }
}
