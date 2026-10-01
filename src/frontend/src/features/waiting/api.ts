import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";
import { infoType, statusType } from "./type";
import { ResponseType } from "..";

const instance: AxiosInstance = axiosInstance();

// 줄 서기
const postEnqueue = async (target: string): Promise<infoType> => {
  return await instance
    .post<ResponseType<infoType>>(
      "/waiting",
      {},
      {
        headers: {
          "Content-Type": "application/json; charset=utf8",
          "Target-URL": target,
        },
      }
    )
    .then(({ data }) => data.result);
};

// 현재 나의 순번 조회
const getWaitingInfo = async (partitionNo: number, waiterId: string) => {
  return await instance
    .post<statusType>(`/waiting/order`, {
      partitionNo,
      waiterId,
    })
    .then(({ data }) => data);
};

// 이탈
const postWaitingOut = async (partitionNo: number, waiterId: string) => {
  return await instance
    .post(`/waiting/out`, null, { params: { partitionNo, waiterId } })
    .then(({ data }) => data);
};

export { postEnqueue, getWaitingInfo, postWaitingOut };
