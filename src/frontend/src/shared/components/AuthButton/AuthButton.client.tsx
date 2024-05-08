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
        await signIn();
      }}
    >
      Sign Out
    </Button>
  ) : (
    <Button onClick={async () => await signIn()}>Sign In</Button>
  );
};

export default AuthButton;
