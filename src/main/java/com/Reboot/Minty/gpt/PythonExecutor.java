package com.Reboot.Minty.gpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class PythonExecutor {

    public List<Map<String, Object>> executePythonScript() throws IOException {
        String pythonScriptPath = "D:/IntelliJPrac/Minty/src/main/python/test.py";

        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
        Process process = processBuilder.start();

        // Python 스크립트의 출력을 읽어옵니다.
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = reader.lines().collect(Collectors.joining());

        // Python 스크립트의 실행 완료까지 대기합니다.
        try {
            int exitCode = process.waitFor();
            System.out.println("Python 스크립트 실행이 완료되었습니다. 종료 코드: " + exitCode);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Python 스크립트의 출력을 처리합니다.
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> data = new ArrayList<>();
        try {
            data = objectMapper.readValue(output, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 추출된 데이터를 반환합니다.
        return data;
    }
}
