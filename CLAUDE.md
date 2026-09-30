## 언어

사용자와 나누는 대화, 작업 중 진행 보고, 산출물(문서, GitHub 이슈·PR, 커밋 메시지, 코드 주석)은 모두 한국어로 쓴다. 코드 식별자, 명령어, 파일 경로, 설정 키, 라벨, `feat:` 같은 커밋 접두어, 인용한 로그·오류 메시지는 원문 그대로 둔다.

## 작업 진행 방식

GitHub 이슈를 구현할 때는 `docs/agents/orchestration.md`의 절차를 따른다. 메인 세션은 조율과 사용자 소통을 맡고, 계획·구현·검증은 서브에이전트(`planner`, `implementer`, `verifier`)가 한다.

## 환경

- Git Bash에서 `docker`에 `/`로 시작하는 컨테이너 안 경로를 넘길 때는 `MSYS_NO_PATHCONV=1`을 붙인다. 그래야 Windows 경로로 바뀌지 않는다.
- 컨테이너 안에서 실행되는 파일(`.sh`, `gradlew`, `.py`, nginx 설정)은 LF로 쓴다. `.gitattributes`가 체크아웃을 LF로 맞춘다.

## Agent skills

### Issue tracker

이슈와 스펙은 GitHub Issues에 두고 `gh` CLI로 다룬다. 자세한 내용은 `docs/agents/issue-tracker.md`를 본다.

### Triage labels

기본 트리아지 라벨 5개(`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`)를 이름 그대로 쓴다. 자세한 내용은 `docs/agents/triage-labels.md`를 본다.

### Domain docs

단일 컨텍스트(single-context)다. 루트 `CONTEXT.md`와 `docs/adr/`를 쓰며, 둘 다 필요할 때 만든다. 자세한 내용은 `docs/agents/domain.md`를 본다.
