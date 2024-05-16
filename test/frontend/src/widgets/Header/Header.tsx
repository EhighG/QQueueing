import { NavButton } from "@/entities";
import React from "react";
import SearchIcon from "@mui/icons-material/Search";
import Image from "next/image";
import { logo } from "@/shared";

const Header = () => {
  return (
    <div className="flex items-center w-full min-h-[100px] justify-around">
      <NavButton title="QQueueing" path="/" />
      <div className="flex relative items-center">
        <input
          placeholder="지원을 원하는 회사를 검색해 보세요"
          type="text"
          title="inputForm"
          className="flex w-[600px] border-2 border-sky-600 rounded-md p-1 focus:outline-none opacity-80"
        />
        <div className="absolute self-center right-4">
          <SearchIcon color="primary" />
        </div>
      </div>
      <div className="size-[50px]">
        <Image src={logo} alt="service logo" width={400} height={400} />{" "}
      </div>
    </div>
  );
};

export default Header;
