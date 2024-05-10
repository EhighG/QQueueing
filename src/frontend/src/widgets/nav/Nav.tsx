import React from "react";
import { LinkButton } from "@/shared";
import HomeIcon from "@mui/icons-material/Home";
import ListIcon from "@mui/icons-material/List";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import InfoIcon from "@mui/icons-material/Info";
import ArticleIcon from "@mui/icons-material/Article";
import TerminalIcon from "@mui/icons-material/Terminal";
import GitHubIcon from "@mui/icons-material/GitHub";
import LoyaltyIcon from "@mui/icons-material/Loyalty";
import Diversity1Icon from "@mui/icons-material/Diversity1";
import AuthButton from "@/shared/components/AuthButton/AuthButton.server";
const NavMenu = () => {
  return (
    <nav className="max-2xl:w-[240px] w-[300px] h-full bg-white border-r rounded-r-md shadow-sm">
      <div className="flex flex-col h-full ml-[10px] gap-[100px]">
        <dl>
          <dt className="text-[2rem] font-bold">대기열 관리</dt>
          <dd>
            <LinkButton
              icon={<HomeIcon />}
              href="/dashboard"
              title="대시보드"
            />
          </dd>
          <dd>
            <LinkButton
              icon={<ListIcon />}
              href="/list"
              title="대기열 리스트"
            />
          </dd>
          <dd>
            <LinkButton
              icon={<AddCircleOutlineIcon />}
              href="/regist"
              title="등록 하기"
            />
          </dd>
          <dd>
            <AuthButton />
          </dd>
        </dl>
        <dl>
          <dt className="text-[2rem] font-bold">QQueueing</dt>
          <dd>
            <LinkButton
              icon={<InfoIcon />}
              href="/about"
              title="about"
            ></LinkButton>
          </dd>
          <dd>
            <LinkButton
              icon={<ArticleIcon />}
              href="/docs"
              title="docs"
            ></LinkButton>
          </dd>
          <dd>
            <LinkButton
              icon={<TerminalIcon />}
              href="/example"
              title="example"
            ></LinkButton>
          </dd>

          <dd>
            <LinkButton
              icon={<LoyaltyIcon />}
              href="/license"
              title="License"
            ></LinkButton>
          </dd>
          <dd>
            <LinkButton
              icon={<Diversity1Icon />}
              href="/contributing"
              title="Contributing"
            ></LinkButton>
          </dd>
        </dl>
      </div>
    </nav>
  );
};

export default NavMenu;
