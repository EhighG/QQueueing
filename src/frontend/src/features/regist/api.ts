import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";

const instance: AxiosInstance = axiosInstance();

const registWaiting = async (body: Object) => {
  return await instance.post("url", body).then(({ data }) => data);
};

export { registWaiting };
