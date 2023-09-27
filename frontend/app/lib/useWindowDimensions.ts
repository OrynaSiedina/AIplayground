import { useEffect, useState } from "react";

type WindowDimension = {
  width: number | undefined;
  height: number | undefined;
  isDetermined: boolean;
};

const useWindowDimensions = (): WindowDimension => {
  const [windowDimensions, setWindowDimensions] = useState<WindowDimension>({
    width: undefined,
    height: undefined,
    isDetermined: false,
  });

  useEffect(() => {
    function handleResize(): void {
      setWindowDimensions({
        width: window.innerWidth,
        height: window.innerHeight,
        isDetermined: true,
      });
    }

    handleResize();
    window.addEventListener("resize", handleResize);
    return (): void => window.removeEventListener("resize", handleResize);
  }, []);

  return windowDimensions;
};

export default useWindowDimensions;
