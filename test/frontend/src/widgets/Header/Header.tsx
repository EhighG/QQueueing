import { NavButton } from "@/entities";
import React from "react";

const Header = () => {
  return (
    <div className="flex items-center w-full h-[60px] gap-[30px]">
      <NavButton title="Queueing" path="/" />
    </div>
  );
};

export default Header;
