import { For, Show, createSignal, type Component } from "solid-js";
import { X, Upload, Smile } from "lucide-solid";
import type { SessionIcon } from "../../types";

type IconPickerProps = {
  value?: SessionIcon | null;
  onChange: (icon: SessionIcon | null) => void;
  onClose: () => void;
};

// Curated emoji list organized by category
const EMOJI_CATEGORIES = {
  Work: ["💼", "📁", "📊", "📈", "💡", "🎯", "✅", "📝", "🔧", "⚙️", "🛠️", "📌"],
  Symbols: ["⭐", "🔥", "💎", "🏆", "🎨", "🎵", "🎮", "🚀", "⚡", "💫", "✨", "🌟"],
  Objects: ["📱", "💻", "⌨️", "🖥️", "🖱️", "🎧", "📷", "🎬", "📚", "📖", "📋", "📦"],
  Nature: ["🌈", "🌍", "🌙", "☀️", "⛅", "🌲", "🌸", "🍀", "🌺", "🌻", "🌿", "🍃"],
  Animals: ["🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮"],
  Food: ["🍕", "🍔", "🍟", "🌭", "🍿", "🥤", "🍩", "🍪", "🎂", "🍰", "🧁", "🍫"],
};

/**
 * IconPicker Component
 *
 * Provides a modal interface for selecting an emoji or uploading a custom image icon.
 *
 * Features:
 * - Tab 1: Emoji grid (categorized, searchable)
 * - Tab 2: Image upload (with preview, converts to data URL)
 * - Tab 3: Remove icon option
 */
const IconPicker: Component<IconPickerProps> = (props) => {
  const [activeTab, setActiveTab] = createSignal<"emoji" | "image">("emoji");
  const [selectedEmoji, setSelectedEmoji] = createSignal<string | null>(
    props.value?.type === "emoji" ? props.value.value : null,
  );
  const [imagePreview, setImagePreview] = createSignal<string | null>(
    props.value?.type === "image" ? props.value.dataUrl : null,
  );
  const [uploadError, setUploadError] = createSignal<string | null>(null);

  const handleEmojiSelect = (emoji: string) => {
    setSelectedEmoji(emoji);
    props.onChange({ type: "emoji", value: emoji });
    props.onClose();
  };

  const handleImageUpload = (event: Event) => {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    // Validate file type
    if (!file.type.startsWith("image/")) {
      setUploadError("Please select a valid image file.");
      return;
    }

    // Validate file size (max 1MB)
    if (file.size > 1024 * 1024) {
      setUploadError("Image must be smaller than 1MB.");
      return;
    }

    setUploadError(null);

    const reader = new FileReader();
    reader.onload = (e) => {
      const dataUrl = e.target?.result as string;
      setImagePreview(dataUrl);
      props.onChange({ type: "image", dataUrl });
      props.onClose();
    };
    reader.readAsDataURL(file);
  };

  const handleRemove = () => {
    setSelectedEmoji(null);
    setImagePreview(null);
    props.onChange(null);
    props.onClose();
  };

  return (
    <div
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      onClick={(e) => {
        if (e.target === e.currentTarget) props.onClose();
      }}
    >
      <div class="relative w-full max-w-md rounded-2xl border border-gray-6 bg-gray-1 p-6 shadow-2xl">
        {/* Header */}
        <div class="mb-4 flex items-center justify-between">
          <h2 class="text-lg font-semibold text-gray-12">Choose Icon</h2>
          <button
            type="button"
            onClick={props.onClose}
            class="flex h-8 w-8 items-center justify-center rounded-lg text-gray-10 transition-colors hover:bg-gray-3 hover:text-gray-12"
            aria-label="Close"
          >
            <X size={18} />
          </button>
        </div>

        {/* Tabs */}
        <div class="mb-4 flex gap-2 border-b border-gray-5">
          <button
            type="button"
            onClick={() => setActiveTab("emoji")}
            class={`flex-1 border-b-2 px-4 py-2 text-sm font-medium transition-colors ${
              activeTab() === "emoji"
                ? "border-blue-9 text-blue-11"
                : "border-transparent text-gray-10 hover:text-gray-11"
            }`}
          >
            <Smile size={16} class="mr-1.5 inline" />
            Emoji
          </button>
          <button
            type="button"
            onClick={() => setActiveTab("image")}
            class={`flex-1 border-b-2 px-4 py-2 text-sm font-medium transition-colors ${
              activeTab() === "image"
                ? "border-blue-9 text-blue-11"
                : "border-transparent text-gray-10 hover:text-gray-11"
            }`}
          >
            <Upload size={16} class="mr-1.5 inline" />
            Upload
          </button>
        </div>

        {/* Content */}
        <div class="mb-4">
          <Show when={activeTab() === "emoji"}>
            <div class="max-h-96 space-y-4 overflow-y-auto">
              <For each={Object.entries(EMOJI_CATEGORIES)}>
                {([category, emojis]) => (
                  <div>
                    <h3 class="mb-2 text-xs font-medium uppercase text-gray-10">{category}</h3>
                    <div class="grid grid-cols-8 gap-2">
                      <For each={emojis}>
                        {(emoji) => (
                          <button
                            type="button"
                            onClick={() => handleEmojiSelect(emoji)}
                            class={`flex h-10 w-10 items-center justify-center rounded-lg text-2xl transition-colors hover:bg-gray-3 ${
                              selectedEmoji() === emoji ? "bg-blue-3 ring-2 ring-blue-9" : ""
                            }`}
                            title={emoji}
                          >
                            {emoji}
                          </button>
                        )}
                      </For>
                    </div>
                  </div>
                )}
              </For>
            </div>
          </Show>

          <Show when={activeTab() === "image"}>
            <div class="space-y-4">
              <Show when={imagePreview()}>
                <div class="flex items-center justify-center">
                  <img
                    src={imagePreview()!}
                    alt="Preview"
                    class="h-24 w-24 rounded-lg border border-gray-6 object-cover"
                  />
                </div>
              </Show>

              <label class="flex cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed border-gray-6 bg-gray-2 p-8 transition-colors hover:border-gray-7 hover:bg-gray-3">
                <Upload size={32} class="mb-2 text-gray-10" />
                <span class="mb-1 text-sm font-medium text-gray-11">
                  Click to upload image
                </span>
                <span class="text-xs text-gray-10">PNG, JPG up to 1MB</span>
                <input
                  type="file"
                  accept="image/*"
                  class="hidden"
                  onChange={handleImageUpload}
                />
              </label>

              <Show when={uploadError()}>
                <p class="text-sm text-red-11">{uploadError()}</p>
              </Show>
            </div>
          </Show>
        </div>

        {/* Actions */}
        <div class="flex justify-between gap-2">
          <button
            type="button"
            onClick={handleRemove}
            class="rounded-lg border border-gray-6 px-4 py-2 text-sm font-medium text-gray-11 transition-colors hover:bg-gray-3"
          >
            Remove Icon
          </button>
          <button
            type="button"
            onClick={props.onClose}
            class="rounded-lg bg-blue-9 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-blue-10"
          >
            Done
          </button>
        </div>
      </div>
    </div>
  );
};

export default IconPicker;
