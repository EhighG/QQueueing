import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";

const instance: AxiosInstance = axiosInstance();

const getWaiting = async () => {
  return await instance
    .get("https://jsonplaceholder.typicode.com/todos/1")
    .then(({ data }) => data);
};

const postWaiting = async (data: Object) => {
  return await instance.post("").then(({ data }) => data);
};

export { getWaiting, postWaiting };
