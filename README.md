<br>
![QQueueing](./.asset/queueing.png)  



<div align="left">
    <h1>QQueueing</h1>
</div>

QQueueing은 서버에 직접 설치해서 사용할 수 있는 무료 대기열 서비스입니다. 서버 프로그램이 실행 중인 곳에 설치해서, 추가 비용 없이 대기열 기능을 사용할 수 있습니다.

## Features

- 간편 다운로드 후 사용 가능한 대기열 기능
- 대기열 타깃 url 설정 및 활성/비활성화 기능 제공
- 타깃 url 별로 대기 및 통과 인원 현황 모니터링 화면 제공
- 운영자의 컴퓨팅 자원과 대기열 어플리케이션의 모니터링 지표 제공
- 모바일, PC 호환 지원


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

## User's Guide

<b>대기열 애플리케이션을 동작시킵니다.</b>

<img src="https://lab.ssafy.com/s10-final/S10P31A401/uploads/2951ddee5f70f133d39de917b11ecee2/%EB%8C%80%EA%B8%B0%EC%97%B4_%EC%B2%AB_%ED%99%94%EB%A9%B4.PNG" width="1000" height="500">

<b>등록하기 버튼을 누르고, 대기열을 적용할 URL, 서비스 명, 대기열 대표 이미지를 등록합니다.</b>

<img src="https://lab.ssafy.com/s10-bigdata-recom-sub2/S10P22A706/uploads/0a5b52e80202ae2bfc99a6b59f11b612/%EB%8C%80%EA%B8%B0%EC%97%B4_%EB%93%B1%EB%A1%9D_%ED%99%94%EB%A9%B4.PNG" width="1000" height="500">

<b>대기열 리스트 버튼을 누르고, 대기열이 적용된 모습을 확인합니다.</b>

<img src="https://lab.ssafy.com/s10-final/S10P31A401/uploads/6c1664553082d4e53386b971379b74cc/%EB%8C%80%EA%B8%B0%EC%97%B4_%EB%A6%AC%EC%8A%A4%ED%8A%B8_%ED%99%94%EB%A9%B4.PNG" width="1000" height="500">

<b>등록된 url을 클릭하여 상세 정보를 학인할 수 있고, 활성/비활성화 및 설정을 변경할 수 있습니다. </b>

<img src="https://lab.ssafy.com/s10-bigdata-recom-sub2/S10P22A706/uploads/1032df0f0fdeb394a00428af18b1c95f/url_%EC%83%81%EC%84%B8_%EC%A0%95%EB%B3%B4_%ED%99%94%EB%A9%B4.PNG" width="1000" height="500">

<b>대시보드 버튼을 클릭하여 대기열이 적용된 운영자의 컴퓨팅 자원과, 대기열 어플리케이션의 상태를 모니터링할 수 있습니다.</b>

<img src="https://lab.ssafy.com/s10-bigdata-recom-sub2/S10P22A706/uploads/337ff56068974005c2bb6f73e1649a8e/%EB%8C%80%EC%8B%9C%EB%B3%B4%EB%93%9C_%ED%99%94%EB%A9%B4.PNG" width="1000" height="500">
</br>
<img src="https://lab.ssafy.com/s10-bigdata-recom-sub2/S10P22A706/uploads/494b5ba9a9706ab127ad7000bb75ac17/%EC%9A%B4%EC%98%81%EC%9E%90_%EC%BB%B4%ED%93%A8%ED%8C%85_%EC%9E%90%EC%9B%90_%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81.PNG" width="500" height="250">

<img src="https://lab.ssafy.com/s10-bigdata-recom-sub2/S10P22A706/uploads/9c7cbfe5b00507b70ae92b2d53deb3ff/%EB%8C%80%EA%B8%B0%EC%97%B4_%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98_%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81_%ED%99%94%EB%A9%B4.PNG" width="500" height="250">

## Run Screen

- PC Version
<img src="https://lab.ssafy.com/s10-final/S10P31A401/uploads/c55b304d270db2f7f64b9353b7dc1143/pc_%EB%B2%84%EC%A0%84.PNG" width="500" height="250">

- Mobile Version
<img src="https://lab.ssafy.com/s10-final/S10P31A401/uploads/c5ee0642f278d339a537d5a7efb0dc72/%EB%AA%A8%EB%B0%94%EC%9D%BC_%EB%8C%80%EA%B8%B0%EC%97%B4_%EC%A7%84%EC%9E%85_%ED%99%94%EB%A9%B4.PNG" width="400" height="500">

## Contributing

[CONTRIBUTING](./CONTRIBUTING_KOR.md)에서 코드 기여에 관한 가이던스를 확인하십시오.  
See the [CONTRIBUTING](./CONTRIBUTING.md) for Code Contribution Guidelines.



## License

[LICENSE](./LICENSE)에서 라이센스 저작권과 제한사항을 확인하십시오.  
See the [LICENSE](./LICENSE) for license rights and limitations.
