package com.bdqn.controller;

import com.alibaba.fastjson.JSONArray;
import com.bdqn.common.*;
import com.bdqn.entity.ItripUser;
import com.bdqn.entity.ItripUserVO;
import com.bdqn.mapper.ItripUserMapper;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

@Controller
@RequestMapping("api")
public class ItripController {

    @Resource
    ItripUserMapper mapper;

    @Resource
    TokenBiz tokenBiz;

    @Resource
    RedisUtil redisUtil;

    @Resource
    ItripUserMapper mapper2;

    @RequestMapping("dologin")
    @ResponseBody
    public Dto doLogin(String name, String password, HttpServletRequest request) {
        ItripUser user = mapper.login(name, password);
        if (user != null) {
            // 模拟session的票据
            String token = tokenBiz.generateToken(request.getHeader("User-Agent"), user);
            // 把这个token存储到redis中
            // fastjson 把当前用户转为字符串
            redisUtil.setRedis(token, JSONArray.toJSONString(user));
            ItripTokenVO obj = new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis() * 3600 * 2, Calendar.getInstance().getTimeInMillis());
            return DtoUtil.returnDataSuccess(obj);
        }
        return DtoUtil.returnFail("登录失败", "1000");
    }

    public void Sentsms(String Phone, String message) {
//生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8a216da87f63aaf1017f6c330e33018d";
        String accountToken = "c140c831efdd4cd89c5d2c6f480ba873";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8a216da87f63aaf1017f6c330f8d0193";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = Phone;
        String templateId = "1";
        String[] datas = {message, "2"};
//        String subAppend="1234";  //可选 扩展码，四位数字 0~9999
//        String reqId="zt";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }
    }


    @RequestMapping(value = "/registerbyphone")
    @ResponseBody
    public Dto Register(@RequestBody ItripUserVO vo) {
        System.err.println(vo);
        ItripUser user = new ItripUser();
        user.setUserCode(vo.getUserCode());
        user.setUserPassWord(vo.getUserPassword());
        user.setUserName(vo.getUserName());
        mapper2.insert(user);
        Random random = new Random();
        int result = random.nextInt(9999);
        Sentsms(vo.getUserCode(), result + "");
        redisUtil.setRedis(vo.getUserCode(), result + "");
        return DtoUtil.returnSuccess();
    }

    @PutMapping("/activate")
    @ResponseBody
    public Dto validatephone(String user, String code) {
        String str = redisUtil.getStr(user);
        if (code.equals(str)) {
            mapper2.updateUser(user);
            return DtoUtil.returnDataSuccess("激活成功");
        } else {
            return DtoUtil.returnFail("验证码错误", "10000");
        }
    }

    @RequestMapping("ckusr")
    @ResponseBody
    public Dto rupdateyanzheng() {
        return DtoUtil.returnSuccess();
    }

    @RequestMapping("doregister")
    @ResponseBody
    public Dto RegisterEmail(@RequestBody ItripUserVO vo) throws Exception {
        ItripUser user = new ItripUser();
        user.setUserCode(vo.getUserCode());
        user.setUserPassWord(vo.getUserPassword());
        user.setUserName(vo.getUserName());
        mapper2.insert(user);
        Random random = new Random();
        int result = random.nextInt(9999);
        App.SenEmail(vo.getUserCode(),result+"");
        redisUtil.setRedis(vo.getUserCode(), result + "");
        return DtoUtil.returnSuccess();
    }
}

