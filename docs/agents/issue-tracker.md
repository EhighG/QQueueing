# Issue tracker: GitHub

이 저장소의 이슈와 스펙은 GitHub 이슈로 관리한다. 모든 작업에 `gh` CLI를 쓴다.

## Conventions

- **이슈 만들기**: `gh issue create --title "..." --body "..."`. 여러 줄 본문은 heredoc으로 넘긴다.
- **이슈 읽기**: `gh issue view <number> --comments`. 필요하면 `jq`로 코멘트를 거르고 라벨도 함께 가져온다.
- **이슈 목록**: `gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'`에 필요한 `--label`, `--state` 필터를 붙인다.
- **이슈에 코멘트**: `gh issue comment <number> --body "..."`
- **라벨 붙이기 / 떼기**: `gh issue edit <number> --add-label "..."` / `--remove-label "..."`
- **닫기**: `gh issue close <number> --comment "..."`

저장소는 `git remote -v`로 알아낸다. 클론 안에서 실행하면 `gh`가 알아서 찾는다.

## Pull requests as a triage surface

**PRs as a request surface: no.** _(외부 PR을 기능 요청으로 받는 저장소라면 `yes`로 바꾼다. `/triage`가 이 값을 읽는다.)_

`yes`이면 PR도 이슈와 같은 라벨과 상태를 거치며, 명령은 `gh pr` 쪽을 쓴다.

- **PR 읽기**: `gh pr view <number> --comments`, diff는 `gh pr diff <number>`.
- **트리아지할 외부 PR 목록**: `gh pr list --state open --json number,title,body,labels,author,authorAssociation,comments`로 가져와 `authorAssociation`이 `CONTRIBUTOR`, `FIRST_TIME_CONTRIBUTOR`, `NONE`인 것만 남긴다(`OWNER`/`MEMBER`/`COLLABORATOR`는 뺀다).
- **코멘트 / 라벨 / 닫기**: `gh pr comment`, `gh pr edit --add-label`/`--remove-label`, `gh pr close`.

GitHub는 이슈와 PR이 번호를 함께 쓰므로 `#42`만으로는 둘 중 무엇인지 알 수 없다. `gh pr view 42`로 먼저 확인하고, 아니면 `gh issue view 42`로 넘어간다.

## When a skill says "publish to the issue tracker"

GitHub 이슈를 만든다.

## When a skill says "fetch the relevant ticket"

`gh issue view <number> --comments`를 실행한다.

## Wayfinding operations

`/wayfinder`가 쓴다. **map**은 이슈 하나이고, 티켓은 그 이슈의 **child** 이슈다.

- **Map**: `wayfinder:map` 라벨이 붙은 이슈 하나. 본문에 Notes / Decisions-so-far / Fog를 둔다. `gh issue create --label wayfinder:map`.
- **Child ticket**: map에 GitHub sub-issue로 연결한 이슈(sub-issues 엔드포인트에 `gh api` 호출). sub-issue를 쓸 수 없으면 map 본문의 작업 목록에 child를 넣고 child 본문 맨 위에 `Part of #<map>`을 쓴다. 라벨은 `wayfinder:<type>`(`research`/`prototype`/`grilling`/`task`)이다. 누군가 맡으면 그 개발자에게 할당한다.
- **Blocking**: GitHub의 **네이티브 이슈 의존성**을 기준으로 삼으며, UI에서도 보인다. `gh api --method POST repos/<owner>/<repo>/issues/<child>/dependencies/blocked_by -F issue_id=<blocker-db-id>`로 간선을 추가한다. `<blocker-db-id>`는 blocker의 숫자 **database id**다(`gh api repos/<owner>/<repo>/issues/<n> --jq .id`로 얻는다. `#number`나 `node_id`가 아니다). GitHub는 `issue_dependencies_summary.blocked_by`에 열린 blocker만 세어 보여 주며, 이 값이 실제로 막혀 있는지를 판단하는 기준이다. 의존성 기능을 쓸 수 없으면 child 본문 맨 위에 `Blocked by: #<n>, #<n>` 줄을 둔다. blocker가 모두 닫히면 티켓이 풀린다.
- **Frontier query**: map의 열린 child를 나열한 뒤(`gh issue list --state open`, map의 sub-issue나 작업 목록으로 범위를 좁힌다) 열린 blocker가 있거나(`issue_dependencies_summary.blocked_by > 0`, 또는 `Blocked by` 줄에 열린 이슈) 담당자가 있는 것을 뺀다. 남은 것 중 map 순서상 첫 번째가 다음 티켓이다.
- **Claim**: `gh issue edit <n> --add-assignee @me`. 세션의 첫 쓰기 작업이다.
- **Resolve**: `gh issue comment <n> --body "<answer>"`, 이어서 `gh issue close <n>`, 그다음 map의 Decisions-so-far에 컨텍스트 포인터(요지 + 링크)를 덧붙인다.
