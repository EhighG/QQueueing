import { NextRequest, NextResponse } from "next/server";

// auth: 로그인 여부 확인용
export { auth as middleware } from "./auth";

// middleware를 적용할 라우트 목록, 즉 로그인 여부를 확인할 라우트 목록
// 접근권한 체크시 사용
export const config = {
  matcher: ["/dashboard"],
};
