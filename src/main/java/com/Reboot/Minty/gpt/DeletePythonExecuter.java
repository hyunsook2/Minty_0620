package com.Reboot.Minty.gpt;

import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
public class DeletePythonExecuter {

    public static void executePythonScript() throws IOException {
        String pythonScriptPath = "D:/IntelliJPrac/Minty/src/main/python/deletePrice.py";

        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
        Process process = processBuilder.start();

        // Python 스크립트의 출력을 읽어옵니다.
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Python 스크립트의 실행 완료까지 대기합니다.
        try {
            int exitCode = process.waitFor();
            System.out.println("Python 스크립트 실행이 완료되었습니다. 종료 코드: " + exitCode);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
