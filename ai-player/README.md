# AI Player for VASSAL — developer walkthrough

This folder holds the scripts and notes for the AI-player work on VASSAL 3.8.1.
It is written for someone who has not built VASSAL from source before.

## What you have on disk

| Location | What it is |
|---|---|
| `vassal\` (this repository) | The VASSAL source code, on a branch based on the 3.8.1 release |
| `vassal\ai-player\` | These notes plus `build.bat` and `run.bat` |
| `vassal-dev-conf\` (next to the repository) | Settings and logs for the development copy only |
| `test-modules\` (next to the repository) | Copies of game modules used for testing |

The development copy is completely separate from the VASSAL you installed
under `C:\Program Files`. It keeps its own preferences, so you can run both at
once and nothing you do here changes your normal games. The title bar tells
them apart: the development copy says **VASSAL 3.8.1**.

## One-time requirement

Java 17 or later must be installed and on the PATH. To check, open a terminal
and run `java -version`. (This machine has Java 24, which works.)

## Build it

Double-click `ai-player\build.bat`.

A console window shows Maven compiling the code. The first run downloads
libraries and takes a few minutes; later runs take about a minute. It ends with
either "Build finished" or "Build FAILED". You must rebuild every time the
source code changes — a running VASSAL does not pick up changes on its own.

## Run it

Double-click `ai-player\run.bat`.

The Module Manager opens. This is VASSAL's home screen: a list of game modules.

1. The first time, the list is empty. Choose **File > Open Module...** and pick
   a `.vmod` file, for example `test-modules\Circus_Maximus_2_5.vmod`.
2. The module is added to the list and a game window opens.
3. If a wizard appears, pick a name and password (anything will do for
   testing), then choose **Start a new game offline** and a side.

Shortcut: drag a `.vmod` file onto `run.bat` to skip the Module Manager and
open that module straight away.

Close the windows normally when you are done. Close all development windows
before running `build.bat` again.

## Trying the AI Player features

AI Player support is off by default and is switched on per module.

1. Open a module, then choose **File > Preferences** in the game window.
2. Open the **AI Player** tab and tick **Enable AI Player (experimental)**.
3. An **AI Player** button appears in the toolbar, after the Undo and Step
   buttons. Clicking it prints the AI Player status in the chat area.
4. Untick the box to hide the button again. The choice is remembered.

## When something goes wrong

- **"The development build was not found"** — run `build.bat` first.
- **Nothing appears** — open `vassal-dev-conf\errorLog-3.8.1` in Notepad. Every
  start is logged there; the reason for a failure is usually in the last lines.
- **Build FAILED** — scroll up in the console to the first line marked `ERROR`.

## How the work is organised

- `origin` is the fork where this work is pushed; `upstream` is the official
  VASSAL repository.
- `ai-player/base-3.8.1` is the untouched 3.8.1 release.
- `ai-player/main` collects finished stages. Each stage is developed on its own
  branch and merged here once it has been tried out.

## Stages

Each stage ends with something you can see and try in the running application.

| # | Stage | What you will be able to see |
|---|---|---|
| 0 | Build and launch locally | The development copy starts and opens a module (done) |
| 1 | AI Player switch | An "AI Player" preferences tab and a toolbar button that appear only when enabled (done) |
| 2 | Read the game | A "Show what the AI sees" window listing maps, pieces and locations as text |
| 3 | Respect hidden information | The same window, viewed from one side, hides masked and private pieces |
| 4 | Watch the game | A live event list: "X moved from A to B", chat lines, dice |
| 5 | Act on the game | Test buttons that move a piece or run a piece command without the mouse |
| 6 | Answer prompts | Pop-up questions are routed to the AI instead of blocking |
| 7 | Connect Claude | Claude can read the game and make a move through the bridge |
| 8 | Turn hand-off and thinking display | "Hand turn to AI" button, "thinking" banner, stop button |
| 9 | Difficulty and play style | Difficulty levels and aggressive / docile / strategic settings |
| 10 | Module rules profile | A per-module file that teaches the AI the rules of that game |
