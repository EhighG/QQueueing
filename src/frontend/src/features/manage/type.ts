export type ResponseType<T> = {
  status: number;
  message: string;
  result: T;
};
