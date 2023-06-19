package com.Reboot.Minty.member.oauth2;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String ageRange;
    private String mobile;
    private String gender;

    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String ageRange, String mobile, String gender) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.ageRange = ageRange;
        this.mobile= mobile;
        this.gender = gender;
    }

    public OAuthAttributes() {
    }

    // 해당 로그인인 서비스가 kakao인지 google인지 구분하여, 알맞게 매핑을 해주도록 합니다.
    // 여기서 registrationId는 OAuth2 로그인을 처리한 서비스 명("google","kakao","naver"..)이 되고,
    // userNameAttributeName은 해당 서비스의 map의 키값이 되는 값이됩니다. {google="sub", kakao="id", naver="response"}
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if (registrationId.equals("kakao")) {
            System.out.println(attributes);
            return ofKakao(userNameAttributeName, attributes);
        } else return ofNaver(userNameAttributeName,attributes);
    }

    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");  // 카카오로 받은 데이터에서 계정 정보가 담긴 kakao_account 값을 꺼낸다.

        return new OAuthAttributes(attributes, userNameAttributeName,
                (String) kakao_account.get("name"),
                (String) kakao_account.get("email"),
                (String) kakao_account.get("age_range"),
                (String) kakao_account.get("phone_number"),
                (String) kakao_account.get("gender"));
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response"); // 네이버에서 받은 데이터에서 프로필 정보가 담긴 response 값을 꺼냅니다.

        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) response.get("name"),
                (String) response.get("email"),
                (String) response.get("age"),
                (String) response.get("mobile"),
                (String) response.get("gender"));
    }


}