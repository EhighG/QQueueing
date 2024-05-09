"use client";
import { useSession } from "next-auth/react";
import { Button } from "@/shared";
import { signIn, signOut } from "@/auth/helpers";

const AuthButton = () => {
  const session = useSession();

  return session?.data?.user ? (
    <Button
      onClick={async () => {
        await signOut();
      }}
    >
      로그아웃
    </Button>
  ) : (
    <Button onClick={async () => await signIn()}>로그인</Button>
  );
};

export default AuthButton;
