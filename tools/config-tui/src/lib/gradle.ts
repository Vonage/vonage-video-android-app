import { spawn } from "node:child_process";
import { resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = dirname(fileURLToPath(import.meta.url));
// From src/lib/ → config-tui → tools → project root
const projectRoot = resolve(__dirname, "../../../..");

export type GradleTask = "generateVonageConfig" | "generateTheme" | "installDebug";

export interface GradleResult {
  success: boolean;
  output: string;
  durationMs: number;
}

export type ProgressCallback = (line: string) => void;

export function runGradleTask(task: GradleTask, onProgress?: ProgressCallback): Promise<GradleResult> {
  return new Promise((res) => {
    const cmd = process.platform === "win32" ? "gradlew.bat" : "./gradlew";
    const args: string[] = task === "generateTheme"
      ? ["clean", ":vonage-video-ui-compose:generateTheme"]
      : task === "generateVonageConfig"
        ? ["clean", "generateVonageConfig"]
        : [task];

    const startTime = Date.now();

    const child = spawn(cmd, [...args, "--console=plain"], {
      cwd: projectRoot,
      shell: true,
      env: { ...process.env },
    });

    let output = "";

    child.stdout?.on("data", (d) => {
      const chunk = d.toString();
      output += chunk;
      if (onProgress) {
        const lines = chunk.split("\n").filter((l: string) => l.trim());
        for (const line of lines) {
          onProgress(line.trim());
        }
      }
    });

    child.stderr?.on("data", (d) => {
      const chunk = d.toString();
      output += chunk;
      if (onProgress) {
        const lines = chunk.split("\n").filter((l: string) => l.trim());
        for (const line of lines) {
          onProgress(line.trim());
        }
      }
    });

    child.on("close", (code) => {
      res({ success: code === 0, output: output.trim(), durationMs: Date.now() - startTime });
    });
    child.on("error", (err) => {
      res({ success: false, output: err.message, durationMs: Date.now() - startTime });
    });
  });
}
