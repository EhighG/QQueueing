import { axiosInstance } from "@/shared";
import type { infoType, statusType } from "./type";

const instance = axiosInstance();

const enterApi = async (): Promise<infoType> => {
  return await instance.post("/member/enqueue").then(({ data }) => data);
};

const getInfoApi = async (Idx: number): Promise<statusType> => {
  return await instance
    .post<statusType>(`/tes24/waiting/${Idx}`, {
      idVal: "tmpUserId" + Idx,
    })
    .then(({ data }) => data);
};

export { enterApi, getInfoApi };
