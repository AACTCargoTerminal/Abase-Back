package com.aact.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClsUserInfo implements Serializable {

	private String sesId;
	private BigDecimal userSid;
	private String userId;
	private String userName;
	private String userNameDefault;
	private String userCompany;
	private String userCompanyName;
	private String userBranch;
	private String userBranchName;
	private String userDepart;
	private String userDepartName;
	private String userLang;
	private String userLangName;
	private String userEmail;
	private String userDepartmentEmail;
	private String userPhone;
	private String userMobile;
	private String userFax;
	private String userOperationCarrier;
	private String userIpAddress;
	private String userTerminalCodeWork;
	private String userTerminalNameWork;
	private String userBondedZoneCd;
	private String userEdiSenderCd;
	private String userAUTH_WORKTIMELINE_YN;
	private String userAUTH_BOARD_WRITE_YN;
	private String userAUTH_BOARDHP_WRITE_YN;
	private String userAUTH_IN_CANCEL_YN;
	private String userAUTH_IT_BOARD_YN;
	private String userNOTICEDISPLAY_YN;
	private String userHrDeptCd;
	private String userHrDeptName;
	private String userHrLeaderYN;
	private String pgmId;
	private String menuMode;
	private byte[] signData;
	private String signType;
	private List<Map<String,Object>> relArray;

	public static ClsUserInfo from(Map<String, Object> dto, String ip) {
		ClsUserInfo ret = new ClsUserInfo();

		ret.setUserSid((BigDecimal) dto.get("USER_SID"));
		ret.setUserId((String) dto.get("USER_ID"));
		ret.setUserName((String) dto.get("USER_NAME"));
		ret.setUserNameDefault((String) dto.get("USER_NAME_DEFAULT"));
		ret.setUserCompany((String) dto.get("COMPANY_CODE"));
		ret.setUserCompanyName((String) dto.get("COMPANY_NAME"));
		ret.setUserBranch((String) dto.get("BRANCH_CODE"));
		ret.setUserBranchName((String) dto.get("BRANCH_NAME"));
		ret.setUserDepart((String) dto.get("DEPARTMENT_CODE"));
		ret.setUserDepartName((String) dto.get("DEPARTMENT_NAME"));
		ret.setUserLang((String) dto.get("DEFAULT_LANGUAGE_CODE"));
		ret.setUserLangName((String) dto.get("DEFAULT_LANGUAGE_NAME"));
		ret.setUserEmail((String) dto.get("EMAIL_ADDRESS"));
		ret.setUserDepartmentEmail((String) dto.get("DEPARTMENT_EMAIL"));
		ret.setUserPhone((String) dto.get("PHONE_NO"));
		ret.setUserMobile((String) dto.get("MOBILE_NO"));
		ret.setUserFax((String) dto.get("FAX_NO"));
		ret.setUserOperationCarrier((String) dto.get("OPERATION_CARRIER"));
		ret.setUserIpAddress((String) dto.get("TERMINAL_CODE_WORK") + ":" + ip);
		ret.setUserTerminalCodeWork((String) dto.get("TERMINAL_CODE_WORK"));
		ret.setUserTerminalNameWork((String) dto.get("TERMINAL_NAME_WORK"));
		ret.setUserBondedZoneCd((String) dto.get("BODEDZONE_CD"));
		ret.setUserEdiSenderCd((String) dto.get("EDI_SENDER_CD"));

		ret.setUserAUTH_BOARD_WRITE_YN((String) dto.get("AUTH_WORKTIMELINE_YN"));
		ret.setUserAUTH_BOARDHP_WRITE_YN((String) dto.get("AUTH_BOARD_WRITE_YN"));
		ret.setUserAUTH_IN_CANCEL_YN((String) dto.get("AUTH_BOARDHP_WRITE_YN"));
		ret.setUserAUTH_WORKTIMELINE_YN((String) dto.get("AUTH_IN_CANCEL_YN"));
		ret.setUserAUTH_IT_BOARD_YN((String) dto.get("AUTH_IT_BOARD_YN"));

		ret.setUserNOTICEDISPLAY_YN((String) dto.get("NOTICEDISPLAY_YN"));

		ret.setUserHrDeptCd((String) dto.get("HR_DEPT_CODE"));
		ret.setUserHrDeptName((String) dto.get("HR_DEPT_NAME"));
		ret.setUserHrLeaderYN((String) dto.get("HR_LEADER_YN"));
		if(dto.get("DATA")==null||dto.get("DATA").equals("")){
			ret.setSignData(null);
		}else{
			ret.setSignData((byte[]) dto.get("DATA"));
		}

		ret.setSignType((String) dto.get("MIME_TYPE"));

		return ret;
	}

}
