import { WaitingListType, ResponseType } from "@/entities/waitingList/type";
import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";

const instance: AxiosInstance = axiosInstance();

const getWaitingInfo = async (waitingIdx: number) => {
  return await instance
    .post(`/tes24/waiting/${waitingIdx}`, {
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

export { getWaitingInfo, getWaitingList, postWaiting };
