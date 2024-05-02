import { WaitingListType, ResponseType } from "@/entities/waitingList/type";
import { axiosInstance } from "@/shared";
import { AxiosInstance, AxiosResponse } from "axios";
import { infoType, statusType } from "./type";

const instance: AxiosInstance = axiosInstance();

const postEnqueue = async (): Promise<infoType> => {
  return await instance
    .post<infoType>("/member/enqueue")
    .then(({ data }) => data);
};

const getWaitingInfo = async (waitingIdx: number) => {
  return await instance
    .post<statusType>(`/tes24/waiting/${waitingIdx}`, {
      idVal: waitingIdx,
    })
    .then(({ data }) => data);
};

const getWaitingList = async () => {
  return await instance
    .get<ResponseType>("/queue")
    .then(({ data }) => data.result);
};

const postWaiting = async (
  data: Omit<WaitingListType, "id" | "queueImageUrl">
) => {
  return await instance.post("/queue", data).then(({ data }) => data);
};

export { postEnqueue, getWaitingInfo, getWaitingList, postWaiting };
