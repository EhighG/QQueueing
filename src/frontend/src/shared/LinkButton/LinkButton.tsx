"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import React, { ReactNode } from "react";
import { cls } from "@/shared";

type LinkButtonProps = {
  href: string;
  title: string;
};

const LinkButton = ({ href, title }: LinkButtonProps) => {
  const pathname = usePathname();
  return (
    <Link href={href} className="flex gap-[10px]">
      <p className={cls(href === pathname && "text-blue-600 font-bold")}>
        {title}
      </p>
    </Link>
  );
};

export default LinkButton;
