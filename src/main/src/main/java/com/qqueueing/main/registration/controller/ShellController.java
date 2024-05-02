package com.qqueueing.main.registration.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shell")
@Slf4j
public class ShellController {

    @Value("${shell.path}")
    private String PATH;

    @Value("${shell.fileNm}")
    private String fileNm;


    @GetMapping
    public String execShell(@RequestParam String url){
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
                pb.command("cmd.exe", "/c", fileNm, "\"" + url + "\"");
            }
            else
            {
                pb.command("sh", "-c", fileNm, "\"" + url + "\"");
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

    @GetMapping("/exec")
    public void execShell(){

        try {
//            // 스크립트 실행 전 실행 권한을 부여하는 명령어 추가
//            ProcessBuilder pb = new ProcessBuilder();
//            pb.command("chmod 777", PATH + fileNm);
//
//            // 스크립트 실행
//            Process chmodProcess = pb.start();
//            chmodProcess.waitFor(); // 실행이 완료될 때까지 기다림

            // os check
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            System.out.println(":: OS is "+ (isWindows ? "window" : "mac"));

            // Run script
            ProcessBuilder pb = new ProcessBuilder();
            pb.directory(new File(PATH)); // 스크립트가 실행될 디렉토리 설정
            System.out.println(":: execDirectory is "+ PATH);
            if (isWindows)
            {
                pb.command("cmd.exe", "/c", fileNm, "\"url\"");
            }
            else
            {
                pb.command("sh", "-c", fileNm);
            }

            Process process = pb.start();
            process.waitFor();

            // Read output
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 에러 스트림 읽기
            reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // checking the command in list
            System.out.println(":: command: " + pb.command());
            // checking the output
            System.out.println(output.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
