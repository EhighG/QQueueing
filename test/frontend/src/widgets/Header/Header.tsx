import { NavButton } from "@/entities";
import React from "react";

const Header = () => {
  return (
    <div className="flex items-center w-full h-[60px] gap-[30px]">
      <NavButton title="서비스 로고" path="/" />
      <NavButton title="Queueing" path="/introduce" />
      <NavButton title="Example" path="/example" />
      <NavButton title="Docs" path="/docs" />
      <NavButton title="GitHub" path="/github" />
      <NavButton title="Setting" path="/setting" />
    </div>
  );
};

export default Header;
