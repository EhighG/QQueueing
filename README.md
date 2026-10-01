<br>

![QQueueing](./.asset/queueing.png)

<br>

<div align="left">
    <h1>QQueueing</h1>
</div>

QQueueing은 서버에 직접 설치해서 사용할 수 있는 무료 대기열 서비스입니다. 서버 프로그램이 실행 중인 곳에 설치해서, 추가 비용 없이 대기열 기능을 사용할 수 있습니다.

지금 이 저장소는 로컬 PC(Windows + Docker Desktop)에서 QQueueing과 데모 사이트(대기열을 걸어 볼 예제 쇼핑몰)를 함께 띄우는 구성을 기준으로 합니다. 운영 서버의 nginx에 붙이는 설치 방법은 아직 정리되지 않았습니다.

<br>

## Features

- 소스코드 변경 없이 다운로드를 통해 적용 가능한 대기열 기능
- 대기열 타깃 url 설정 및 활성/비활성화 기능 제공
- 타깃 url 별로 대기 및 통과 인원 현황 모니터링 화면 제공
- 운영자의 컴퓨팅 자원과 대기열 어플리케이션의 모니터링 지표 제공
- 모바일, PC 호환 지원

<br>

## Getting Started

### 준비물

- Windows와 Docker Desktop(WSL 2 기반). Docker Compose 2.20 이상이 필요합니다(Docker Desktop 4.22 이상에 들어 있습니다). `docker compose version`으로 확인합니다.
  - Windows 11, Docker Desktop 4.66.1(Engine 29.3.1, Compose v5.1.0)에서 확인했습니다.
- Git
- 비어 있는 호스트 포트
  - **80, 443**: 데모 사이트의 nginx가 씁니다. 대기를 통과한 요청을 main이 `host.docker.internal:80`으로 다시 보내므로 바꿀 수 없습니다. IIS나 다른 웹 서버, 다른 compose 프로젝트가 쓰고 있으면 먼저 끕니다.
  - 3001, 3002, 3003, 6379, 8081, 8100, 27017: 기본값입니다. 겹치면 `.env`에서 바꿉니다(아래 "설정" 참고).

PowerShell에서 80·443 포트를 확인하는 방법입니다. 아무것도 출력되지 않으면 비어 있습니다.

```powershell
Get-NetTCPConnection -State Listen -LocalPort 80,443 -ErrorAction SilentlyContinue
```

> [!WARNING]
> 이 구성은 로컬 개발·시연용입니다. URL 등록 에이전트(`qqueueing-agent`)는 `/var/run/docker.sock`을 마운트해 데모 nginx 컨테이너의 설정을 고칩니다. docker.sock에 접근할 수 있으면 Docker 호스트의 root 권한을 가진 것과 같습니다. 계정 기본값(Grafana `admin`/`admin`, MongoDB `root`/`example` 등)도 로컬용입니다. 외부에 공개된 서버에 이 구성을 그대로 띄우지 않습니다.

### 실행

아래 명령은 PowerShell과 Git Bash에서 똑같이 씁니다. `docker compose` 명령은 모두 저장소 루트에서 실행합니다.

```bash
git clone https://github.com/EhighG/QQueueing.git qqueueing
cd qqueueing
cp .env.example .env
docker compose run --rm demo-cert
docker compose up -d --build
```

1. `cp .env.example .env`: 설정 파일을 만듭니다. 기본값을 그대로 쓴다면 복사만 하면 됩니다(`.env`가 없어도 같은 기본값으로 뜹니다). `.env`는 커밋하지 않습니다.
2. `docker compose run --rm demo-cert`: 데모 nginx가 쓸 자체 서명 인증서(`localhost`, `127.0.0.1`, `::1`용, 유효기간 365일)를 `demo/nginx/cert/`에 만듭니다. 처음 한 번만 하면 되고, 인증서가 이미 있으면 건너뜁니다. 브라우저 경고 없이 쓰려면 아래 "인증서 경고와 mkcert"를 봅니다.
3. `docker compose up -d --build`: 이미지를 빌드하고 서비스 12개를 띄웁니다. 처음에는 Gradle·npm·apt 다운로드 때문에 10분 넘게 걸릴 수 있습니다.

`up`이 끝난 뒤에도 main이 뜨고 데모 nginx에 초기 설정이 들어가기까지 1~2분이 더 걸립니다. 에이전트 로그에 `demo-nginx 초기 설정 완료`가 나오면 준비된 것입니다. 로그 보기는 Ctrl+C로 끝냅니다.

```bash
docker compose logs -f qqueueing-agent
```

그 전에 `host not found in upstream "qqueueing-main"`와 `demo-nginx 초기 설정 실패. 5초 뒤 다시 시도한다.`가 여러 번 나오는 것은 main이 아직 뜨는 중이라서이며 정상입니다.

### 접속 주소

| 대상 | 주소 | 비고 |
|---|---|---|
| 데모 사이트 | https://localhost | `http://localhost`로 들어오면 https로 보냅니다 |
| 관리자 화면 | http://localhost:3001 | 대기열 등록·관리, 대시보드 |
| Grafana | http://localhost:3002 | 계정 `admin` / `admin` |
| Prometheus | http://localhost:3003 | |
| main API | http://localhost:8081 | 상태 확인 `/monitoring/health` |

