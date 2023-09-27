"use client";
import { usePathname, useRouter } from "next/navigation";
import {
  JSXElementConstructor,
  ReactElement,
  ReactNode,
  useEffect,
  useState,
} from "react";
import { useAuth } from "@/app/lib";
import { CenteredCircularProgress } from "@/app/components";
import * as React from "react";

export interface RouteGuardProps {
  children:
    | ReactElement<unknown, string | JSXElementConstructor<unknown>>
    | ReactNode;
}

export function RouteGuard({ children }: RouteGuardProps) {
  const router = useRouter();
  const [authorized, setAuthorized] = useState(false);
  const { isLoggedIn } = useAuth();
  const path = usePathname();

  useEffect(() => {
    const authCheck = () => {
      setAuthorized(false);
      if (!isLoggedIn) {
        void router.push("/authorization");
      } else {
        setAuthorized(true);
      }
    };

    authCheck();
  }, [path]);

  return authorized ?<>{children}</> : <CenteredCircularProgress />;
}
