import { SessionProvider } from "next-auth/react";
import { BASE_PATH, auth } from "@/auth";

import AuthButtonClient from "./AuthButton.client";

const AuthButton = async () => {
  const session = await auth();
  if (session && session.user) {
    session.user = {
      name: session.user.name,
      email: session.user.email,
    };
  }

  return (
    <SessionProvider basePath={BASE_PATH} session={session}>
      <AuthButtonClient />
    </SessionProvider>
  );
};

export default AuthButton;
