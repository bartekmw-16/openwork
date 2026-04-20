/**
 * Web Worker for image compression to offload CPU-intensive work from the main thread.
 * This prevents UI freezing when users attach large images.
 */

const IMAGE_COMPRESS_MAX_PX = 2048;
const IMAGE_COMPRESS_QUALITY = 0.82;
const IMAGE_COMPRESS_TARGET_BYTES = 1_500_000;

interface CompressRequest {
  id: string;
  file: File;
}

interface CompressResponse {
  id: string;
  success: boolean;
  result?: File;
  error?: string;
}

self.onmessage = async (event: MessageEvent<CompressRequest>) => {
  const { id, file } = event.data;

  try {
    // Skip GIFs (animated) and already-small images
    if (file.type === "image/gif" || file.size <= IMAGE_COMPRESS_TARGET_BYTES) {
      self.postMessage({ id, success: true, result: file } as CompressResponse);
      return;
    }

    const bitmap = await createImageBitmap(file);
    const { width, height } = bitmap;

    // Calculate scaled dimensions
    const maxDim = Math.max(width, height);
    const scale = maxDim > IMAGE_COMPRESS_MAX_PX ? IMAGE_COMPRESS_MAX_PX / maxDim : 1;
    const targetW = Math.round(width * scale);
    const targetH = Math.round(height * scale);

    // Use OffscreenCanvas (available in workers)
    const offscreen = new OffscreenCanvas(targetW, targetH);
    const ctx = offscreen.getContext("2d");

    if (!ctx) {
      // Fallback: return original
      self.postMessage({ id, success: true, result: file } as CompressResponse);
      bitmap.close();
      return;
    }

    ctx.drawImage(bitmap, 0, 0, targetW, targetH);
    const blob = await offscreen.convertToBlob({
      type: "image/jpeg",
      quality: IMAGE_COMPRESS_QUALITY
    });

    bitmap.close();

    if (!blob || blob.size >= file.size) {
      // Compression didn't help
      self.postMessage({ id, success: true, result: file } as CompressResponse);
      return;
    }

    const ext = file.name.replace(/\.[^.]+$/, "");
    const compressedFile = new File([blob], `${ext || "image"}.jpg`, { type: "image/jpeg" });

    self.postMessage({ id, success: true, result: compressedFile } as CompressResponse);
  } catch (error) {
    self.postMessage({
      id,
      success: false,
      error: error instanceof Error ? error.message : "Compression failed"
    } as CompressResponse);
  }
};
