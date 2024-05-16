type infoType = {
  order: number;
  idVal: string;
  partitionNo?: number;
};

type statusType = {
  myOrder: number;
  totalQueueSize: number;
  token: string;
  enterCnt: number;
};

export type { infoType, statusType };
