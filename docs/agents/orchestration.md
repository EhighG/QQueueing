# 작업 진행 방식

GitHub 이슈 하나를 계획 → 구현 → 검증 → 머지까지 끌고 가는 절차다. 메인 세션은 이 흐름을 조율하고 사용자와 소통한다. 계획서를 쓰거나 코드를 고치는 일은 서브에이전트에 맡긴다.

## 역할

| 역할 | 에이전트 | 모델 · 추론 강도 |
|---|---|---|
| 계획 | `planner` | Opus · xhigh |
| 구현 | `implementer` | Sonnet · high |
| 검증 | `verifier` | Opus · high |

코드 위치 찾기 같은 단순 조회는 기본 `Explore` 에이전트에 맡긴다.

## 이슈 하나의 흐름

1. **계획**: `planner`에 이슈 번호와 계획서 저장 경로(메인 세션 scratchpad의 `plan-issue-<번호>.md`)를 준다. 계획서의 "결정 필요"가 "없음"이 아니면 사용자에게 묻고 답을 반영한다. 확정한 계획서는 메인 세션이 맨 위에 "확정" 머리말을 붙여 `gh issue comment <번호> --body-file <경로>`로 이슈에 남긴다. 서브에이전트는 GitHub에 쓰지 않는다.
2. **구현**: `implementer`를 `isolation: "worktree"`로 부르고 계획서 경로, 이슈 번호, base 브랜치를 넘긴다. 막혔다는 보고가 오면 계획서를 고치거나(`planner` 재호출) 사용자에게 묻는다.
3. **검증**: `verifier`에 이슈 번호, worktree 경로, 브랜치, base 브랜치, 계획서 경로를 넘긴다.
4. **수정 루프**: FAIL이면 검증 보고서를 `implementer`에게 넘겨 같은 브랜치에서 고치게 하고 다시 검증한다. 두 번 고쳐도 FAIL이면 재계획하거나 사용자에게 보고한다.
5. **머지**: PASS면 브랜치를 push하고 PR을 연다(한국어, 본문에 `Closes #번호`와 검증 요약). `gh pr merge --squash --delete-branch`로 머지한다.
6. **마무리**: 이슈가 닫혔는지 확인한다(`Closes`가 이슈를 닫지 않는 경우가 있다). 열려 있으면 PR 번호를 적은 코멘트를 남기고 `gh issue close`로 닫는다. 그다음 worktree와 로컬 브랜치를 지우고(`git worktree remove --force`, `git worktree prune`, `git branch -D`) master를 pull한 뒤 사용자에게 결과를 보고한다.

완료 기준: 이슈가 닫혔고, 이슈에는 계획서가, PR에는 검증 결과가 남아 있다.

## 순서

- 선행 이슈가 머지된 뒤에 시작하고, 이슈 브랜치는 최신 master에서 딴다.
- 계획은 여러 이슈를 동시에 받아도 된다. 구현과 검증은 compose 파일이나 호스트 포트가 겹치는 이슈끼리 하나씩 한다.

## docker compose 스택 다루기

compose 프로젝트 이름(`qqueueing`), `container_name`, 호스트 포트가 고정이라 같은 머신에서 스택은 한 번에 하나만 뜰 수 있다. 메인 체크아웃, 각 worktree, 사용자가 띄운 스택 모두 마찬가지다.

- 스택을 띄우는 서브에이전트에게는 사전 점검(`docker ps -a --filter label=com.docker.compose.project=qqueueing`의 working_dir, 대상 포트 LISTENING)을 시키고, 다른 경로의 스택이 있으면 멈추고 보고하게 한다.
- `docker compose down -v`는 떠 있는 `qqueueing` 컨테이너가 모두 그 작업 경로 것일 때만 쓴다. 볼륨 이름도 프로젝트 이름을 따르기 때문이다.
- 사용자가 결과를 직접 보는 동안에는 스택을 띄우는 서브에이전트를 시작하지 않는다.
- 계획 단계의 실험은 다른 프로젝트 이름과 옮긴 포트로 하게 한다.
- 남의 프로젝트 컨테이너는 건드리지 않는다.

## 서브에이전트를 멈출 때

`TaskStop`으로 멈춘 뒤 그 에이전트가 남긴 것을 정리한다: 해당 worktree의 compose 스택(`docker compose ls`), 호스트 프로세스(`jps -l`의 `ApiServerApplication`, Gradle wrapper). 재개 지점은 브랜치 커밋(`git log master..<브랜치>`)으로 판단한다.

## GitHub와 셸에서 주의할 점

- 한글 제목·본문은 명령줄 인자로 넘기지 않는다. 파일로 만들어 `--body-file`이나 `gh api --input <json>`으로 넘긴다.
- `gh issue view`는 `--comments`와 `--json`을 함께 쓸 수 없다. 코멘트는 `--json comments --jq '.comments[0].body'`로 읽는다.
- 실행 환경의 가드가 `$(...)`, `exec`, 긴 heredoc이 든 복합 명령을 거부할 수 있다. 서브에이전트에게 "거부되면 같은 효과가 나게 쪼개 실행하고 보고서에 적으라"고 지시한다.

## 사용자에게 묻는 때

- 계획서에 "결정 필요" 항목이 있을 때
- 재계획이 상위 스펙 이슈의 결정 사항이나 범위를 바꿀 때
- 두 번 고쳐도 검증을 통과하지 못할 때
- 이슈 범위 밖이지만 스펙 목표를 막는 문제를 발견해 새 이슈가 필요할 때
