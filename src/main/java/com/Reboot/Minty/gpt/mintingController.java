package com.Reboot.Minty.gpt;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

@Controller
public class mintingController {

    @Autowired
    UserService userService;

    @GetMapping("/chat")
    public String chat(){
        return "gpt/chat";
    }

    @GetMapping("/minting")
    @ResponseBody
    public void executeGpt(HttpSession session, HttpServletRequest request, @Value("${gpt-api-key}") String apiKey) {
        String pythonScriptPath = "D:/intellijPrac/Minty/src/main/python/chatGpt.py";

        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        String nickname = user.getNickName();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, nickname, apiKey);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            OutputStream outputStream = process.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write("data\n");  // Sending "data" command to the Python script
            writer.flush();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println("Python script execution completed. Exit code: " + exitCode);

            String[] data = output.toString().split("\n");
            for (String value : data) {
                // Do something with each data value
                System.out.println(value);
            }

            // session.setAttribute("gptOutput", output.toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
