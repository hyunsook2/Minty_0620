package com.Reboot.Minty.member.controller;

import com.Reboot.Minty.manager.service.TwilioService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
public class PaymentController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private TwilioService twilioService;

    @Value("${payment.secretKey}")
    private String secretKey;
    @Value("${payment.clientKey}")
    private String clientKey;
    @GetMapping(value = "success")
    public String paymentResult(
            Model model, HttpServletRequest request,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) throws Exception {


        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;
        model.addAttribute("isSuccess", isSuccess);

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        model.addAttribute("responseStr", jsonObject.toJSONString());
        System.out.println(jsonObject.toJSONString());

        model.addAttribute("method", (String) jsonObject.get("method"));
        model.addAttribute("orderName", (String) jsonObject.get("orderName"));

        if (((String) jsonObject.get("method")) != null) {
            if (((String) jsonObject.get("method")).equals("카드")) {
                model.addAttribute("cardNumber", (String) ((JSONObject) jsonObject.get("card")).get("number"));
            } else if (((String) jsonObject.get("method")).equals("가상계좌")) {
                model.addAttribute("accountNumber", (String) ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
            } else if (((String) jsonObject.get("method")).equals("계좌이체")) {
                model.addAttribute("bank", (String) ((JSONObject) jsonObject.get("transfer")).get("bank"));
            } else if (((String) jsonObject.get("method")).equals("휴대폰")) {
                model.addAttribute("customerMobilePhone", (String) ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
            }
        } else {
            model.addAttribute("code", (String) jsonObject.get("code"));
            model.addAttribute("message", (String) jsonObject.get("message"));
        }

        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        userService.updateBalance(userEmail, amount);

        User user = userService.getUserInfo(userEmail);
        String nickname = user.getNickName();
        String mobile = user.getMobile();
        int balance = user.getBalance();
        System.out.println(nickname);
        System.out.println(mobile);
        System.out.println(balance);

        // mobile 번호 형식 변경
        if (mobile.startsWith("010")) {
            mobile = "+82" + mobile.substring(1);
        }

        // SMS 보내는 코드
        String message = "[Minty]\n" + nickname + " 님 " + amount + " 원 충전이 완료되었습니다.\n총 잔고 " + balance + " 원.";
        //twilioService.sendSms(mobile, message);

        return "redirect:/member/myPage";
    }

    @GetMapping(value = "fail")
    public String paymentResult(
            Model model,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "code") Integer code
    ) throws Exception {

        model.addAttribute("code", code);
        model.addAttribute("message", message);

        return "pay/fail";
    }

    //main.html 에서 충전하기 누르면 이동
    @GetMapping(value = "charge")
    public String charge(Model model) {
        model.addAttribute("clientKey", clientKey);
        return "pay/pay";
    }

    // 출금하기 페이지 이동
    @GetMapping(value = "wthdr")
    public String wthdr(Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail"); // userEmail 값을 가져옵니다.
        User user = userRepository.findByEmail(userEmail);

        model.addAttribute("user", user);

        return "pay/wthdr";
    }

    @PostMapping("/withdraw")
    public String withdrawBalance(@RequestParam("amount") Integer amount, HttpSession session) {
        try {
            String userEmail = (String) session.getAttribute("userEmail"); // userEmail 값 가져오기

            System.out.println(userEmail);
            System.out.println(amount);
            userService.wthdrBalance(userEmail, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/member/myPage";
    }

}