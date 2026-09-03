package com.aact.authservice.service;

import com.aact.authservice.dto.InfraUser;
import com.aact.authservice.dto.LoginReq;
import com.aact.authservice.dto.SetUserDTO;
import com.aact.authservice.repo.UserRepo;
import com.aact.common.*;
import com.aact.common.ServiceBase;
import com.aact.commonClient.service.ClientService;
import com.aact.commonClient.service.FileClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService extends ServiceBase {

    private final ObjectProvider<UserRepo> userRepo;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ClientService clientService;
    private final FileClientService fileClientService;


    public ResponseDTO<?> login(LoginReq.LoginDTO dto, HttpServletRequest hReq){
        UserRepo repo = userRepo.getObject();
        return execute(repo,()-> {
            DbDto ret = repo.getLogin(dto.username(), dto.password());

            if (ret.getErrFlag().equalsIgnoreCase("N")) {
                var res = ResponseDTO.from(ret);
                String sid = ((BigDecimal) res.getData().get(0).get(0).get("USER_SID")).toPlainString();
                String terminal = ((String) res.getData().get(0).get(0).get("TERMINAL_CODE_WORK"));
                if (!dto.terminal().equals("")) {
                    if (terminal != null && !terminal.equalsIgnoreCase(dto.terminal())
                            && !terminal.equalsIgnoreCase("")) {
                        throw new BizException("login","터미널 정보가 일치 하지 않습니다.");
                    }
                }

                DbDto relRet = repo.getUserRel(Util.getDecimal(sid),"Y","KOR", Util.getGUID(),dto.username(),hReq.getRemoteAddr(),"login");

                ResponseDTO<Map<Integer, List<Map<String, Object>>>> chgRet = ResponseDTO.from(relRet);

                if(chgRet.getErrFlag().equals("Y")){
                    throw new BizException("login",chgRet.getErrMsg());
                }

                String menuMode =dto.menuType();

                if(menuMode!=null&&menuMode.equals("INTRA")){
                    if(chgRet.getData().get(0)!=null&&!chgRet.getData().get(0).isEmpty()){
                        List<Map<String, Object>> tmpObj = chgRet.getData().get(0);
                        Map<String,Object> findObj = tmpObj.stream().filter((m)->Util.getStrChk(m.get("CLASS_CODE")).equals("HRPAT")).findFirst().orElse(null);

                        if(findObj==null){
                            throw new BizException("login","파트 확인요청");
                        }
                        findObj = tmpObj.stream().filter((m)->Util.getStrChk(m.get("CLASS_CODE")).equals("TRMCD")).findFirst().orElse(null);

                        if(findObj==null){
                            throw new BizException("login","터미널 확인요청");
                        }

                        findObj = tmpObj.stream().filter((m)->Util.getStrChk(m.get("CLASS_CODE")).equals("HRWDT")&&(Util.getStrChk(m.get("CODE_CODE")).equals("A")||Util.getStrChk(m.get("CODE_CODE")).equals("C"))).findFirst().orElse(null);

                        if(findObj==null){
                            throw new BizException("login","입사일 및 그룹 입사일 확인요청");
                        }

                    }else{
                        throw new BizException("login","파트, 터미널, 입사일 설정 확인 요청");
                    }

                }

                HttpSession session = hReq.getSession(true);
                ClsUserInfo info = ClsUserInfo.from(res.getData().get(0).get(0), hReq.getRemoteAddr());
                info.setRelArray(chgRet.getData().get(0));
                info.setSesId(session.getId());
                session.setAttribute("USER_PROFILE", info);
                String userKey = "sess:user:" + sid;
                String newSessionId = session.getId();

                String oldSession = stringRedisTemplate.opsForValue().get(userKey);

                if (oldSession != null && !oldSession.equals(newSessionId)) {
                    stringRedisTemplate.delete(userKey);
                    stringRedisTemplate.opsForValue().set(userKey, newSessionId, Duration.ofMinutes(30));
                    stringRedisTemplate.opsForValue().set("sess:kill-reason:" + oldSession,
                            "KICKED_BY_LOGIN|" + sid + "|" + newSessionId, Duration.ofMinutes(10));
                } else {
                    stringRedisTemplate.opsForValue().set(userKey, newSessionId, Duration.ofMinutes(30));
                }
            }else{
                if (ret.getErrCode().equalsIgnoreCase("SP_USER_NOT_FOUND")) {
                    throw new BizException("login","사용자 아이디가 잘못되었습니다.");
                }
                throw new BizException("login",ret.getErrMsg());
            }
            return ResponseDTO.builder().errFlag("N").errMsg("로그인완료").build();
        });
    }

    public ResponseDTO<?> verity(){

        return execute(()->{
            ClsUserInfo info = UserContext.get();
            return ResponseDTO.builder().errFlag("N").data(info).build();
        });
    }

    public ResponseDTO<?> logout(HttpServletRequest hReq){
        return execute(()->{
            HttpSession session = hReq.getSession(false);
            ClsUserInfo info = UserContext.get();
            String userKey = "sess:user:" + info.getUserSid().toString();

            String userSes = stringRedisTemplate.opsForValue().get(userKey);

            if (userSes != null) {
                stringRedisTemplate.delete(userKey);
            }

            session.invalidate();

            return ResponseDTO.builder().errFlag("N").errMsg("로그아웃 완료").build();
        });
    }
    public ResponseDTO<?> buildMenu() {

        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.getMenu(info.getUserId(), info.getMenuMode());

            return okOrThrow("buildMenu", dbRet);
        });


    }

    public ResponseDTO<?> getUserInfo() {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.getUserInfo(info.getUserId(), info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getUserInfo", dbRet);
        });

    }

    public ResponseDTO<?> getUserM010_003(String userId) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.getUserM010_003(userId, info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getUserM010_003", dbRet);
        });

    }

    public ResponseDTO<?> getUserRel(BigDecimal userSid,String usableFlag) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.getUserRel(userSid, usableFlag,info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getUserRel", dbRet);
        });

    }

    public ResponseDTO<?> setUserDelete(String userId) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.setUserDelete(userId, "N",info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("setUserDelete", dbRet);
        });

    }

    public ResponseDTO<?> getUserList(String deptCode,String userId,String userName,String usableFlag){
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.getUserL010_002(deptCode,userId,userName,usableFlag,info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getUserList", dbRet);
        });
    }


    public ResponseDTO<?> setUserInfo(HttpServletRequest req, SetUserDTO dto) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;
            String pass = "";
            String passHp = "";

            dbRet = repo.getUserInfo(info.getUserId(), info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            if (dbRet.getErrFlag().equalsIgnoreCase("N")) {
                pass = (String) dbRet.getResult().get(0).get(0).get("USER_PASSWORD").getObj();
                passHp = (String) dbRet.getResult().get(0).get(0).get("USER_PASSWORD_HP").getObj();
            } else {
                throw new BizException("setUserInfo", dbRet.getErrMsg());
            }

            if (!(dto.getPass() == null || dto.getPass().isEmpty())) {
                pass = dto.getPass();
            }

            if (!(dto.getPassHp() == null || dto.getPassHp().isEmpty())) {
                passHp = dto.getPassHp();
            }

            Map<String,DbTypeDTO> tmp = dbRet.getResult().get(0).get(0);

            String companyCode = Util.getStrChk(tmp.get("COMPANY_CODE").getObj());
            String branchCode = Util.getStrChk(tmp.get("BRANCH_CODE").getObj());
            String departmentCode = Util.getStrChk(tmp.get("DEPARTMENT_CODE").getObj());
            String emailAddress = Util.getStrChk(tmp.get("EMAIL_ADDRESS").getObj());
            String phoneNo = Util.getStrChk(tmp.get("PHONE_NO").getObj());
            String mobileNo = Util.getStrChk(tmp.get("MOBILE_NO").getObj());
            String faxNo = Util.getStrChk(tmp.get("FAX_NO").getObj());
            String terminalCode = Util.getStrChk(tmp.get("TERMINAL_CODE_WORK").getObj());
            String terminalName = Util.getStrChk(tmp.get("TERMINAL_NAME_WORK").getObj());
            String authWorktimelineYn = Util.getStrChk(tmp.get("AUTH_WORKTIMELINE_YN").getObj());
            String authBoardWriteYn = Util.getStrChk(tmp.get("AUTH_BOARD_WRITE_YN").getObj());
            String authInCancelYn = Util.getStrChk(tmp.get("AUTH_IN_CANCEL_YN").getObj());
            String authBoardhpWriteYn = Util.getStrChk(tmp.get("AUTH_BOARDHP_WRITE_YN").getObj());
            String authItBoardYn = Util.getStrChk(tmp.get("AUTH_IT_BOARD_YN").getObj());;

            dbRet = repo.setUserInfo(info.getUserId(),"", pass, passHp, dto.getUserName(), dto.getUserName2(),
                    companyCode, branchCode, departmentCode, dto.getLangCode(), emailAddress,
                    phoneNo,mobileNo, faxNo, terminalCode,
                    terminalName, authWorktimelineYn, authBoardWriteYn,
                    authInCancelYn, authBoardhpWriteYn, authItBoardYn,
                    info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setUserInfo", dbRet.getErrMsg());
            }

            info.setUserNameDefault(dto.getUserName());
            info.setUserName(dto.getUserName2());
            info.setUserLang(dto.getLangCode());

            HttpSession session = req.getSession(false);
            session.setAttribute("USER_PROFILE", info);

            return okOrThrow("setUserInfo", dbRet);
        });
    }
    public ResponseDTO<?> setUserInfoMgm(InfraUser.SaveUserDTO dto) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();

        return execute(repo,()->{
            DbDto dbRet = null;


            ResponseDTO<List<Map<String, Object>>> hrpat = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRPAT")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(hrpat.getErrFlag().equals("Y")){
                throw new BizException("setUserInfoMgm", hrpat.getErrMsg());
            }

            String teminalCode = "";
            String teminalName = "";
            String companyCode = "AACT";
            String branchCode = "AACTINC";
            String deptCode = hrpat.getData().stream().filter(v->v.get("CODE_CODE").equals(dto.teamCode()))
                    .findFirst().map(v->Util.getStrChk(v.get("VALUE3_CHAR"))).orElse("");
            String langCode = "KOR";
            String email = "";
            String phone = "";
            String mobile = "";
            String fax = "";
            String workYn = "N";
            String boardYn = "N";
            String inYn = "N";
            String boardHpYn = "N";
            String itYn = "N";

            if(deptCode.isEmpty()){
                throw new BizException("setUserInfoMgm", "HR 부서와 SAMS 부서의 일처하는 부서가 없습니다.");
            }

            if(dto.userSid().compareTo(BigDecimal.ZERO)==0){
                dbRet = repo.setUserInfo("",dto.userId(), dto.userPass(), dto.userPassHp(), dto.userName1(),
                        dto.userName2(),companyCode,branchCode,deptCode,
                        langCode,email,phone,mobile,fax,dto.terminalCode(),dto.terminalName(),
                        workYn,boardYn,inYn,boardHpYn,itYn,
                        info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
                }
            }else{
                dbRet = repo.getUserInfo(dto.userId(), info.getUserLang(), Util.getGUID(), info.getUserId(),
                        info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
                }

                if(!dbRet.getResult().get(0).isEmpty()){
                    teminalCode =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("TERMINAL_CODE_WORK").getObj(),dto.terminalCode());
                    teminalName =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("TERMINAL_NAME_WORK").getObj(),dto.terminalName());
                    companyCode = Util.getStrChk(dbRet.getResult().get(0).get(0).get("COMPANY_CODE").getObj());
                    branchCode = Util.getStrChk(dbRet.getResult().get(0).get(0).get("BRANCH_CODE").getObj());
                    langCode =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("DEFAULT_LANGUAGE_CODE").getObj());
                    email =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("EMAIL_ADDRESS").getObj());
                    phone =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("PHONE_NO").getObj());
                    mobile =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("MOBILE_NO").getObj());
                    fax =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("FAX_NO").getObj());
                    workYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_WORKTIMELINE_YN").getObj());
                    boardYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_BOARD_WRITE_YN").getObj());
                    inYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_IN_CANCEL_YN").getObj());
                    boardHpYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_BOARDHP_WRITE_YN").getObj());
                    itYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_IT_BOARD_YN").getObj());
                }

                dbRet = repo.setUserInfo(dto.userId(),dto.userIdChange(), dto.userPass(), dto.userPassHp(), dto.userName1(),
                        dto.userName2(),companyCode,branchCode,deptCode,
                        langCode,email,phone,mobile,fax,teminalCode,teminalName,
                        workYn,boardYn,inYn,boardHpYn,itYn,
                        info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
                }
            }



            BigDecimal userSid = Util.getDecimal(dbRet.getRetObj().get("O_USER_SID"));

            if(userSid.compareTo(BigDecimal.ZERO)==0){
                throw new BizException("setUserInfoMgm", "유저 정보 저장실패 HR 설정 불가능");
            }

            dbRet = repo.setUserRel(userSid,"HRWDT","A",
                    "0000",dto.joinDay(),"","",""
                    ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
            }
            if(!dto.groupJoinDay().isEmpty()){
                dbRet = repo.setUserRel(userSid,"HRWDT","C",
                        "0000",dto.groupJoinDay(),"","",""
                        ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
                }
            }

            dbRet = repo.setUserRel(userSid,"HRPAT",dto.teamCode(),
                    "0000",dto.teamDate(),"","",""
                    ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
            }

            dbRet = repo.setUserRel(userSid,"TRMCD",dto.terminalCode(),
                    "0000","","","",""
                    ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
            }

            dbRet = repo.setUserRel(userSid,"TRMCD",dto.terminalCode(),
                    "0000","","","",""
                    ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
            }



            return okOrThrow("setUserInfoMgm", dbRet);
        });
    }


    public ResponseDTO<?> getPgmInfo(String pgmId) {
        ClsUserInfo info = UserContext.get();
        StringBuilder sql = new StringBuilder();
        UserRepo repo = userRepo.getObject();
        sql.append(
                "SELECT PROGRAM_SID, PROGRAM_ID, PROGRAM_PATH, PROGRAM_FILE_NAME, SYS_FUNCTION.FCM_GET_OBJECT_NAME_BY_SID(PROGRAM_SID, '"
                        + info.getUserLang() + "') PROGRAM_NAME ");
        sql.append("FROM TCM_PROGRAM_MASTER ");
        sql.append("WHERE PROGRAM_ID = '" + pgmId + "'");

        return execute(repo,()->{
            DbDto dbRet = repo.callSql(sql.toString());

            return okOrThrow("getPgmInfo", dbRet);
        });

    }

    public ResponseDTO<?> getUserList(String depCode) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.getUserList("", "", depCode, "", info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getUserList", dbRet);
        });

    }

    public ResponseDTO<?> getUserSign(String userId){
        UserRepo repo = userRepo.getObject();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TUS.MIME_TYPE,TUS.DATA ");
        sql.append("FROM TCM_USER_MASTER TUM ");
        sql.append("JOIN TCM_USER_SIGN TUS ");
        sql.append("ON TUM.USER_SID = TUS.USER_SID ");
        sql.append("WHERE TUM.USABLE_FLAG  = 'Y' ");
        sql.append("AND TUM.USER_ID = '"+userId+"'");

        return execute(repo,()->{
            DbDto dbRet = repo.callSql(sql.toString());

            return okOrThrow("getUserSign", dbRet);
        });
    }

    public ResponseDTO<?> setUserSign(HttpServletRequest req, List<MultipartFile> files) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();

        return execute(repo,()->{
            if(files==null||files.isEmpty()){
                throw new BizException("setUserSign","파일이 없습니다.");
            }

            MultipartFile f = files.get(0);

            try {
                String ct = safeContentType(f);
                String name = safeBaseName(f.getOriginalFilename(), 1);

                BufferedImage src;
                if ("application/pdf".equals(ct) || name.toLowerCase().endsWith(".pdf")) {
                    src = renderFirstPagePdfToImage(f.getBytes(), 200);
                } else {
                    src = ImageIO.read(new ByteArrayInputStream(f.getBytes()));
                }
                if (src == null) throw new BizException("setUserSign", "이미지를 읽을 수 없습니다.");

                BufferedImage out = extractInkAndCropToPng(src, 100,10);

                ByteArrayOutputStream imgOut = new ByteArrayOutputStream();
                ImageIO.write(out, "png", imgOut);

                byte[] pngBytes = imgOut.toByteArray();

                DbDto dbRet = repo.setUserSign(info.getUserSid(),name,"image/png",new BigDecimal(f.getSize()),pngBytes, info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setUserSign",dbRet.getErrMsg());
                }
                HttpSession session = req.getSession(false);
                info.setSignType("image/png");
                info.setSignData(pngBytes);
                session.setAttribute("USER_PROFILE", info);

                return okOrThrow("setUserSign",
                        ResponseDTO.builder().errFlag("N").errMsg(dbRet.getErrMsg()).build());

            } catch (IOException ex) {
                throw new SysException("setUserSign", ex.getMessage());
            }
        });

    }

    private String safeContentType(MultipartFile f) {
        String ct = f.getContentType();
        return ct == null ? "" : ct;
    }

    private String safeBaseName(String name, int idx) {
        if (name == null || name.isBlank()) return "file_" + idx;
        // zip entry에 위험한 문자 제거
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
    }

    // PDF 첫 페이지를 이미지로 렌더링
    private BufferedImage renderFirstPagePdfToImage(byte[] pdfBytes, int dpi) throws IOException {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            // 첫 페이지(0)
            return renderer.renderImageWithDPI(0, dpi);
        }
    }

    /**
     * 핵심: 배경 제거 + 글자 영역만 크롭 + 투명 PNG
     * - threshold: 0~255 (클수록 더 많은 픽셀을 "배경"으로 판단)
     *   보통 종이 배경이면 230~245 사이가 잘 맞음
     */
    private BufferedImage extractInkAndCropToPng(BufferedImage src, int threshold,int padding) throws IOException {
        int w = src.getWidth();
        int h = src.getHeight();

        int minX = w, minY = h, maxX = -1, maxY = -1;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = src.getRGB(x, y);

                // ✅ 투명 픽셀은 제외
                int a = (argb >> 24) & 0xff;
                if (a == 0) continue;

                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                if (isInk(r, g, b, threshold)) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        // 잉크가 없으면 원본 반환
        if (maxX < 0 || maxY < 0) {
            return src;
        }

        // padding 크게 적용 + 경계 보정
        minX = Math.max(0, minX - padding);
        minY = Math.max(0, minY - padding);
        maxX = Math.min(w - 1, maxX + padding);
        maxY = Math.min(h - 1, maxY + padding);

        int cropW = maxX - minX + 1;
        int cropH = maxY - minY + 1;

        // ✅ getSubimage는 (x, y, width, height)
        BufferedImage cropped = src.getSubimage(minX, minY, cropW, cropH);
        BufferedImage transparent = makeTransparentInkOnly(cropped, threshold);

//        if (cropW + 50 < cropH) {
//            return rotate90(transparent);
//        }

        return transparent;
    }

    private BufferedImage makeTransparentInkOnly(BufferedImage src, int threshold) {
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = src.getRGB(x, y);

                int a = (argb >> 24) & 0xff;

                // ✅ 이미 투명한 픽셀은 그대로 투명 처리
                if (a == 0) {
                    out.setRGB(x, y, 0x00000000);
                    continue;
                }

                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                if (isInk(r, g, b, threshold)) {
                    out.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                } else {
                    out.setRGB(x, y, 0x00000000);
                }
            }
        }

        return out;
    }

    private BufferedImage rotate90(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage rotated = new BufferedImage(
                h, w, src.getType());

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                rotated.setRGB(h - 1 - y, x, src.getRGB(x, y));
            }
        }

        return rotated;
    }

    private boolean isInk(int r, int g, int b, int thresholdDark) {
        // 1) 기존: 어두운 건 무조건 잉크 (검정/진한 잉크)
        int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
        if (gray < thresholdDark) return true;

        // 2) 색 잉크(연한 빨강/파랑 등)도 잉크로
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        float s = hsb[1];   // saturation 0~1
        float v = hsb[2];   // brightness 0~1

        // "거의 흰색(배경)" 제외: v가 너무 높고 s가 너무 낮으면 배경일 확률 큼
        // 연한 빨강은 v가 높아도 s가 상대적으로 남음 -> 통과 가능
        boolean notPaper = !(v > 0.97f && s < 0.10f);

        // 색감이 조금이라도 있으면 잉크 후보
        boolean coloredInk = (s > 0.12f) && notPaper;

        // 추가로 "빨강 우세"면 더 적극적으로 잉크로 (연한 도장 보강)
        boolean redDominant = (r - Math.max(g, b)) > 12; // 10~20 사이 튜닝
        boolean redInk = redDominant && (s > 0.10f) && (v > 0.35f); // 연한 빨강 살리기

        return coloredInk || redInk;
    }

    public ResponseDTO<?> getUserGroup(){
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        DbDto dbRet = null;
        dbRet = execute(repo, () -> {

            DbDto db = repo.getUserL010_003(
                    info.getUserLang(),
                    Util.getGUID(),
                    info.getUserId(),
                    info.getUserIpAddress(),
                    info.getPgmId()
            );

            if ("Y".equals(db.getErrFlag())) {
                throw new BizException("getUserGroup", db.getErrMsg());
            }

            return db;
        });
        ResponseDTO<byte[]> ret =
                fileClientService.fileRead("/IMG/TEMPLATE/USER_GROUP_TEMPLATE.xlsx");
        if(ret.getErrFlag().equals("Y")){
            throw new BizException("getUserGroup",ret.getErrMsg());
        }

        ResponseDTO<List<Map<String, Object>>> hrpat = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                .path("/sys/getBaseOds")
                .queryParam("classCode", "HRPAT")
                .queryParam("codeName", "")
                .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
        if(hrpat.getErrFlag().equals("Y")){
            throw new BizException("setUserGroup", hrpat.getErrMsg());
        }

        List<Map<String, Object>> hrpatDt = hrpat.getData();

        ResponseDTO<List<Map<String, Object>>> postn = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                .path("/sys/getBaseOds")
                .queryParam("classCode", "POSTN")
                .queryParam("codeName", "")
                .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
        if(postn.getErrFlag().equals("Y")){
            throw new BizException("setUserGroup", postn.getErrMsg());
        }

        List<Map<String, Object>> postnDt = postn.getData();
        ResponseDTO<List<InfraUser.TeamGroupDTO>> dto = InfraUser.changeGroup(dbRet,hrpatDt,postnDt);
        if(dto.getErrFlag().equals("Y")){
            throw new BizException("setUserGroup", dto.getErrMsg());
        }

        try(
                Workbook srcWorkbook = WorkbookFactory.create(new ByteArrayInputStream(ret.getData()));
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ){
            Sheet sheet1 = srcWorkbook.getSheet("SHEET1");
            Sheet sheet2 = srcWorkbook.getSheet("SHEET2");

            // =========================
            // 1. 필요한 행 수 먼저 계산
            // =========================
            int totalUserCount = dto.getData().stream()
                    .mapToInt(v -> v.getUserArray().size())
                    .sum();

            int totalPositionCount = dto.getData().stream()
                    .flatMap(v -> v.getUserArray().stream())
                    .filter(v -> v.getPositionList() != null)
                    .mapToInt(v -> v.getPositionList().size())
                    .sum();

            // 템플릿 기준 행
            int sheet1TemplateIdx = 6;
            int sheet2TemplateIdx = 6;

            Row sheet1TemplateRow = sheet1.getRow(sheet1TemplateIdx);
            Row sheet2TemplateRow = sheet2.getRow(sheet2TemplateIdx);

            // =========================
            // SHEET2용 중간행 스타일 생성
            // =========================
            CellStyle[] sheet2Styles = new CellStyle[6];

            for (int i = 0; i < 6; i++) {

                Cell sourceCell = sheet2TemplateRow.getCell(
                        i,
                        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                );

                CellStyle style = srcWorkbook.createCellStyle();
                style.cloneStyleFrom(sourceCell.getCellStyle());

                // 중간 가로선 제거
                style.setBorderTop(BorderStyle.NONE);
                style.setBorderBottom(BorderStyle.NONE);

                sheet2Styles[i] = style;
            }

            // =========================
            // 2. 기존 아래쪽 영역 한 번만 밀기
            // =========================
            int sheet1AddCount = Math.max(0, totalUserCount - 1);

            if (sheet1AddCount > 0 && sheet1.getLastRowNum() > sheet1TemplateIdx) {
                sheet1.shiftRows(
                        sheet1TemplateIdx + 1,
                        sheet1.getLastRowNum(),
                        sheet1AddCount,
                        true,
                        false
                );
            }

            int sheet2AddCount = Math.max(0, totalPositionCount - 1);

            if (sheet2AddCount > 0 && sheet2.getLastRowNum() > sheet2TemplateIdx) {
                sheet2.shiftRows(
                        sheet2TemplateIdx + 1,
                        sheet2.getLastRowNum(),
                        sheet2AddCount,
                        true,
                        false
                );
            }


            // =========================
            // 3. SHEET1 생성
            // =========================
            int sheet1Idx = 6;

            for (InfraUser.TeamGroupDTO teamRow : dto.getData()) {

                int teamStartIdx = sheet1Idx;

                for (InfraUser.TeamUserGroupDTO groupRow : teamRow.getUserArray()) {

                    Row rowC;

                    if (sheet1Idx == sheet1TemplateIdx) {
                        rowC = sheet1TemplateRow;
                    } else {
                        rowC = sheet1.createRow(sheet1Idx);
                        copyRowStyleFast(
                                sheet1TemplateRow,
                                rowC,
                                9
                        );
                    }

                    setCellValue(rowC, 0, teamRow.getTeamCode());
                    setCellValue(rowC, 1, teamRow.getTeamName());
                    setCellValue(rowC, 2, groupRow.getTeminalCode());
                    setCellValue(rowC, 3, groupRow.getUserName());
                    setCellValue(rowC, 4, groupRow.getUserId());
                    setCellValue(rowC, 5, groupRow.getPass());
                    setCellValue(rowC, 6, groupRow.getGroupJoinDate());
                    setCellValue(rowC, 7, groupRow.getJoinDate());
                    setCellValue(rowC, 8, groupRow.getWorkType());
                    setCellValue(rowC, 9, groupRow.getWorkType2());

                    sheet1Idx++;
                }

                // 팀 단위 병합
                if (sheet1Idx - teamStartIdx > 1) {

                    sheet1.addMergedRegion(
                            new CellRangeAddress(
                                    teamStartIdx,
                                    sheet1Idx - 1,
                                    0,
                                    0
                            )
                    );

                    sheet1.addMergedRegion(
                            new CellRangeAddress(
                                    teamStartIdx,
                                    sheet1Idx - 1,
                                    1,
                                    1
                            )
                    );
                }
            }

            // =========================
            // 4. SHEET2 생성
            // =========================
            int sheet2Idx = 6;

            for (InfraUser.TeamGroupDTO teamRow : dto.getData()) {

                for (InfraUser.TeamUserGroupDTO groupRow
                        : teamRow.getUserArray()) {

                    if (groupRow.getPositionList() == null
                            || groupRow.getPositionList().isEmpty()) {
                        continue;
                    }

                    for (InfraUser.TeamUserPositionDTO positionRow
                            : groupRow.getPositionList()) {

                        Row rowD;

                        if (sheet2Idx == sheet2TemplateIdx) {

                            rowD = sheet2TemplateRow;

                            // 첫 데이터 행도 SHEET2 전용 스타일 적용
                            for (int i = 0; i < 6; i++) {

                                Cell cell = rowD.getCell(
                                        i,
                                        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                                );

                                cell.setCellStyle(sheet2Styles[i]);
                            }

                        } else {

                            rowD = sheet2.createRow(sheet2Idx);
                            rowD.setHeight(sheet2TemplateRow.getHeight());

                            for (int i = 0; i < 6; i++) {

                                Cell cell = rowD.createCell(i);
                                cell.setCellStyle(sheet2Styles[i]);
                            }
                        }

                        setCellValue(
                                rowD,
                                0,
                                positionRow.getTeamCode()
                        );

                        setCellValue(
                                rowD,
                                1,
                                positionRow.getTeamName()
                        );

                        setCellValue(
                                rowD,
                                2,
                                positionRow.getTeminalCode()
                        );

                        setCellValue(
                                rowD,
                                3,
                                groupRow.getUserName()
                        );

                        setCellValue(
                                rowD,
                                4,
                                groupRow.getUserId()
                        );

                        setCellValue(
                                rowD,
                                5,
                                positionRow.getPosition()
                        );

                        sheet2Idx++;
                    }
                }
            }

            // =========================
            // SHEET2 마지막 데이터 행에만 하단 테두리 적용
            // =========================
            if (sheet2Idx > sheet2TemplateIdx) {

                Row lastRow = sheet2.getRow(sheet2Idx - 1);

                for (int i = 0; i < 6; i++) {

                    Cell cell = lastRow.getCell(
                            i,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                    );

                    CellStyle lastStyle = srcWorkbook.createCellStyle();
                    lastStyle.cloneStyleFrom(cell.getCellStyle());

                    lastStyle.setBorderBottom(BorderStyle.MEDIUM);

                    cell.setCellStyle(lastStyle);
                }
            }

            srcWorkbook.write(bos);

            ret = ResponseDTO.<byte[]>builder().errFlag("N").errMsg("재직자목록 다운완료").data(bos.toByteArray()).build();
        }catch (IOException ex){
            throw new BizException("getUserGroup",ex.getMessage());
        }
        return ret;
    }

    public ResponseDTO<?> setUserGroup(InfraUser.UserGroupDTO dto){
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();

        return execute(repo,()->{
            DbDto dbRet = null;

            ResponseDTO<List<Map<String, Object>>> postn = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "POSTN")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(postn.getErrFlag().equals("Y")){
                throw new BizException("setUserGroup", postn.getErrMsg());
            }

            List<Map<String, Object>> postnDt = postn.getData();

            ResponseDTO<List<Map<String, Object>>> hrpat = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRPAT")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(hrpat.getErrFlag().equals("Y")){
                throw new BizException("setUserGroup", hrpat.getErrMsg());
            }

            List<Map<String, Object>> hrpatDt = hrpat.getData();

            ResponseDTO<List<Map<String, Object>>> trmcd = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "TRMCD")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(trmcd.getErrFlag().equals("Y")){
                throw new BizException("setUserGroup", trmcd.getErrMsg());
            }

            List<Map<String, Object>> trmcdDt = trmcd.getData();

            for(InfraUser.TeamGroupDTO row : dto.getTeamGroup()) {
                Map<String,Object> findHrpat = hrpatDt.stream()
                        .filter(v->v.get("CODE_NAME").equals(row.getTeamName()))
                        .findFirst().orElse(null);

                if(findHrpat == null){
                    throw new BizException("setUserGroup", row.getTeamName()+" 공통코드에 동일한 팀명이 없습니다.");
                }
                for(InfraUser.TeamUserGroupDTO userRow: row.getUserArray()){
                    Map<String,Object> findTrmcd = trmcdDt.stream()
                            .filter(v->v.get("CODE_CODE").equals(userRow.getTeminalCode()))
                            .findFirst().orElse(null);
                    if(findTrmcd == null){
                        throw new BizException("setUserGroup", userRow.getTeminalCode()+" 공통코드에 동일한 터미널이 없습니다.");
                    }

                    dbRet = repo.callSql("SELECT USER_SID FROM TCM_USER_MASTER WHERE USER_ID = '"+userRow.getUserId()+"'");

                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setUserGroup", dbRet.getErrMsg());
                    }

                    BigDecimal userSid = null;
                    String pass = "aact";

                    if(!userRow.getPass().isEmpty()){
                        pass = userRow.getPass();
                    }

                    if(dbRet.getResult().get(0).isEmpty()){

                        dbRet = repo.setUserInfo("",userRow.getUserId(),pass,pass,userRow.getUserName(),userRow.getUserName(),"AACT","AACTINC"
                        ,Util.getStrChk(findHrpat.get("VALUE3_CHAR")),"KOR","","","","",Util.getStrChk(findTrmcd.get("CODE_CODE"))
                        , Util.getStrChk(findTrmcd.get("CODE_NAME")),"N","N","N","N","N"
                                ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setUserGroup", dbRet.getErrMsg());
                        }

                        userSid = Util.getDecimal(dbRet.getRetObj().get("O_USER_SID"));

                    }else{

                        String companyCode = "AACT";
                        String branchCode = "AACTINC";
                        String deptCode = hrpat.getData().stream().filter(v->v.get("CODE_CODE").equals(userRow.getTeminalCode()))
                                .findFirst().map(v->Util.getStrChk(v.get("VALUE3_CHAR"))).orElse("");
                        String langCode = "KOR";
                        String email = "";
                        String phone = "";
                        String mobile = "";
                        String fax = "";
                        String workYn = "N";
                        String boardYn = "N";
                        String inYn = "N";
                        String boardHpYn = "N";
                        String itYn = "N";

                        dbRet = repo.getUserInfo(info.getUserId(), info.getUserLang(), Util.getGUID(), info.getUserId(),
                                info.getUserIpAddress(), info.getPgmId());
                        if (dbRet.getErrFlag().equals("Y")) {
                            throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
                        }

                        if(!dbRet.getResult().get(0).isEmpty()){
                            companyCode = Util.getStrChk(dbRet.getResult().get(0).get(0).get("COMPANY_CODE").getObj());
                            branchCode = Util.getStrChk(dbRet.getResult().get(0).get(0).get("BRANCH_CODE").getObj());
                            langCode =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("DEFAULT_LANGUAGE_CODE").getObj());
                            email =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("EMAIL_ADDRESS").getObj());
                            phone =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("PHONE_NO").getObj());
                            mobile =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("MOBILE_NO").getObj());
                            fax =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("FAX_NO").getObj());
                            workYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_WORKTIMELINE_YN").getObj());
                            boardYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_BOARD_WRITE_YN").getObj());
                            inYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_IN_CANCEL_YN").getObj());
                            boardHpYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_BOARDHP_WRITE_YN").getObj());
                            itYn =  Util.getStrChk(dbRet.getResult().get(0).get(0).get("AUTH_IT_BOARD_YN").getObj());
                        }

                        dbRet = repo.setUserInfo(userRow.getUserId(),"", pass
                                , Util.getStrChk(dbRet.getResult().get(0).get(0).get("USER_PASSWORD_HP").getObj())
                                , Util.getStrChk(dbRet.getResult().get(0).get(0).get("USER_NAME1").getObj()),
                                Util.getStrChk(dbRet.getResult().get(0).get(0).get("USER_NAME2").getObj())
                                ,companyCode,branchCode,deptCode,
                                langCode,email,phone,mobile,fax, Util.getStrChk(dbRet.getResult().get(0).get(0).get("TERMINAL_CODE_WORK").getObj()),
                                Util.getStrChk(dbRet.getResult().get(0).get(0).get("TERMINAL_NAME_WORK").getObj()),
                                workYn,boardYn,inYn,boardHpYn,itYn,
                                info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                        if (dbRet.getErrFlag().equals("Y")) {
                            throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
                        }

                        userSid = Util.getDecimal(dbRet.getRetObj().get("O_USER_SID"));

                    }

                    dbRet = repo.getUserRel(userSid,"Y",info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());


                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setUserGroup", dbRet.getErrMsg());
                    }

                    List<Map<String,DbTypeDTO>> relDt = dbRet.getResult().get(0);

                    Map<String,DbTypeDTO> hrwdtSelect = relDt.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRWDT")&&v.get("CODE_CODE").getObj().equals("A"))
                            .findFirst().orElse(null);
                    if(hrwdtSelect==null){
                        dbRet = repo.setUserRel(userSid,"HRWDT","A",
                                "0000",userRow.getJoinDate(),"","",""
                                ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setUserGroup", dbRet.getErrMsg());
                        }
                    }

                    if(!userRow.getGroupJoinDate().isEmpty()){
                        Map<String,DbTypeDTO> hrwdtSelectC = relDt.stream()
                                .filter(v->v.get("CLASS_CODE").getObj().equals("HRWDT")&&v.get("CODE_CODE").getObj().equals("C"))
                                .findFirst().orElse(null);
                        if(hrwdtSelectC==null){
                            dbRet = repo.setUserRel(userSid,"HRWDT","C",
                                    "0000",userRow.getGroupJoinDate(),"","",""
                                    ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                            if(dbRet.getErrFlag().equals("Y")){
                                throw new BizException("setUserGroup", dbRet.getErrMsg());
                            }
                        }
                    }
                    Map<String, DbTypeDTO> hrpatSelect = relDt.stream()
                            .filter(v -> "HRPAT".equals(
                                    Util.getStrChk(v.get("CLASS_CODE").getObj())
                            ))
                            .filter(v -> !Util.getStrChk(v.get("VALUE1").getObj()).isEmpty())
                            .max(Comparator.comparing(
                                    v -> Util.getStrChk(v.get("VALUE1").getObj())
                            ))
                            .orElse(null);
                    if(hrpatSelect==null){
                        dbRet = repo.setUserRel(userSid,"HRPAT",Util.getStrChk(findHrpat.get("CODE_CODE")),
                                "0000",userRow.getJoinDate(),"","",""
                                ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setUserGroup", dbRet.getErrMsg());
                        }
                    }

                    Map<String,DbTypeDTO> trmcdSelect = relDt.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("TRMCD"))
                            .findFirst().orElse(null);
                    if(trmcdSelect==null){
                        dbRet = repo.setUserRel(userSid,"TRMCD",Util.getStrChk(findTrmcd.get("CODE_CODE")),
                                "0000","","","",""
                                ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setUserGroup", dbRet.getErrMsg());
                        }
                    }

                    Map<String,DbTypeDTO> hrwktSelect = relDt.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRWKT")&&v.get("CODE_CODE").getObj().equals("A"))
                            .findFirst().orElse(null);
                    if(hrwktSelect==null&&userRow.getWorkType().equals("Y")){
                        dbRet = repo.setUserRel(userSid,"HRWKT","A",
                                "0000",userRow.getJoinDate(),"","",""
                                ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setUserGroup", dbRet.getErrMsg());
                        }
                    }

                    Map<String,DbTypeDTO> hrwktSelect2 = relDt.stream()
                            .filter(v->v.get("CLASS_CODE").getObj().equals("HRWKT")&&v.get("CODE_CODE").getObj().equals("C"))
                            .findFirst().orElse(null);
                    if(hrwktSelect2==null&&userRow.getWorkType2().equals("Y")){
                        dbRet = repo.setUserRel(userSid,"HRWKT","C",
                                "0000",userRow.getJoinDate(),"","",""
                                ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setUserGroup", dbRet.getErrMsg());
                        }
                    }

                    if(userRow.getPositionList() != null && !userRow.getPositionList().isEmpty()){
                        for(InfraUser.TeamUserPositionDTO positionRow : userRow.getPositionList()){
                            Map<String,Object> postnSelect = postnDt.stream()
                                    .filter(v->v.get("CODE_NAME").equals(positionRow.getPosition())).findFirst().orElse(null);
                            if(postnSelect == null){
                                throw new BizException("setUserGroup", positionRow.getPosition()+"팀명과 일치하는 것은 없습니다.");
                            }
                            Map<String,DbTypeDTO> hrtauSelect = relDt.stream()
                                    .filter(v->v.get("CLASS_CODE").getObj().equals("HRTAU") &&
                                            v.get("CODE_CODE").equals(positionRow.getTeamCode()))
                                    .findFirst().orElse(null);
                            if(hrtauSelect == null){
                                dbRet = repo.setUserRel(userSid,"HRTAU",positionRow.getTeamCode(),
                                        "0000",positionRow.getTeminalCode(),Util.getStrChk(postnSelect.get("CODE_CODE")),"",""
                                        ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());
                                if(dbRet.getErrFlag().equals("Y")){
                                    throw new BizException("setUserGroup", dbRet.getErrMsg());
                                }
                            }
                        }
                    }
                }
            }

            return okOrThrow("setUserRel", dbRet);
        });
    }

    public ResponseDTO<?> setUserRel(List<InfraUser.UserRelDTO> dtos) {

        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();

        return execute(repo,()->{
            DbDto dbRet = null;

            for(InfraUser.UserRelDTO row: dtos){
                dbRet = repo.setUserRel(row.userSid(),row.classCode(),row.codeCode(),row.yyyy(),row.value1(),row.value2(),row.value3(),row.value4(),info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setUserRel", dbRet.getErrMsg());
                }
            }
            String key = "sess:user:";
            for(InfraUser.UserRelDTO row: dtos){
                String sessionKey = stringRedisTemplate.opsForValue().get(key+row.userSid());
                if (sessionKey == null) {
                    continue;
                }
                ClsUserInfo userProfile = (ClsUserInfo) redisTemplate.opsForHash()
                        .get(sessionKey, "sessionAttr:USER_PROFILE");



                if (userProfile != null) {

                    DbDto dbDto = repo.getUserRel(row.userSid(),"Y","KOR", Util.getGUID(),info.getUserId(),info.getUserIpAddress(),"login");

                    ResponseDTO<Map<Integer, List<Map<String, Object>>>> ret = ResponseDTO.from(dbDto);

                    userProfile.setRelArray(ret.getData().get(0));

                    redisTemplate.opsForHash().put(
                            sessionKey,
                            "sessionAttr:USER_PROFILE",
                            userProfile
                    );
                }
            }

            return okOrThrow("setUserRel", dbRet);
        });

    }

    public ResponseDTO<?> delUserRel(List<InfraUser.UserRelDTO>dtos){
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;

            for(InfraUser.UserRelDTO row: dtos){
                String delFlag = "N";
                if(row.classCode().equals("HRWDT")&&row.codeCode().equals("D")){
                    delFlag = "Y";
                }
                dbRet = repo.delUserRel(row.userSid(),row.classCode(),row.codeCode(),row.yyyy(),delFlag,info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("delUserRel", dbRet.getErrMsg());
                }
            }

            return okOrThrow("delUserRel", dbRet);});
    }

    private void setCellValue(Row row, int index, String value) {

        Cell cell = row.getCell(
                index,
                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
        );

        cell.setCellValue(value == null ? "" : value);
    }

    private void copyRowStyleFast(
            Row sourceRow,
            Row targetRow,
            int cellCount
    ) {

        targetRow.setHeight(sourceRow.getHeight());

        for (int i = 0; i < cellCount; i++) {

            Cell sourceCell = sourceRow.getCell(
                    i,
                    Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
            );

            Cell targetCell = targetRow.createCell(i);

            targetCell.setCellStyle(
                    sourceCell.getCellStyle()
            );
        }
    }
}
