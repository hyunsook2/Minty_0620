package com.Reboot.Minty.support.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailAuthService implements EmailService{
    @Autowired
    private JavaMailSender mailSender;
    private int authNumber;     // 난수 발생

    public void makeRandomNumber() {
        // 난수의 범위 111111 ~ 999999 (6자리 난수)
        Random r = new Random();
        int checkNum = r.nextInt(888888) + 111111;
        System.out.println("인증번호 : " + checkNum);
        authNumber = checkNum;
    }


    //이메일 양식
    public String joinEmail(String email) {
        makeRandomNumber();
        String setFrom = "tjddntjr9897@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "Minty 회원 인증메일입니다."; // 이메일 제목
        String content =
                "Minty 회원복구 인증메일입니다." +    //html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br><br>" +
                        "인증번호 확인 페이지로 돌아가 회원복구를 마치시기 바랍니다.";   // 이메일 내용
        System.out.println("메일 데이터 : " + title);
        System.out.println("메일 데이터 : " + content);
        System.out.println("메일 데이터 : " + setFrom);

        mailSend(setFrom, toMail, title, content);

        return Integer.toString(authNumber);
    }

    //이메일 전송 메소드
    public void mailSend(String setFrom, String toMail, String title, String content)  {
        System.out.println("mailSend 확인 완료");
        MimeMessage mailMessage = mailSender.createMimeMessage();
        try {
            mailMessage.addRecipients(MimeMessage.RecipientType.TO, toMail);
            mailMessage.setSubject(title);
            mailMessage.setFrom(setFrom);
            mailMessage.setText(content, "utf-8", "html");
            System.out.println("mailSend 메일 보내기 1초 전");
            mailSender.send(mailMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sendSimpleMessage(String to) throws Exception {
        return null;
    }
}
