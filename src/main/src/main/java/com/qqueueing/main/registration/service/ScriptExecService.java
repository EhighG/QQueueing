package com.qqueueing.main.registration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ScriptExecService {
    @Value("${shell.path}")
    private String PATH;

    @Value("${shell.fileNm}")
    private String fileNm;

    public String execShell(String url, String param){
        try {
            // os check
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            System.out.println(":: OS is "+ (isWindows ? "window" : "mac"));

            // Run script
            ProcessBuilder pb = new ProcessBuilder();
            pb.directory(new File(PATH)); // 스크립트가 실행될 디렉토리 설정
            System.out.println(":: execDirectory is "+ PATH);
            if (isWindows)
            {
                String tmp;
                if (param.equals("register")) {
                    tmp = "echo 'bash " + fileNm + " register " + url + "' > pipe";
                } else if (param.equals("delete")) {
                    tmp = "echo 'bash " + fileNm + " delete " + url + "' > pipe";
                } else {
                    throw new IllegalArgumentException("Invalid requestParam value");
                }
                System.out.println(tmp);
                pb.command("cmd.exe", "/c", tmp);
            }
            else
            {
                String tmp;
                if (param.equals("register")) {
                    tmp = "echo 'bash " + fileNm + " register " + url + "' > pipe";
                } else if (param.equals("delete")) {
                    tmp = "echo 'bash " + fileNm + " delete " + url + "' > pipe";
                } else {
                    throw new IllegalArgumentException("Invalid requestParam value");
                }
                System.out.println(tmp);
                pb.command("bash", "-c", tmp);
                //pb.command("bash",  fileNm,  url);
                //pb.command("echo", "\"", "bash", fileNm, url, "\"", ">", "pipe");
            }

            Process process = pb.start();

            // 실행 결과를 읽어옴
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 프로세스 종료 대기
            int exitCode = process.waitFor();

            // checking the command in list
            System.out.println(":: command: " + pb.command());
            // checking the output
            System.out.println(output.toString());


            if (exitCode == 0) {
                // 성공적으로 실행됨
                return "API request success.";
            } else {
                // 오류 발생
                return "API request fail.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "API request fail.";
        }
    }
}
