// 줄 서기 응답(POST /waiting의 result)
type infoType = {
  queueId: string;
  waiterId: string;
  myOrder: number;
};

// WAITING: 줄에 있음, ENTERED: 입장해서 이번 응답에 통과 토큰이 있음, NOT_FOUND: 대기자 없음
type waitingStatus = "WAITING" | "ENTERED" | "NOT_FOUND";

// 순번 조회 응답(POST /waiting/order)
type statusType = {
  status: waitingStatus;
  myOrder: number;
  totalQueueSize: number;
  token: string | null;
};

export type { infoType, statusType, waitingStatus };
