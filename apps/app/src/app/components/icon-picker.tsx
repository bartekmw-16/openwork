import { createSignal, For, Show } from "solid-js";
import { X } from "lucide-solid";
import type { SessionIcon } from "../types";

type Props = {
  icon?: SessionIcon | null;
  onSelect: (icon: SessionIcon | null) => void;
  onClose: () => void;
};

const EMOJI_CATEGORIES = {
  "Popular": ["🚀", "💡", "✨", "🔥", "⚡", "🎯", "📌", "⭐", "🌟", "💎", "🔮", "🎨"],
  "Nature": ["🌸", "🌺", "🌻", "🌿", "🍀", "🌵", "🌴", "🌊", "🔥", "❄️", "🌈", "☀️"],
  "Animals": ["🐱", "🐶", "🦊", "🐼", "🐨", "🦁", "🐯", "🐸", "🐙", "🦋", "🐝", "🐞"],
  "Objects": ["💻", "📱", "⌨️", "🖱️", "📷", "🎬", "📚", "🛠️", "🔧", "⚙️", "💡", "📎"],
  "Food": ["🍕", "🍔", "🍟", "🌮", "🍣", "🍰", "🧁", "🍩", "☕", "🍵", "🍺", "🍷"],
  "Symbols": ["❤️", "💙", "💚", "💛", "🧡", "💜", "🖤", "✅", "❌", "⚠️", "💯", "🎵"],
};

// Common solid-color emojis for avatars
const SIMPLE_ICONS = [
  "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "0️⃣",
  "🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "⚫", "⚪", "🟤", "⬛",
];

export default function IconPicker(props: Props) {
  const [activeCategory, setActiveCategory] = createSignal("Popular");
  const [customInput, setCustomInput] = createSignal("");

  const handleEmojiClick = (emoji: string) => {
    const icon: SessionIcon = { type: "emoji", value: emoji };
    props.onSelect(icon);
  };

  const handleRemoveIcon = () => {
    props.onSelect(null);
  };

  const handleCustomSubmit = (e: Event) => {
    e.preventDefault();
    const input = customInput().trim();
    if (input) {
      handleEmojiClick(input);
    }
  };

  const emojis = EMOJI_CATEGORIES[activeCategory() as keyof typeof EMOJI_CATEGORIES] || [];

  return (
    <div class="absolute z-50 mt-1 w-72 rounded-lg border border-dls-border bg-dls-surface shadow-lg">
      {/* Header */}
      <div class="flex items-center justify-between border-b border-dls-border px-3 py-2">
        <span class="text-sm font-medium">Choose Icon</span>
        <button
          type="button"
          class="rounded p-1 hover:bg-gray-3"
          onClick={props.onClose}
        >
          <X size={14} />
        </button>
      </div>

      {/* Categories */}
      <div class="flex flex-wrap gap-1 border-b border-dls-border px-2 py-2">
        <For each={Object.keys(EMOJI_CATEGORIES)}>
          {(category) => (
            <button
              type="button"
              class={`rounded-full px-2 py-0.5 text-xs ${
                activeCategory() === category
                  ? "bg-dls-accent text-white"
                  : "hover:bg-gray-2"
              }`}
              onClick={() => setActiveCategory(category)}
            >
              {category}
            </button>
          )}
        </For>
      </div>

      {/* Emoji Grid */}
      <div class="grid grid-cols-6 gap-1 p-2 max-h-40 overflow-y-auto">
        <For each={emojis}>
          {(emoji) => (
            <button
              type="button"
              class="flex h-8 w-8 items-center justify-center rounded text-lg hover:bg-gray-2"
              onClick={() => handleEmojiClick(emoji)}
            >
              {emoji}
            </button>
          )}
        </For>
      </div>

      {/* Simple Icons Row */}
      <div class="border-t border-dls-border px-2 py-2">
        <div class="mb-1 text-xs text-gray-9">Solid Colors</div>
        <div class="flex flex-wrap gap-1">
          <For each={SIMPLE_ICONS}>
            {(emoji) => (
              <button
                type="button"
                class="flex h-7 w-7 items-center justify-center rounded text-lg hover:bg-gray-2"
                onClick={() => handleEmojiClick(emoji)}
              >
                {emoji}
              </button>
            )}
          </For>
        </div>
      </div>

      {/* Custom Input */}
      <div class="border-t border-dls-border px-2 py-2">
        <form onSubmit={handleCustomSubmit} class="flex gap-2">
          <input
            type="text"
            class="flex-1 rounded border border-dls-border px-2 py-1 text-sm"
            placeholder="Type emoji..."
            value={customInput()}
            onInput={(e) => setCustomInput(e.currentTarget.value)}
            maxLength={2}
          />
          <button
            type="submit"
            class="rounded bg-dls-accent px-3 py-1 text-sm text-white"
          >
            Add
          </button>
        </form>
      </div>

      {/* Remove Button */}
      <Show when={props.icon}>
        <div class="border-t border-dls-border px-2 py-2">
          <button
            type="button"
            class="w-full rounded border border-red-6 px-3 py-1.5 text-sm text-red-11 hover:bg-red-2"
            onClick={handleRemoveIcon}
          >
            Remove Icon
          </button>
        </div>
      </Show>
    </div>
  );
}