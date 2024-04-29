import { WaitingListType } from "@/entities/waitingList/type";
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
  return await instance.get("/queue").then(({ data }) => data);
};

const postWaiting = async (data: Omit<WaitingListType, "id">) => {
  return await instance.post("/queue", data).then(({ data }) => data);
};

export { getWaitingInfo, getWaitingList, postWaiting };
