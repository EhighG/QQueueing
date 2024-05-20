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
  return (
    <Link href={path} className={cls(className ? className : "")}>
      {title}
    </Link>
  );
};

export default NavButton;
