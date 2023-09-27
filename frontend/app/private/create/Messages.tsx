import { useEffect, useState } from "react";

const loadingMessages = [
  "Decoding the prompt...",
  "Thinking...",
  "Thinking some more...",
  "Did I leave the stove on?",
  "Probably not...",
  "Give me a second...",
  "Almost done",
  "I need to think about this...",
];

export function LoadingMessage() {
  let intervalId: NodeJS.Timeout;
  const [loadingMessageIndex, setLoadingMessageIndex] = useState(0);

  useEffect(() => {
    intervalId = setInterval(() => {
      setLoadingMessageIndex(
        (prevIndex) => (prevIndex + 1) % loadingMessages.length,
      );
    }, 3000);
    return () => clearInterval(intervalId);
  }, []);

  return (
    <div style={{ fontSize: "2rem" }}>
      {loadingMessages[loadingMessageIndex]}
    </div>
  );
}
