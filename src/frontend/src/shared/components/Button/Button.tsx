import { cls } from "@/shared";
import React from "react";

type ButtonProps = {
  className?: string;
  type?: "button" | "submit" | "reset";
  children: string;
  style?: "square" | "rounded";
  onClick?: () => void;
};

const Button = ({
  className,
  type = "button",
  children,
  style = "rounded",
  onClick,
}: ButtonProps) => {
  return (
    <button
      onClick={onClick}
      className={cls(
        className
          ? className
          : style === "rounded"
          ? "text-[1.5rem] border rounded-full px-10 py-2 bg-blue-500 text-white font-bold"
          : "text-[1.5rem] border rounded-md px-10 py-2 bg-blue-500 text-white font-bold",
        "hover:opacity-80"
      )}
      type={type}
    >
      {children}
    </button>
  );
};

export default Button;
