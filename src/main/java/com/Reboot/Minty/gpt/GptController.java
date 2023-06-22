package com.Reboot.Minty.gpt;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GptController {
    @Autowired
    PythonExecutor pythonExecutor;
    @Autowired
    DeletePythonExecuter deletePythonExecuter;
    @Autowired
    SaveNamePythonExecuter saveNamePythonExecuter;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserService userService;

    @GetMapping("/lowest")
    public String gpt(@RequestParam(name = "search", required = false) String searchQuery, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        int level = user.getLevel();
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String[] keywords = searchQuery.split("\\s+");  // 입력된 검색어를 공백으로 분리하여 공백있어도 검색가능하게 만듬
            StringBuilder queryBuilder = new StringBuilder("SELECT name, price FROM data WHERE 1=1");
            List<Object> queryParams = new ArrayList<>();

            for (String keyword : keywords) {
                queryBuilder.append(" AND LOWER(name) LIKE ?");
                queryParams.add("%" + keyword.toLowerCase() + "%");
            }

            String query = queryBuilder.toString();
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query, queryParams.toArray());

            // 결과 맵의 속성 이름을 대소문자 구분 없이 검색
            List<Map<String, Object>> convertedResults = new ArrayList<>();
            for (Map<String, Object> result : results) {
                Map<String, Object> convertedResult = new LinkedHashMap<>(result.size());
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    convertedResult.put(key.toLowerCase(), value);
                }
                convertedResults.add(convertedResult);
            }

            model.addAttribute("results", convertedResults);
        }
        model.addAttribute("level", level);
        return "gpt/lowest";
    }

    //가격비교 검색
    @GetMapping("/pytest")
    public String pytest(Model model) {
        try {
            pythonExecutor.executePythonScript();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/lowest";
    }

    //물품 이름 저장
    @GetMapping("/saveName")
    public String saveName(){
        try {
            saveNamePythonExecuter.executePythonScript();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/lowest";
    }

    //가격비교정보 삭제
    @GetMapping("/delete/lowest")
    public String deleteLowestPrice(Model model) {
        try{
            deletePythonExecuter.executePythonScript();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/lowest";
    }
}
