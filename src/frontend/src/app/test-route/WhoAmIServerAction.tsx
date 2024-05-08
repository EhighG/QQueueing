"use client";
import { useEffect, useState } from "react";
const WhoAmIServerAction = async ({
  onGetUserAction,
}: {
  onGetUserAction: () => Promise<string | null>;
}) => {
  const [user, setUser] = useState<string | null>();
  useEffect(() => {
    onGetUserAction().then((user) => setUser(user));
  }, []);
  return <div className="mt-5">Who am I (Server Action): {user}</div>;
};

export default WhoAmIServerAction;
