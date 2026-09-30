import { NavButton } from "@/entities";
import React from "react";
import SearchIcon from "@mui/icons-material/Search";
import Image from "next/image";
import { logo } from "@/shared";
import Link from "next/link";

const Header = () => {
  return (
    <div className="flex items-center w-full min-h-[100px] p-4 justify-around gap-2">
        <div></div>
      <div className="flex w-full max-w-[600px] relative items-center">
        <input
          placeholder="다양한 회사를 검색해 보세요"
          type="text"
          title="inputForm"
          className="flex w-full border-2 border-sky-600 rounded-md p-1 focus:outline-none opacity-80"
        />
        <div className="absolute self-center right-4">
          <SearchIcon color="primary" />
        </div>
      </div>
      <div className="size-[50px]">
          <Link href={"/"}>
        <Image src={logo} alt="service logo" width={400} height={400} />
          </Link>
      </div>
    </div>
  );
};

export default Header;
