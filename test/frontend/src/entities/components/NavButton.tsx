"use client";
import { cls } from "@/shared";
import Link from "next/link";
import { usePathname } from "next/navigation";
import React from "react";

type NavButtonProps = {
  path: string;
  title: string;
  className?: string;
};

const NavButton = ({ path, title, className }: NavButtonProps) => {
  const pathName = usePathname();
  return (
    <Link
      href={path}
      className={cls(
        className ? className : "",
        pathName === path ? "text-black font-bold" : "text-gray-600"
      )}
    >
      {title}
    </Link>
  );
};

export default NavButton;
