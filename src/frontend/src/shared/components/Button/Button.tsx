import { cls } from "@/shared";
import React, { ButtonHTMLAttributes, ReactNode } from "react";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  className?: string;
  type?: "button" | "submit" | "reset";
  children: string | ReactNode;
  edgeType?: "square" | "rounded";
  color?:
    | "warning"
    | "primary"
    | "green"
    | "yellow"
    | "purple"
    | "pink"
    | "disable";
  onClick?: () => void;
};

const colorTypes = {
  warning: "bg-red-500",
  primary: "bg-blue-500",
  green: "bg-green-500",
  yellow: "bg-yellow-500",
  purple: "bg-purple-500",
  pink: "bg-pink-500",
  disable: "bg-gray-500",
};

const Button = ({
  className,
  type = "button",
  children,
  color = "primary",
  onClick,
  edgeType = "rounded",
}: ButtonProps) => {
  return (
    <button
      onClick={onClick}
      className={cls(
        className
          ? className
          : edgeType === "rounded"
          ? "text-[1.5rem] border rounded-full px-10 py-2 text-white font-bold"
          : "text-[1.5rem] border rounded-md px-10 py-2 text-white font-bold",
        "hover:opacity-80",
        colorTypes[color]
      )}
      type={type}
    >
      {children}
    </button>
  );
};

export default Button;
