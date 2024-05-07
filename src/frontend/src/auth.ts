import NextAuth from "next-auth";
import Credentials from "next-auth/providers/credentials";

export const {
  handlers: { GET, POST }, // API 라우트 핸들러
  auth,
  signIn, //로그인 용
} = NextAuth({
  providers: [
    Credentials({
      credentials: {
        id: {},
        password: {},
      },
      authorize: async (credentials) => {
        let user = null;
        // user =  사용자 로그인 정보를 가져오는 부분
        if (!user) {
          throw new Error("User not found");
        }
        return user;
      },
    }),
  ],
});
