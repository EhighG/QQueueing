package io.gatling.demo;

import java.time.Duration;
import java.util.*;

import io.gatling.core.body.RawFileBody;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class RecordedSimulation extends Simulation {
  // How to run : ./mvnw.cmd gatling:test -> input 1

  private final String TEST_TOKEN = "testTokenStringValue";
  private final int POLLING_REQ_CNT = 50;

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("https://k10a401.p.ssafy.io")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
  
  private Map<CharSequence, String> headers_0 = Map.ofEntries(
    Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"),
    Map.entry("Cache-Control", "max-age=0"),
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("Sec-Fetch-User", "?1"),
    Map.entry("Upgrade-Insecure-Requests", "1"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );
  
  private Map<CharSequence, String> headers_1 = Map.ofEntries(
    Map.entry("Accept", "text/css,*/*;q=0.1"),
    Map.entry("Sec-Fetch-Dest", "style"),
    Map.entry("Sec-Fetch-Mode", "no-cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );
  
  private Map<CharSequence, String> headers_2 = Map.ofEntries(
    Map.entry("Sec-Fetch-Dest", "script"),
    Map.entry("Sec-Fetch-Mode", "no-cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );
  
  private Map<CharSequence, String> headers_16 = Map.ofEntries(
    Map.entry("Accept", "application/json, text/plain, */*"),
    Map.entry("Content-Type", "application/json; charset=UTF-8"),
    Map.entry("Origin", "https://k10a401.p.ssafy.io"),
    Map.entry("Sec-Fetch-Dest", "empty"),
    Map.entry("Sec-Fetch-Mode", "cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("Target-URL", "https://k10a401.p.ssafy.io/product/1"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );
  
  private Map<CharSequence, String> headers_17 = Map.ofEntries(
    Map.entry("Accept", "application/json, text/plain, */*"),
    Map.entry("Content-Type", "application/json; charset=UTF-8"),
    Map.entry("Origin", "https://k10a401.p.ssafy.io"),
    Map.entry("Sec-Fetch-Dest", "empty"),
    Map.entry("Sec-Fetch-Mode", "cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );
  
  private Map<CharSequence, String> headers_18 = Map.ofEntries(
    Map.entry("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8"),
    Map.entry("Sec-Fetch-Dest", "image"),
    Map.entry("Sec-Fetch-Mode", "no-cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );
  
  private Map<CharSequence, String> headers_23 = Map.ofEntries(
    Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"),
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("Upgrade-Insecure-Requests", "1"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );
  
  private Map<CharSequence, String> headers_24 = Map.ofEntries(
    Map.entry("Next-Router-Prefetch", "1"),
    Map.entry("Next-Router-State-Tree", "%5B%22%22%2C%7B%22children%22%3A%5B%22product%22%2C%7B%22children%22%3A%5B%5B%22id%22%2C%221%22%2C%22d%22%5D%2C%7B%22children%22%3A%5B%22__PAGE__%22%2C%7B%7D%2C%22%2FqqueueingAPI%2Fwaiting%2Fpage-req%3Ftoken%3DlWOWiazvKy3PtsyBQgOA%22%2C%22refresh%22%5D%7D%5D%7D%5D%7D%2Cnull%2Cnull%2Ctrue%5D"),
    Map.entry("Next-Url", "/product/1"),
    Map.entry("RSC", "1"),
    Map.entry("Sec-Fetch-Dest", "empty"),
    Map.entry("Sec-Fetch-Mode", "cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\""),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "\"Windows\"")
  );


  private ScenarioBuilder scn = scenario("RecordedSimulation")
    .exec(
            // target page req(init)
      http("initialPageReq")
        .get("/product/1")
        .headers(headers_0)
        .resources(
          http("resource_1")
            .get("/waiting/_next/static/css/5ab05d5e6c3900c7.css")
            .headers(headers_1),
          http("resource_2")
            .get("/waiting/_next/static/chunks/webpack-1214e4bd8911037e.js")
            .headers(headers_2),
          http("resource_3")
            .get("/waiting/_next/static/chunks/main-app-074144f751678bfb.js")
            .headers(headers_2),
          http("resource_4")
            .get("/waiting/_next/static/chunks/fd9d1056-84ae4bb5d973de0d.js")
            .headers(headers_2),
          http("resource_5")
            .get("/waiting/_next/static/chunks/23-8d8c56c513082ab9.js")
            .headers(headers_2),
          http("resource_6")
            .get("/waiting/_next/static/chunks/932-39197822ccb23872.js")
            .headers(headers_2),
          http("resource_7")
            .get("/waiting/_next/static/chunks/41ade5dc-cf0261f5e919d330.js")
            .headers(headers_2),
          http("resource_8")
            .get("/waiting/_next/static/chunks/405-283d170337534494.js")
            .headers(headers_2),
          http("resource_9")
            .get("/waiting/_next/static/chunks/681-5723c9bf092a9057.js")
            .headers(headers_2),
          http("resource_10")
            .get("/waiting/_next/static/chunks/381-372d3069c487c8b3.js")
            .headers(headers_2),
          http("resource_11")
            .get("/waiting/_next/static/chunks/850-d1d0496122b8b001.js")
            .headers(headers_2),
          http("resource_12")
            .get("/waiting/_next/static/chunks/app/(waiting)/waiting/page-36d79ba0b355645d.js")
            .headers(headers_2),
          http("resource_13")
            .get("/waiting/_next/static/chunks/17-de56114266b18982.js")
            .headers(headers_2),
          http("resource_14")
            .get("/waiting/_next/static/chunks/739-12480a1e7e58c1e1.js")
            .headers(headers_2),
          http("resource_15")
            .get("/waiting/_next/static/chunks/9-44dd672eca12265a.js")
            .headers(headers_2),
          // enqueue
          http("Enqueue")
            .post("/qqueueingAPI/waiting")
            .headers(headers_16)
            .body(RawFileBody("0016_request.json")),
                http("request_18")
                        .get("/waiting/_next/image?url=%2Fwaiting%2F_next%2Fstatic%2Fmedia%2Fcats.aab00cf1.png&w=1080&q=75")
                        .headers(headers_18),
                http("request_19")
                        .get("/waiting/_next/image?url=%2Fwaiting%2F_next%2Fstatic%2Fmedia%2Fq_logo.fbf5d9ee.png&w=1080&q=75")
                        .headers(headers_18)
        ),
      // repeat polling
            repeat(POLLING_REQ_CNT).on(
                    http("getMyOrder-polling")
                    .post("/qqueueingAPI/waiting/order")
                    .headers(headers_17)
                    .body(RawFileBody("0020_request.json")),
                    pause(1)
            ),
            // polling end
            http("end-polling(=get-token)")
                    .post("/qqueueingAPI/waiting/order")
                    .headers(headers_17)
                    .body(RawFileBody("0020_request.json")),
      pause(1),
      // request target page using test token
            http("target-page-req")
                    .get("/qqueueingAPI/waiting/page-req?token=" + TEST_TOKEN)
                    .headers(headers_23),
            http("resource_24")
                    .get("/?_rsc=72931")
                    .headers(headers_24)
    );


  {
	  setUp(scn.injectOpen(
              stressPeakUsers(1000).during(5) // 1000번의 요청을 5초동안 나눠서 보냄
//              nothingFor(4), // 4초간 정지
//              atOnceUsers(10), // 10명의 유저가 동시에 요청
//              rampUsers(10).during(5), // 유저를 10명으로 점진적으로 5초간 증가
//              constantUsersPerSec(20).during(15), // 초당 20명의 유저가 15초 동안 요청
//              constantUsersPerSec(20).during(15).randomized(), // 랜덤한 간격으로 (초당 20명의 유저가 15초 동안 요청)하는 것처럼 처리
//              rampUsersPerSec(10).to(20).during(10), // (10초 duration)초당 10명의 유저에서 초당 20명의 유저가 요청하는 형태로 균일하게 증가하게 진행
//              rampUsersPerSec(10).to(20).during(10).randomized(), // (10초 duration)초당 10명의 유저에서 초당 20명의 유저가 요청하는 형태로 비균등하게 증가하게 진행
              )).protocols(httpProtocol);
  }
}
