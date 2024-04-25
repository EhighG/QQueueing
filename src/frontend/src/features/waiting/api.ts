import { axiosInstance } from "@/shared";
import { AxiosInstance } from "axios";

const instance: AxiosInstance = axiosInstance();

const fetchingWaiting = async () => {
  return await instance
    .get("https://jsonplaceholder.typicode.com/todos/1")
    .then(({ data }) => data);
};

export { fetchingWaiting };
