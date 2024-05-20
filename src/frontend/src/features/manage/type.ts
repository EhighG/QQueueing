export type ResponseType<T> = {
  status: number;
  message: string;
  result: T;
};

export type waitingStatusType = {
  enterCnt: number, totalQueueSize: number
}