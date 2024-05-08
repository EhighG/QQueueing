import React from "react";
import { auth } from "@/auth";
import WhoAmIServerAction from "./WhoAmIServerAction";
import WhoAmIApi from "./WhoAmIAPI";
import WhoAmIRSC from "./WhoAmIRSC";
const Page = async () => {
  const session = await auth();

  async function onGetUserAction() {
    "use server";
    const session = await auth();
    return session?.user?.name ?? null;
  }

  return (
    <main className="flex flex-col">
      <h1 className="text-3xl mb-5">TestRoute</h1>
      <div>User: {session?.user?.name}</div>
      <WhoAmIServerAction onGetUserAction={onGetUserAction} />
      <WhoAmIApi />
      <WhoAmIRSC />
    </main>
  );
};

export default Page;