node-exporter(8100), MongoDB(27017), Redis(6379)도 호스트에 열립니다. `.env`에서 포트를 바꿨다면 바꾼 값으로 접속합니다.

### 인증서 경고와 mkcert

`demo-cert`로 만든 인증서는 자체 서명이라 브라우저가 경고를 띄웁니다.

1. 브라우저에서 `https://localhost`를 열고, 경고 화면에서 "고급"을 눌러 localhost로 계속 진행합니다.
2. 이 수락은 관리자 화면을 쓰기 전에 해 둡니다. 관리자 화면(`http://localhost:3001`)은 API를 `https://localhost/qqueueingAPI`로 부르기 때문에, 수락하지 않으면 대기열 목록과 등록이 실패합니다. 이때 Chrome·Edge 개발자 도구 콘솔에는 `net::ERR_CERT_AUTHORITY_INVALID`가 찍힙니다.

경고 없이 쓰려면 [mkcert](https://github.com/FiloSottile/mkcert)로 이 PC가 신뢰하는 인증서를 만들어 같은 파일 이름으로 둡니다. `demo-cert`는 인증서가 이미 있으면 건너뛰므로 mkcert 인증서를 덮어쓰지 않습니다. `choco install`은 관리자 권한 PowerShell에서 실행하고, 나머지는 저장소 루트에서 실행합니다.

```powershell
choco install mkcert            # 또는: scoop bucket add extras; scoop install mkcert
mkcert -install                 # 로컬 인증 기관을 Windows 인증서 저장소에 등록합니다. 확인 창이 뜨면 허용합니다
mkdir demo/nginx/cert           # 폴더가 이미 있으면 이 줄은 건너뜁니다
mkcert -cert-file demo/nginx/cert/fullchain.pem -key-file demo/nginx/cert/privkey.pem localhost 127.0.0.1 ::1
docker compose restart demo-nginx   # 이미 띄워 둔 상태라면 새 인증서를 반영합니다
```

mkcert는 Windows에서 Firefox의 인증서 저장소를 지원하지 않으므로 Chrome이나 Edge를 씁니다.

### 대기열 걸어 보기

데모 사이트의 상품 페이지 `https://localhost/product/1`에 대기열을 걸어 봅니다. 먼저 위의 "인증서 경고와 mkcert"대로 `https://localhost` 인증서를 수락해 둡니다(mkcert 인증서를 쓴다면 필요 없습니다).

1. 관리자 화면(`http://localhost:3001`) 왼쪽 메뉴에서 "등록 하기"를 누릅니다.
2. "대기열 등록 대상 URL"에 `https://localhost/product/1`, "서비스 명"에 아무 이름(예: `demo`)을 넣고 "등록"을 누릅니다. 대표 이미지는 넣지 않아도 됩니다. 등록하면 대기열이 바로 활성 상태가 됩니다.
3. 에이전트 로그에 `register https://localhost/product/1 완료`가 나왔는지 봅니다.
   ```bash
   docker compose logs qqueueing-agent
   ```
4. 새 탭에서 `https://localhost/product/1`을 엽니다. "접속 대기 중" 대기 페이지가 뜨고, 차례가 되면 원래 상품 페이지가 보입니다. 혼자 접속하면 대기 페이지는 잠깐 보였다가 바로 넘어갑니다. 통과한 뒤 주소창에는 `/qqueueingAPI/waiting/page-req?token=...`이 보입니다(main이 원래 페이지를 가져와 보여 주기 때문입니다).
5. 왼쪽 메뉴 "대기열 리스트"에서 등록한 URL을 누르면 상세 화면이 열립니다. "현재 대기 중 인원"은 지금 줄에 있는 대기자 수이고, "입장 인원"은 지금까지 입장한 누적 인원입니다(4에서 통과했다면 1 이상입니다). "비 활성"을 누르면 대기 없이 바로 들어갑니다. 이때 줄에서 기다리던 대기자도 모두 입장하므로, 열려 있던 대기 페이지는 다음 순번 조회에서 원래 페이지로 넘어갑니다. 비활성 대기열을 다시 "활성"으로 바꾸면 빈 줄에서 새로 시작하고 "입장 인원"도 0부터 다시 셉니다. "삭제"를 누르면 데모 nginx에서 설정이 빠져 원래 페이지로 바로 들어갑니다.

등록 URL 규칙은 다음과 같습니다.

- `http(s)://호스트/경로` 형식만 받습니다. 호스트는 데모 nginx의 `server_name`인 `localhost`여야 하고, 포트를 붙일 수 없습니다(`https://localhost:443/...`도 안 됩니다).
- 공백, `;`, `{`, `}`, 따옴표, `$`, `#`, `\` 같은 문자는 쓸 수 없습니다.
- 형식이 틀렸거나 호스트가 `localhost`가 아닌 URL도 관리자 화면 목록에는 들어가지만 데모 nginx에는 반영되지 않습니다. 에이전트 로그에 `형식에 맞지 않는 입력이라 실행하지 않고 버린다`나 `no server block that listens 443 with server_name ...`이 남습니다. 이런 등록은 상세 화면에서 삭제하고 다시 등록합니다.
- 경로가 데모 nginx에 이미 있는 경로(`/`, `/api`, `/waiting`, `/qqueueingAPI`)와 같으면 안 됩니다. 이런 URL은 nginx 설정 검사(`duplicate location`)에서 걸려 반영되지 않지만, 목록에서 삭제하면 같은 경로의 원래 `location`이 빠져 데모 사이트가 깨집니다. 잘못 등록했다면 삭제하지 말고 `docker compose down` 후 `docker compose up -d`로 초기화합니다(등록한 대기열이 모두 사라집니다). 이미 삭제해서 깨졌을 때도 같은 방법으로 되돌립니다.

### 모니터링 보기

- 관리자 화면의 "대시보드"(`http://localhost:3001/dashboard`): 호스트 자원(CPU·메모리·디스크)과 main의 상태를 봅니다.
- Grafana(`http://localhost:3002`): `admin` / `admin`으로 로그인합니다. 비밀번호 변경 화면이 뜨면 Skip을 누릅니다. Dashboards의 QQueueing 폴더에 대시보드 2개가 들어 있습니다.
  - 호스트 자원 (node-exporter): http://localhost:3002/d/qqueueing-node
  - QQueueing main (Spring Boot): http://localhost:3002/d/qqueueing-main
  - HTTP 요청 패널은 요청이 있어야 값이 나옵니다. "대기열 걸어 보기"를 하거나 관리자 화면을 둘러본 뒤에 봅니다.
- Prometheus(`http://localhost:3003/targets`): 수집 대상 `main-server`와 `node-exporter`가 UP인지 봅니다.

Docker Desktop에서 node-exporter는 Windows가 아니라 Docker Desktop이 쓰는 Linux VM의 자원을 보여 줍니다.

### 종료와 초기화

```bash
docker compose down      # 컨테이너와 네트워크를 지웁니다
docker compose down -v   # 볼륨까지 지웁니다
```

- `docker compose down` 뒤 다시 `docker compose up -d`로 올리면 등록한 대기열이 사라집니다. MongoDB 데이터를 볼륨에 두지 않고, 데모 nginx도 이미지의 설정으로 새로 뜨기 때문입니다. 초기 설정은 에이전트가 다시 넣으므로 대기열만 다시 등록하면 됩니다. Redis·Prometheus·데모 MySQL 데이터는 남습니다. Redis에 남은 대기 상태는 등록 정보가 사라진 대기열의 것이므로 main이 다시 기동할 때 지웁니다. 이때 main 로그에 `기동 정리: 등록 정보가 없는 대기열 N개의 Redis 상태를 지웠다`가 남습니다. 다시 등록한 대기열은 새 대기열 id를 받으므로 이전 상태와 섞이지 않습니다.
- `docker compose down -v`는 Redis·Prometheus·데모 MySQL 데이터가 든 볼륨까지 지웁니다. 인증서(`demo/nginx/cert/`)와 `.env`는 지우지 않습니다.
- main이나 Redis 컨테이너를 재시작해도 기다리던 대기자의 순번은 그대로입니다. 대기열 상태를 main 밖의 Redis에 두고, Redis는 AOF(1초마다 디스크에 반영)로 `redis_data` 볼륨에 기록하기 때문입니다. Redis가 비정상 종료되면 직전 1초 사이의 변경은 잃을 수 있습니다. main이 기동할 때 지우는 것은 등록 정보가 없는 대기열의 상태뿐입니다.
- Grafana는 데이터를 볼륨에 두지 않습니다. 컨테이너를 새로 만들 때마다 저장소의 프로비저닝 파일로 데이터소스와 대시보드가 다시 만들어집니다.

<br>

## User's Guide

<b>대기열 애플리케이션을 동작시킵니다.</b>

![QQueueing](./.asset/대기열_첫_화면.PNG)
<br><br><br><br>

<b>등록하기 버튼을 누르고, 대기열을 적용할 URL, 서비스 명, 대기열 대표 이미지를 등록합니다.</b>

![QQueueing](./.asset/대기열_등록_화면.PNG)
<br><br><br><br>

<b>대기열 리스트 버튼을 누르고, 대기열이 적용된 모습을 확인합니다.</b>

![QQueueing](./.asset/대기열_리스트_화면.PNG)
<br><br><br><br>

<b>등록된 url을 클릭하여 상세 정보를 확인할 수 있고, 활성/비활성화 및 설정을 변경할 수 있습니다.</b>

![QQueueing](./.asset/url_상세_정보_화면.PNG)
<br><br><br><br>

<b>대시보드 버튼을 클릭하여 대기열이 적용된 운영자의 컴퓨팅 자원과, 대기열 어플리케이션의 상태를 모니터링할 수 있습니다.</b>
![QQueueing](./.asset/대쉬보드_gif.gif)
<br>
![QQueueing](./.asset/사용자_컴퓨팅_자원_모니터링_gif.gif)

![QQueueing](./.asset/대기열_애플리케이션_모니터링_gif.gif)

<br>

## Run Screen

- PC Version <br>

![QQueueing](./.asset/pc_대기열_gif.gif)
<br><br><br><br>

- Mobile Version <br>

![QQueueing](./.asset/모바일_대기열_gif.gif)

<br>

## 동작 방식

### 구성

```mermaid
flowchart LR
  user([브라우저])
  subgraph demo[데모 대상 사이트]
    nginx["demo nginx<br/>:80 / :443"]
    dfront[demo frontend]
    dback[demo backend]
    mysql[(demo mysql)]
  end
  subgraph qq[QQueueing]
    main["qqueueing-main :8081"]
    admin["qqueueing-frontend :3001"]
    redis[(redis)]
    mongo[(mongo)]
    agent[qqueueing-agent]
  end
  subgraph mon[모니터링]
    prom["prometheus :3003"]
    nodeexp[node-exporter]
    grafana["grafana :3002"]
  end
  user -->|"https://localhost"| nginx
  user -->|"http://localhost:3001"| admin
  user -->|"http://localhost:3002"| grafana
  nginx -->|"/"| dfront
  nginx -->|"/api"| dback --> mysql
  nginx -->|"등록된 URL, /waiting, /qqueueingAPI"| main
  main -->|"대기열 상태"| redis
  main -->|"등록 정보"| mongo
  main -->|"대기 페이지 화면"| admin
  main -->|"통과 후 원본 요청<br/>host.docker.internal:80"| nginx
  main -->|"FIFO /pipes"| agent
  agent -->|"docker.sock<br/>설정 반영, reload"| nginx
  prom -->|scrape| main
  prom -->|scrape| nodeexp
  main -->|"관리자 대시보드용 지표 조회"| prom
  grafana -->|조회| prom
```

- QQueueing(`src/`): main(API 서버. 1초마다 입장 처리도 합니다), 관리자 프론트(관리자 화면과 대기 페이지), Redis(대기열 상태), MongoDB(대기열 등록 정보), URL 등록 에이전트
- 데모 대상 사이트(`demo/`): 대기열을 걸어 볼 예제 쇼핑몰입니다. nginx가 앞단 리버스 프록시이고, QQueueing은 이 nginx에 `location`을 넣어 대기열을 겁니다.
- 모니터링: Prometheus, node-exporter, Grafana

### 요청 흐름

1. 사용자가 등록된 URL(예: `https://localhost/product/1`)에 접속하면, 데모 nginx에 등록된 `location`이 요청을 main의 `/waiting/enter`로 보냅니다. 원래 URL은 `Target-URL` 헤더에 실립니다.
2. main은 그 URL의 대기열이 활성 상태면 대기 페이지(`<PUBLIC_ORIGIN>/waiting/queue-page?Target-URL=...`)로 리다이렉트합니다. 비활성이면 한 번만 쓸 수 있는 통과 토큰을 만들어 `<PUBLIC_ORIGIN>/waiting/page-req?token=...`로 바로 보냅니다.
3. 대기 페이지는 관리자 프론트(`qqueueing-frontend`)의 `/waiting` 화면입니다. main이 이 화면을 가져와 데모 nginx의 `/waiting` 경로로 내줍니다.
4. 대기 페이지가 `POST /qqueueingAPI/waiting`으로 줄을 서면, main은 추측할 수 없는 대기자 ID(UUID)를 발급해 그 대기열의 줄(Redis Sorted Set)에 넣습니다. 응답에는 대기열 id, 대기자 ID, 처음 순번이 들어 있습니다.
5. main은 1초마다 활성 대기열을 돌며 줄 앞에서 대기자를 꺼내 입장 기록으로 옮깁니다(입장). 지금은 대기열마다 1초에 최대 100명을 입장시킵니다.
6. 대기 페이지는 1초마다 `POST /qqueueingAPI/waiting/order`에 대기열 id와 대기자 ID를 보내 순번과 대기 인원을 묻습니다. 순번은 앞에 남은 대기자 수 + 1이라, 앞사람이 입장하거나 이탈하면 바로 줄어듭니다. 입장한 대기자는 다음 조회 응답에서 통과 토큰을 한 번만 받습니다. "나가기"를 누르면 `POST /qqueueingAPI/waiting/out`으로 줄에서 빠집니다(이탈).
7. 대기 페이지가 `/qqueueingAPI/waiting/page-req?token=...`로 이동하면, main은 토큰을 확인해 지우고 원래 페이지를 `http://host.docker.internal/<경로>`에서 가져와 돌려줍니다. 같은 토큰은 다시 쓸 수 없습니다. 이 요청은 호스트 80 포트를 거쳐 데모 nginx의 `host.docker.internal` 서버 블록으로 들어가 데모 프론트에 닿습니다. 그래서 데모 nginx는 호스트 80 포트를 써야 합니다.

관리자가 대기열을 비활성화·활성화·삭제할 때와 main이 기동할 때, Redis의 대기열 상태는 다음과 같이 바뀝니다.

- **비활성화**: 줄에 남은 대기자를 한 번에 모두 입장시킨 뒤 비활성으로 바꿉니다. 남은 대기자는 다음 순번 조회에서 통과 토큰을 받아 원래 페이지로 넘어갑니다. 비활성화와 거의 같은 때에 줄을 서서 줄에 남은 대기자도 순번을 물으면 그 자리에서 입장합니다.
- **다시 활성화**(비활성 → 활성): 그 대기열의 줄, 대기 번호, 입장 기록, 통과 토큰, 누적 입장 인원을 모두 비우고 빈 줄에서 시작합니다. 비활성화로 입장했지만 아직 통과 토큰을 받아 가지 않은 대기자와, 비활성 동안 발급되고 쓰이지 않은 통과 토큰은 무효가 됩니다. 그래서 다시 활성화한 뒤에 온 사람은 모두 줄을 섭니다. 이미 활성인 대기열을 활성화하면 아무것도 바뀌지 않습니다.
- **삭제**: 그 대기열이 발급한 통과 토큰까지 상태를 모두 지웁니다. 같은 대상 URL로 다시 등록하면 새 대기열 id로 빈 줄에서 시작하고, 이전 대기자 ID나 통과 토큰은 쓸 수 없습니다.
- **main 기동**: MongoDB 등록 정보에 없는 대기열 id의 상태를 지웁니다. 삭제 도중 Redis 정리가 실패했거나 `docker compose down`으로 등록 정보만 사라진 경우에 남은 상태입니다. 등록된 대기열의 상태는 건드리지 않으므로 main을 재시작해도 기다리던 대기자의 순번이 이어집니다.

대기열 상태(줄, 대기 번호, 입장 기록, 통과 토큰, 누적 입장 인원)는 모두 Redis에 두고 대기열 id로 구분합니다. main 메모리에는 MongoDB에서 다시 읽어 올 수 있는 등록 정보만 둡니다. 대기열 저장소로 Redis Sorted Set을 고른 이유는 [ADR-0001](docs/adr/0001-redis-sorted-set-for-waiting-queue.md)에, 용어 정의는 [CONTEXT.md](CONTEXT.md)에 있습니다.

### API로 따라가 보기

대기 페이지가 보내는 요청을 Git Bash에서 직접 보내 볼 수 있습니다. "대기열 걸어 보기"의 1~3으로 `https://localhost/product/1`을 등록한 뒤 실행합니다. `-k`는 자체 서명 인증서를 검사하지 않는 옵션입니다. `<queueId>`처럼 꺾쇠로 감싼 자리에는 앞 응답의 값을 옮겨 적습니다.

```bash
# 줄 서기: result에 queueId(대기열 id), waiterId(대기자 ID), myOrder(순번)가 나옵니다
curl -sk -X POST -H "Target-URL: https://localhost/product/1" https://localhost/qqueueingAPI/waiting

# 순번 조회: 줄에 있으면 "status":"WAITING"과 순번(myOrder), 대기 인원(totalQueueSize)이 나옵니다.
# 입장했으면 "status":"ENTERED"와 통과 토큰(token)이 한 번만 나오고, 그다음부터는 "status":"NOT_FOUND"입니다
curl -sk -X POST -H "Content-Type: application/json" -d '{"queueId":"<queueId>","waiterId":"<waiterId>"}' https://localhost/qqueueingAPI/waiting/order

# 통과: 원래 페이지(데모 상품 페이지 HTML)가 나옵니다. 같은 토큰을 다시 쓰면 "invalid token"이 나옵니다
curl -sk "https://localhost/qqueueingAPI/waiting/page-req?token=<token>"
```

줄에 혼자 있으면 1초 안에 입장하므로 순번 조회는 처음부터 `ENTERED`로 나옵니다.

### URL 등록 흐름

1. 관리자 화면에서 URL을 등록하면 main(`POST /queue`)이 MongoDB에 등록 정보를 저장합니다. 대기열은 이 등록 정보의 id(대기열 id)로 부르고, 등록한 대기열은 바로 활성 상태가 됩니다.
2. main은 에이전트와 함께 쓰는 볼륨의 FIFO(`/pipes/pipe`)에 `bash conf.sh register <URL>` 한 줄을 씁니다. 삭제(`DELETE /queue/{id}`)할 때는 `delete`를 쓰고, MongoDB의 등록 정보와 함께 Redis에 있는 그 대기열의 상태(줄, 입장 기록, 통과 토큰 등)도 지웁니다.
3. 에이전트(`qqueueing-agent`)는 FIFO를 계속 읽다가, 이 형식과 URL 규칙에 맞는 줄만 `conf.sh`에 인자로 넘깁니다. 그 밖의 입력은 실행하지 않고 로그만 남깁니다.
4. `conf.sh`는 docker.sock으로 대상 nginx 컨테이너(`TARGET_NGINX_CONTAINER`, 기본 `demo-nginx`)의 `/etc/nginx`를 복사해 옵니다. 443을 listen하는 server 블록 가운데 `server_name`이 URL의 호스트와 같은 블록에 아래와 같은 `location`을 넣습니다(삭제할 때는 뺍니다). 새 설정을 넣은 뒤 `nginx -t`가 통과하면 reload하고, 실패하면 원래 설정으로 되돌립니다.
   ```nginx
   location /product/1 {
       proxy_pass http://qqueueing-main:8081/waiting/enter ;
       proxy_set_header Target-URL https://localhost/product/1 ;
   }
   ```
5. 에이전트는 시작할 때와 대상 nginx 컨테이너가 새로 뜰 때 초기 설정을 넣습니다. 443 server 블록에는 `/qqueueingAPI`(main API)와 `/waiting`(대기 페이지) location을, 80 포트에는 원래 페이지를 가져오는 통로인 `host.docker.internal` server 블록을 추가합니다. 이미 들어 있으면 건너뜁니다.

에이전트 로그와 지금 적용된 nginx 설정은 이렇게 봅니다.

```bash
docker compose logs -f qqueueing-agent
docker exec demo-nginx nginx -T
```

### 모니터링

- Prometheus는 1초마다 두 대상을 수집합니다. node-exporter(job `node-exporter`)와 main의 actuator(`/monitoring/prometheus`, job `main-server`)입니다. 설정 파일은 `src/build/prometheus/prometheus.yml`입니다.
- 관리자 화면의 대시보드는 main API를 거쳐 지표를 봅니다. 호스트 자원과 요청 수는 main이 Prometheus에 질의해 돌려주고, JVM·HTTP 지표는 main의 actuator(`/monitoring/metrics/...`)에서 바로 가져옵니다.
- Grafana는 Prometheus를 데이터소스로 씁니다. 데이터소스(`src/build/grafana/provisioning/datasources/prometheus.yml`)와 대시보드(`src/build/grafana/dashboards/*.json`)는 프로비저닝 파일로 저장소에서 관리합니다.
- main에는 대기 인원 같은 대기열 전용 지표가 없습니다. Grafana의 "대기열 경로 (/waiting/**)" 패널은 main의 HTTP 요청 지표로 대기열 트래픽을 보여 줍니다. Redis 지표는 수집하지 않습니다. Redis 연결 상태는 main의 `/monitoring/health`에서 봅니다.

<br>

## 설정

루트의 `.env`(`.env.example`을 복사한 파일)로 바꿉니다. `.env`를 고친 뒤 `docker compose up -d`를 다시 실행하면 바뀐 서비스만 새로 만들어집니다. 같은 이름의 셸 환경변수가 있으면 `.env`보다 우선합니다.

| 변수 | 기본값 | 설명 |
|---|---|---|
| `MAIN_PORT` | `8081` | main API 호스트 포트 |
| `ADMIN_PORT` | `3001` | 관리자 화면 호스트 포트 |
| `GRAFANA_PORT` | `3002` | Grafana 호스트 포트 |
| `PROMETHEUS_PORT` | `3003` | Prometheus 호스트 포트 |
| `NODE_EXPORTER_PORT` | `8100` | node-exporter 호스트 포트 |
| `MONGO_PORT` | `27017` | MongoDB 호스트 포트 |
| `REDIS_PORT` | `6379` | Redis 호스트 포트. IDE에서 main을 실행할 때 `localhost:<이 포트>`로 붙습니다 |
| `MONGO_ROOT_USERNAME`, `MONGO_ROOT_PASSWORD` | `root`, `example` | MongoDB 루트 계정. main의 접속 URI에 그대로 들어가므로 `@ : / ? # %`는 쓰지 않습니다. MongoDB 데이터가 새로 만들어질 때만 적용되므로, 바꾼 뒤에는 `docker compose down` 후 다시 올립니다 |
| `PUBLIC_ORIGIN` | `https://localhost` | 브라우저가 데모 사이트에 접속하는 주소. 아래 설명 참고 |
| `TARGET_NGINX_CONTAINER` | `demo-nginx` | URL 등록 에이전트가 설정을 고치고 reload할 nginx 컨테이너 이름 |
| `DEMO_MYSQL_USER`, `DEMO_MYSQL_PASSWORD`, `DEMO_MYSQL_ROOT_PASSWORD` | `demo`, `demo`, `root` | 데모 MySQL 계정. 데이터 볼륨이 처음 만들어질 때만 적용되므로, 바꾼 뒤에는 `docker compose down` 후 `docker volume rm qqueueing_demo_mysql_data`로 데이터를 지우고 다시 올립니다 |
| `DEMO_JWT_KEY` | `.env.example`의 값 | 데모 백엔드의 JWT 서명 키. Base64 문자열이고, 디코딩했을 때 32바이트 이상이어야 합니다 |
| `GF_SECURITY_ADMIN_USER`, `GF_SECURITY_ADMIN_PASSWORD` | `admin`, `admin` | Grafana 관리자 계정. Grafana 컨테이너를 새로 만들 때마다 적용됩니다 |

`PUBLIC_ORIGIN`은 브라우저가 데모 사이트(데모 nginx)에 접속하는 주소입니다. 끝에 `/`를 붙이지 않습니다.

- main은 대기 페이지와 통과 페이지로 리다이렉트할 때 이 주소를 씁니다(`<PUBLIC_ORIGIN>/waiting/...`).
- 관리자 화면과 대기 페이지는 `<PUBLIC_ORIGIN>/qqueueingAPI`로, 데모 프론트는 `<PUBLIC_ORIGIN>/api`로 API를 부릅니다. 두 프론트에는 빌드할 때 이 값이 들어가므로, 바꾼 뒤에는 `docker compose up -d --build`로 다시 빌드합니다.
- 로컬에서는 기본값을 그대로 씁니다. 호스트 이름을 바꾸려면 `demo/nginx/conf/default.conf`의 `server_name`과 인증서의 SAN도 같은 이름으로 바꿔야 URL 등록이 동작합니다. 포트는 붙일 수 없습니다.

<br>

## 개발 환경

### main을 IDE에서 실행하기

MongoDB와 Redis만 compose로 띄우고, main은 IDE(IntelliJ 등)나 `gradlew bootRun`으로 호스트에서 실행합니다. main의 `application.yml` 기본값이 이 방식(`localhost`와 `.env.example`의 기본 호스트 포트)을 기준으로 합니다.

- JDK 21이 필요합니다.
- Redis는 7.4 이상이어야 합니다(main이 필드별 만료 시간 명령 `HEXPIRE`를 씁니다). compose의 `qqueueing-redis`를 쓰면 됩니다.
- 전체 스택이 떠 있다면 컨테이너 main을 먼저 멈춥니다: `docker compose stop qqueueing-main`. 포트 8081이 겹치고, 두 main이 같은 Redis에서 함께 입장 처리를 하게 되기 때문입니다.

```bash
docker compose up -d qqueueing-mongo qqueueing-redis
```

IntelliJ에서는 `src/main`을 Gradle 프로젝트로 열고 `ApiServerApplication`을 실행합니다. 터미널에서는 다음과 같이 실행합니다.

```bash
cd src/main
./gradlew bootRun        # PowerShell: .\gradlew.bat bootRun
```

`http://localhost:8081/monitoring/health`가 `UP`이면 뜬 것입니다. IDE 실행 기본값과, `.env`에서 포트나 계정을 바꿨을 때 덮어쓸 환경변수는 다음과 같습니다. 환경변수 이름은 Spring relaxed binding 규칙(점은 `_`로, 대시는 빼고, 대문자로)을 따릅니다.

| 대상 | 기본값 | 환경변수 |
|---|---|---|
| MongoDB | `mongodb://root:example@localhost:27017/qqueueing?authSource=admin&authMechanism=SCRAM-SHA-1` | main: `SPRING_DATA_MONGODB_URI` |
| Redis | `localhost:6379` | main: `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT` |
| 대기 페이지(관리자 프론트) | `localhost:3001/waiting` | main: `SERVERS_FRONT` |
| Prometheus | `http://localhost:3003` | main: `PROMETHEUS_MONITORING` |

IDE로 띄운 main은 다음을 할 수 없습니다. 대기열 흐름 전체는 컨테이너로 띄운 스택에서 확인합니다.

- URL 등록이 데모 nginx에 반영되지 않습니다. FIFO와 에이전트는 컨테이너에만 있습니다(등록 정보는 MongoDB에 저장됩니다).
- 데모 nginx는 `qqueueing-main` 컨테이너로 요청을 보내므로 `https://localhost`를 거친 요청은 IDE의 main에 닿지 않습니다. IDE의 main에는 `http://localhost:8081`로 바로 요청합니다.

### main 테스트 실행하기

main의 테스트는 대기열 HTTP API를 프로세스 안에서 띄운 main에 보내 확인합니다. MongoDB와 Redis는 Testcontainers가 컨테이너로 띄우므로 Docker Desktop이 켜져 있어야 합니다. compose 스택을 띄우지 않아도 되고, 떠 있어도 포트가 겹치지 않습니다.

```bash
cd src/main
./gradlew test           # PowerShell: .\gradlew.bat test
```

결과 보고서는 `src/main/build/reports/tests/test/index.html`에 생깁니다.

### 관리자 프론트를 `npm run dev`로 실행하기

Node.js 20을 씁니다(이미지 빌드도 `node:20`을 씁니다).

```bash
cd src/frontend
cp .env.example .env.local
npm install
npm run dev
```

`http://localhost:3000`으로 접속합니다. `.env.local`의 값은 다음과 같습니다.

- `NEXT_PUBLIC_BASE_URL`, `NEXT_PUBLIC_MONITORING_URL`: main API 주소입니다. 기본값 `https://localhost/qqueueingAPI`는 데모 nginx를 거쳐 컨테이너 main으로 갑니다(인증서 수락이 필요합니다). main에 바로 붙으려면 `http://localhost:8081`로 바꿉니다. IDE로 띄운 main도 이 주소입니다.
- `NEXT_PUBLIC_TARGET_URL`: 등록 화면의 URL 입력칸 기본값입니다. 비워 둬도 됩니다.

컨테이너 이미지는 `.env.local`을 쓰지 않고 루트 `.env`의 `PUBLIC_ORIGIN`으로 빌드합니다. `package-lock.json`이 `package.json`과 맞지 않아 `npm ci`는 실패하므로 `npm install`을 쓰고, 이때 바뀐 `package-lock.json`은 커밋하지 않습니다.

### Grafana 대시보드 고치기

대시보드는 저장소의 JSON 파일(`src/build/grafana/dashboards/`)로 관리하므로 Grafana UI에서는 저장되지 않습니다. 고친 내용을 남기려면 다음과 같이 합니다.

1. Grafana에서 대시보드를 고친 뒤 저장을 누릅니다. `Cannot save provisioned dashboard` 창이 뜨면 거기서 JSON을 복사하거나 파일로 저장합니다. Export > Export as JSON으로 받을 때는 "Export the dashboard to use in another instance"를 끈 채로 받습니다.
2. 받은 JSON으로 `src/build/grafana/dashboards/`의 같은 파일(`node-exporter.json` 또는 `qqueueing-main.json`)을 바꿉니다. `uid`는 그대로 둡니다.
3. Grafana가 30초 간격으로 파일을 다시 읽습니다. 바로 보려면 `docker compose restart qqueueing-grafana`를 실행합니다.
4. 바뀐 JSON 파일을 커밋합니다.

<br>

## 문제 해결

- **`demo-nginx`가 뜨지 않고 로그에 `cannot load certificate "/etc/ssl/demo/fullchain.pem"`가 있다**: 인증서를 만들지 않은 것입니다. `docker compose run --rm demo-cert`를 실행한 뒤 `docker compose up -d`를 다시 실행합니다.
- **80·443 포트를 쓸 수 없다는 오류로 `demo-nginx`가 뜨지 않는다**: 그 포트를 쓰는 프로그램을 끄고 `docker compose up -d`를 다시 실행합니다. 이 두 포트는 바꿀 수 없습니다.
- **관리자 화면에 대기열 목록이 뜨지 않거나 등록이 실패한다**: `https://localhost` 인증서를 수락했는지, 에이전트 로그에 `demo-nginx 초기 설정 완료`가 있는지 봅니다. 초기 설정이 들어가기 전에는 `/qqueueingAPI` 요청이 main으로 가지 않습니다.
- **등록했는데 대기 페이지가 뜨지 않는다**: `docker compose logs qqueueing-agent`에서 원인을 봅니다.
  - `형식에 맞지 않는 입력` 또는 `no server block that listens 443 with server_name ...`: URL 형식이나 호스트가 틀린 것입니다. 목록에서 삭제하고 규칙에 맞게 다시 등록합니다.
  - `duplicate location`과 `nginx -t가 실패해서 ... 되돌린다`: 데모 nginx에 이미 있는 경로와 겹친 것입니다. 삭제하지 말고 `docker compose down` 후 `docker compose up -d`로 초기화합니다.
- **main이나 관리자 프론트를 다시 빌드하거나 멈췄다가 다시 띄운 뒤 등록한 URL이나 `/qqueueingAPI`가 502를 낸다**: nginx가 시작할 때 찾아 둔 컨테이너 주소가 바뀐 것입니다. `docker compose restart demo-nginx`를 실행합니다. 에이전트가 초기 설정을 다시 확인합니다.
- **`qqueueing-agent`가 재시작을 반복하고 로그에 `Docker 데몬에 연결하지 못했다`가 있다**: docker.sock 마운트가 막힌 것입니다. Docker Desktop의 Enhanced Container Isolation처럼 소켓 마운트를 막는 설정이 켜져 있는지 봅니다.
- **`docker compose down` 뒤 다시 올리니 등록한 대기열이 없다**: 정상입니다("종료와 초기화" 참고). 다시 등록합니다.
- **Grafana에서 바꾼 비밀번호나 대시보드가 사라졌다**: Grafana 데이터는 볼륨에 두지 않으므로 컨테이너를 새로 만들면 `.env`와 프로비저닝 파일 기준으로 돌아갑니다. 대시보드는 "Grafana 대시보드 고치기"대로 JSON 파일에 반영합니다.
- **다른 폴더에 클론한 저장소를 함께 띄울 수 없다**: compose 프로젝트 이름(`qqueueing`), 컨테이너 이름(`demo-nginx`), 네트워크 이름(`qqueueing-network`)이 고정이라 한 PC에서 한 벌만 띄울 수 있습니다.

<br>

## 폴더 구조

```
.
├── compose.yml           # 실행 진입점. src/compose.yml과 demo/compose.yml을 include합니다
├── .env.example          # 설정 예시(.env로 복사해서 씁니다)
├── src/                  # QQueueing
│   ├── compose.yml       # QQueueing과 모니터링 서비스
│   ├── main/             # main API 서버(Spring Boot, Java 21). 1초마다 입장 처리도 합니다
│   ├── frontend/         # 관리자 화면과 대기 페이지(Next.js)
│   ├── pipes/            # URL 등록 에이전트 스크립트(agent.sh, conf.sh)와 nginx 설정 파서
│   └── build/            # Dockerfile, Prometheus 설정, Grafana 프로비저닝 파일과 대시보드
├── demo/                 # 데모 대상 사이트(예제 쇼핑몰)
│   ├── compose.yml
│   ├── nginx/            # 리버스 프록시 설정. 인증서는 nginx/cert/에 둡니다(git에서 제외)
│   ├── frontend/         # Next.js
│   ├── backend/          # Spring Boot
│   └── mysql/init/       # 스키마 초기화 SQL
├── CONTEXT.md            # 용어집(대기열, 대기자, 순번, 입장, 통과 토큰 등)
├── docs/
│   ├── adr/              # 설계 결정 기록(ADR)
│   └── agents/           # 에이전트(Claude Code)로 이슈를 계획·구현·검증하는 절차와 규칙
├── .claude/agents/       # 계획·구현·검증 에이전트 정의
└── .asset/               # README 이미지
```

<br>

## Contributors

| [지인성](https://github.com/JIINSUNG)                                                     | [이상학](https://github.com/yee950419)                                                             | [손영훈](https://github.com/syhuni)                                                       | [신문영](https://github.com/ztrl)                                                         | [김동건](https://github.com/Zerotay)                                                      | [강이규](https://github.com/EhighG)                                                              |
| ----------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| <img src="https://avatars.githubusercontent.com/u/49591292?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/65946607?v=4" width="150" height="150">          | <img src="https://avatars.githubusercontent.com/u/74291750?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/88647858?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/67823010?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/71206505?v=4" width="150" height="150">        |

<br>

## License

[LICENSE](./LICENSE)에서 라이센스 저작권과 제한사항을 확인하십시오.

See the [LICENSE](./LICENSE) for license rights and limitations.
