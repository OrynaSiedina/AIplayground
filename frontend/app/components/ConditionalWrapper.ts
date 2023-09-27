"use client";
import { ReactElement } from "react";

export interface ConditionalWrapperProps {
  condition: boolean;
  children: ReactElement;
  wrapper: (children: ReactElement) => ReactElement | null;
}

export const ConditionalWrapper = ({
  condition,
  wrapper,
  children,
}: ConditionalWrapperProps) => (condition ? wrapper(children) : children);
