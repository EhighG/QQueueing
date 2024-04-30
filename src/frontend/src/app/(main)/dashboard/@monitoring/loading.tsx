import { PerformanceCard } from "@/shared";
import { Skeleton } from "@mui/material";
import React from "react";

const Loading = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md">
      <Skeleton variant="rectangular" width={100} height={40} />
      <div className="flex items-center flex-1 justify-around">
        <Skeleton variant="rectangular" width={240} height={240} />
        <Skeleton variant="rectangular" width={240} height={240} />
        <Skeleton variant="rectangular" width={240} height={240} />
      </div>
    </div>
  );
};

export default Loading;
