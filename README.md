<br>
![QQueueing](./.asset/queueing.png)  



<div align="left">
    <h1>QQueueing</h1>
</div>

QQueueing은 서버에 직접 설치해서 사용할 수 있는 무료 대기열 서비스입니다. 서버 프로그램이 실행 중인 곳에 설치해서, 추가 비용 없이 대기열 기능을 사용할 수 있습니다.

## Features

- 간편 설정 후 사용 가능한 대기열 기능
- 서버의 CPU사용량, 메모리, API 트래픽 모니터링
- 서버의 허용 트래픽량 테스트, 스케일링을 위한 예상수치 제공


## Getting Started

사전 준비 사항  
- 도커(버전 무관..?)
- 도커 컴포즈(2.17이상)
- 파이썬3(3.8이상)

중요 알림
- 현재 오픈소스는 nginx 리버스 프록시 서버를 띄우고 있는 상황에서 동작합니다.
- nginx 서버는 컨테이너 런타임을 통해 실행되고 있어야 합니다.
- 기본 웹 통신인 80,443 포트를 사용하고 있는 상황에서 동작합니다.
- 추후 개발을 통해 확장성을 갖출 예정입니다.

다운로드  
- 깃을 클론 받습니다.
- 모든 실행은 qqueueing.sh를 통해 가능합니다.
- `./qqueueing.sh install`을 입력하면 대기열 시스템 초기 설정을 진행할 수 있습니다.
  - 이때 입력된 사항은 .env 파일로 저장되며, 이 파일을 직접적으로 수정할 수도 있습니다.
  - .env 파일을 수정하면 반드시 다시 install을 진행해주십시오.
- 설정이 완료되면 `./qqueueing.sh start`를 통해 애플리케이션을 동작시킬 수 있습니다.
- 모든 애플리케이션이 실행된다면 .env 파일에 명시된 포트(기본 3001)로 어드민 페이지에 접근하여 설정을 진행할 수 있습니다.
- 이 프로젝트를 사용하고 싶지 않을 때는 `./qqueueing.sh stop`을 입력해주십시오 

(다운로드 ~ 설치 부분 생략, 실행부터)

<b>starter.exe 파일을 실행합니다.</b>

![alt text](image.png)
(설정 화면 이미지)

<b>대기열을 적용할 URL, 대기열 수용 인원, 1분당 처리 가능 인원 예상치를 입력합니다.</b>

![alt text](image.png)
(입력한 화면 이미지)

<b>'대기열 활성화' 버튼을 누르고, 대기열이 적용된 모습을 확인합니다.</b>

![alt text](image.png)
(대기열 화면 이미지)

<b>대기열이 적용된 요청의 트래픽량, 서버의 메모리 사용량 등을 모니터링할 수 있습니다.</b>

![alt text](image.png)

## Contributing

[CONTRIBUTING](./CONTRIBUTING_KOR.md)에서 코드 기여에 관한 가이던스를 확인하십시오.  
See the [CONTRIBUTING](./CONTRIBUTING.md) for Code Contribution Guidelines.



## License

[LICENSE](./LICENSE)에서 라이센스 저작권과 제한사항을 확인하십시오.  
See the [LICENSE](./LICENSE) for license rights and limitations.
