package com.Reboot.Minty.gpt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class lowestController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/priceSearch")
    public String priceSearch(){
        return "gpt/lowest";
    }

    @GetMapping("/lowest")
    @ResponseBody
    public List<Map<String, Object>> gpt(@RequestParam(name = "inputValue", required = false) String inputValue, Model model) {
        List<Map<String, Object>> results = new ArrayList<>();

        if (inputValue != null && !inputValue.isEmpty()) {
            String[] keywords = inputValue.split("\\s+");
            StringBuilder queryBuilder = new StringBuilder("SELECT id, title, price FROM tradeboard WHERE 1=1");

            List<Object> queryParams = new ArrayList<>();

            for (String keyword : keywords) {
                queryBuilder.append(" AND LOWER(title) LIKE ?");
                queryParams.add("%" + keyword.toLowerCase() + "%");
            }

            queryBuilder.append(" ORDER BY created_date DESC LIMIT 5");

            String query = queryBuilder.toString();
            results = jdbcTemplate.queryForList(query, queryParams.toArray());

            for (Map<String, Object> result : results) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    result.put(key.toLowerCase(), value);
                }
            }
        }
        return results;
    }


    @GetMapping("/gpt/getPriceData")
    @ResponseBody
    public List<Map<String, Object>> getPriceData(HttpSession session) {
        List<Map<String, Object>> priceData = (List<Map<String, Object>>) session.getAttribute("priceData");
        return priceData;
    }

    @GetMapping("/gpt/executePythonScript")
    @ResponseBody
    public void executePythonScript(@RequestParam("inputValue") String inputValue, HttpSession session) {
        String pythonScriptPath = "D:/intellijPrac/Minty/src/main/python/test.py";

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, inputValue);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String output = reader.lines().collect(Collectors.joining());

            int exitCode = process.waitFor();
            System.out.println("Python script execution completed. Exit code: " + exitCode);

            ObjectMapper objectMapper = new ObjectMapper();
            List<List<String>> data = objectMapper.readValue(output, new TypeReference<List<List<String>>>() {});

            List<Map<String, Object>> convertedData = new ArrayList<>();
            for (List<String> item : data) {
                Map<String, Object> mapItem = new LinkedHashMap<>();
                mapItem.put("title", item.get(0));
                mapItem.put("price", item.get(1));
                convertedData.add(mapItem);
            }

            session.setAttribute("priceData", convertedData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}


