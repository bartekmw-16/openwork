import { Show, type Component } from "solid-js";
import type { SessionIcon } from "../../types";

type SessionIconDisplayProps = {
  icon: SessionIcon;
  size?: number;
  class?: string;
};

/**
 * SessionIconDisplay Component
 *
 * Renders either an emoji or a custom image icon.
 * Used in sidebar session list, workspace headers, and project displays.
 */
const SessionIconDisplay: Component<SessionIconDisplayProps> = (props) => {
  const size = () => props.size ?? 16;

  return (
    <div
      class={`inline-flex shrink-0 items-center justify-center ${props.class ?? ""}`}
      style={{
        width: `${size()}px`,
        height: `${size()}px`,
      }}
      aria-hidden="true"
    >
      <Show
        when={props.icon.type === "emoji"}
        fallback={
          <Show when={props.icon.type === "image"}>
            <img
              src={(props.icon as { type: "image"; dataUrl: string }).dataUrl}
              alt=""
              class="h-full w-full rounded object-cover"
              style={{
                "image-rendering": size() <= 24 ? "pixelated" : "auto",
              }}
            />
          </Show>
        }
      >
        <span
          class="flex items-center justify-center"
          style={{
            "font-size": `${size() * 0.8}px`,
            "line-height": "1",
          }}
        >
          {(props.icon as { type: "emoji"; value: string }).value}
        </span>
      </Show>
    </div>
  );
};

export default SessionIconDisplay;
