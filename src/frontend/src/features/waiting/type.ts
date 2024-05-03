type infoType = {
  order: number;
  idVal: string;
  partitionNo?: number;
};

type statusType = {
  myOrder: number;
  totalQueueSize: number;
  redirectUrl: string;
};

export type { infoType, statusType };
