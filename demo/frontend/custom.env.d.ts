declare namespace NodeJS {
  interface ProcessEnv {
    //   readonly 이름 : string;
    readonly baseUrl: string;
  }
}

declare module "*.png" {
  const value: string;
  export default value;
}
