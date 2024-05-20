import { CircularProgress } from "@mui/material";
import React from "react";

const Loading = () => {
  return (
    <div className="flex flex-1 items-center justify-center">
      <CircularProgress />
    </div>
  );
};

export default Loading;
