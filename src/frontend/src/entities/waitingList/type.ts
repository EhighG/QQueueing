type WaitingListType = {
  id: number;
  targetUrl: string;
  maxCapacity: number;
  processingPerMinute: number;
  serviceName: string;
  queueImageUrl: string;
};

type ResponseType = {
  status: number;
  message: string;
  result: WaitingListType[];
};

export type { WaitingListType, ResponseType };
