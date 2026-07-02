package com.aact.authservice.service;

import com.aact.authservice.dto.InfraUser;
import com.aact.authservice.dto.LoginReq;
import com.aact.authservice.dto.SetUserDTO;
import com.aact.authservice.repo.UserRepo;
import com.aact.common.*;
import com.aact.common.ServiceBase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService extends ServiceBase {

    private final ObjectProvider<UserRepo> userRepo;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

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

                DbDto relRet = repo.getUserRel(Util.getDecimal(sid),"Y","KOR", Util.getGUID(),dto.username(),hReq.,"login");

                ResponseDTO<Map<Integer, List<Map<String, Object>>>> chgRet = ResponseDTO.from(relRet);

                if(chgRet.getErrFlag().equals("Y")){
                    throw new BizException("login",chgRet.getErrMsg());
                }

                String menuMode = hReq.getHeader("MENU-MODE");

                if(menuMode!=null&&menuMode.equals("INFRA")){
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

    public ResponseDTO<?> getUserInfo(String userId) {
        ClsUserInfo info = UserContext.get();
        UserRepo repo = userRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = repo.getUserInfo(userId, info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getUserInfo", dbRet);
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
            DbDto dbRet = repo.getUserList("AACT","",deptCode,userId,userName,usableFlag,info.getUserLang(), Util.getGUID(), info.getUserId(),
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

            dbRet = repo.setUserInfo(info.getUserId(), pass, passHp, dto.getUserName2(), info.getUserName(),
                    info.getUserCompany(), info.getUserBranch(), info.getUserDepart(), dto.getLangCode(), dto.getEmail(),
                    dto.getPhone(), dto.getMobile(), dto.getFax(), info.getUserTerminalCodeWork(),
                    info.getUserTerminalNameWork(), info.getUserAUTH_WORKTIMELINE_YN(), info.getUserAUTH_BOARD_WRITE_YN(),
                    info.getUserAUTH_IN_CANCEL_YN(), info.getUserAUTH_BOARDHP_WRITE_YN(), info.getUserAUTH_IT_BOARD_YN(),
                    info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setUserInfo", dbRet.getErrMsg());
            }

            info.setUserNameDefault(dto.getUserName2());
            info.setUserLang(dto.getLangCode());
            info.setUserEmail(dto.getEmail());
            info.setUserPhone(dto.getPhone());
            info.setUserMobile(dto.getMobile());
            info.setUserFax(dto.getFax());

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

            String userId = dto.userId();


            if(!dto.userIdChange().isEmpty()){
                dbRet = repo.callSql("SELECT USER_SID FROM TCM_USER_MASTER WHERE USABLE_FLAG = 'Y' AND USER_ID = '"+dto.userIdChange()+"'");
                if(dbRet.getErrFlag().equals("N")){
                    if(!dbRet.getResult().get(0).isEmpty()){
                        throw new BizException("setUserInfoMgm", dto.userIdChange()+"는 이미 사용중입니다.");
                    }else{
                        userId = dto.userIdChange();
                    }

                }else{
                    throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
                }
            }

            dbRet = repo.getUserInfo(info.getUserId(), info.getUserLang(), Util.getGUID(), info.getUserId(),
                    info.getUserIpAddress(), info.getPgmId());

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setUserInfoMgm", dbRet.getErrMsg());
            }



            repo.beginTrans();

            dbRet = repo.setUserInfo(userId, dto.userPass(), dto.userPassHp(), dto.userName1(), dto.userName2(),dto.companyCode(),dto.branchCode(),dto.deptCode(),
                    dto.langCode(),dto.email(),dto.phone(),dto.mobile(),dto.fax(),dto.terminalCode(),dto.terminalName(),dto.workYn(),dto.boardYn(),dto.inYn(),dto.boardHpYn(),dto.itYn(),
                    info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
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

        if (cropW + 50 < cropH) {
            return rotate90(transparent);
        }

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
}
