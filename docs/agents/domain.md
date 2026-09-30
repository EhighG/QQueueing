# Domain Docs

엔지니어링 스킬이 코드베이스를 탐색할 때 이 저장소의 도메인 문서를 어떻게 읽는지 정한다.

## Before exploring, read these

- 저장소 루트의 **`CONTEXT.md`**, 또는
- 저장소 루트에 **`CONTEXT-MAP.md`**가 있으면 그 파일. 컨텍스트마다 `CONTEXT.md` 하나씩을 가리키므로, 주제와 관련된 것을 모두 읽는다.
- **`docs/adr/`**: 작업할 영역에 걸친 ADR을 읽는다. 다중 컨텍스트 저장소라면 컨텍스트별 결정이 담긴 `src/<context>/docs/adr/`도 확인한다.

이 파일들이 없으면 **아무 말 없이 넘어간다**. 없다고 알리거나 미리 만들자고 제안하지 않는다. 용어나 결정이 실제로 정해질 때 `/domain-modeling` 스킬(`/grill-with-docs`, `/improve-codebase-architecture`에서 호출)이 그때그때 만든다.

## File structure

이 저장소는 단일 컨텍스트다. 루트 `CONTEXT.md`와 `docs/adr/`를 쓰며, 둘 다 아직 없고 필요할 때 만든다.

단일 컨텍스트 저장소(대부분의 저장소):

```
/
├── CONTEXT.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

다중 컨텍스트 저장소(루트에 `CONTEXT-MAP.md`가 있음):

```
/
├── CONTEXT-MAP.md
├── docs/adr/                          ← 시스템 전체 결정
└── src/
    ├── ordering/
    │   ├── CONTEXT.md
    │   └── docs/adr/                  ← 컨텍스트별 결정
    └── billing/
        ├── CONTEXT.md
        └── docs/adr/
```

## Use the glossary's vocabulary

결과물에서 도메인 개념을 부를 때(이슈 제목, 리팩터링 제안, 가설, 테스트 이름) `CONTEXT.md`에 정의된 용어를 쓴다. 용어집이 피하라고 적어 둔 동의어로 바꿔 부르지 않는다.

필요한 개념이 용어집에 없다면 신호로 받아들인다. 프로젝트가 쓰지 않는 말을 지어내고 있거나(다시 생각한다), 실제로 빈틈이 있는 것이다(`/domain-modeling`에서 다루도록 적어 둔다).

## Flag ADR conflicts

결과물이 기존 ADR과 어긋나면 조용히 덮어쓰지 말고 드러낸다.

> _ADR-0007(event-sourced orders)과 어긋나지만, …이므로 다시 논의할 가치가 있다_
