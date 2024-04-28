"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import React, { ReactNode } from "react";
import { cls } from "@/shared";

type LinkButtonProps = {
  href: string;
  title: string;
  icon?: ReactNode;
};

const LinkButton = ({ href, title, icon }: LinkButtonProps) => {
  const pathname = usePathname();
  return (
    <Link
      href={href}
      className={cls(
        "flex items-center gap-[10px] m-2 p-2",
        href === pathname &&
          "bg-blue-500 rounded-md bg-gradient-to-r from-blue-500 to-blue-200 shadow-xl"
      )}
    >
      {icon}
      <p
        className={cls(
          "text-[1.5rem] text-[#625F6E]",
          href === pathname && "text-white font-bold"
        )}
      >
        {title}
      </p>
    </Link>
  );
};

export default LinkButton;
