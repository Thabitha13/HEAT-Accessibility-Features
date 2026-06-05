# HEAT — Haskell Educational Advancement Tool
## Accessibility Extensions — Group 5, COMP7024

This repository contains HEAT v5 extended with accessibility features for students with visual disabilities.

The technical report is at `documentation/Report.pdf`.

---

## Features

- **Text-to-Speech (TTS)** — speaks every status change, compilation result, and GHCi error in plain English
- **Error Simplification** — maps raw GHCi errors to plain English (10 error patterns)
- **Keyboard Navigation** — F5 compile, F6 interrupt, F7 test, F10 toolbar focus, F1 shortcut guide, Cmd+A select all
- **Colour Blind Mode** — Deuteranopia-safe palette (Cmd+Shift+C to toggle)
- **Font Size Control** — A+ and A- buttons with slider, always visible above the editor
- **Splash Screen** — keyboard-navigable startup screen (Cmd+N new file, Cmd+O open file, Escape dismiss)
- **Keyboard File Dialogs** — search-based file open, new file, and file explorer dialogs

---

## Prerequisites

- Java JDK 11 or later
- GHCi installed via GHCup: https://www.haskell.org/ghcup/

On macOS with GHCup, GHCi is typically at:
```
/Users/[username]/.ghcup/bin/ghci
```

---

## Clone and Run

```bash
git clone https://git.cs.kent.ac.uk/comp7024/G5.git
cd G5
mkdir -p bin
find src -name "*.java" | xargs javac -d bin
cp -r src/icons bin/
cp -r src/html bin/
java -cp bin Main
```

On first launch HEAT will ask for the path to your GHCi interpreter. Enter the full path, click Continue, and HEAT will start.

---

## Keyboard Shortcuts

| Shortcut | Action |
|---|---|
| Cmd+O | Open a Haskell file |
| F5 | Compile and load |
| F6 | Interrupt |
| F7 | Run tests |
| F10 | Focus toolbar |
| Tab | Move between toolbar buttons |
| F1 | Read all shortcuts aloud |
| Cmd+A | Select all |
| Cmd+Shift+C | Toggle colour blind mode |
| Cmd+= | Increase font size |
| Cmd+- | Decrease font size |

---

## Running Unit Tests

```bash
cd test/unit
for f in ErrorSimplifierTest ColorThemeManagerTest FontSizeManagerTest; do
  javac -cp ../../src ${f}.java && java -cp .:../../src $f
  echo ""
done

javac KeyboardDialogsTest.java && java KeyboardDialogsTest
```

All 51 tests should pass.

---

## TTS Settings

TTS is enabled by default. To disable it, open `~/heat.settings` and set:
```
TTS_ENABLED=false
```

---

## Project Structure

```
src/                        Java source files
  accessibility/            TTS, error simplifier, colour theme, font size, keyboard guide
  managers/                 Window, action, settings, interpreter managers
  view/dialogs/             Keyboard file dialogs
  view/panels/              Accessibility font size panel
  view/toolbars/            Menu bar and toolbar
  view/windows/             Editor, console, splash screen, options
  utils/                    Settings, resources, parser
bin/                        Compiled classes (generated on build)
test/
  unit/                     51 automated unit tests
  functional/               16 manual functional test scenarios
documentation/              Technical report (Report.pdf), UML diagrams (PNG and PlantUML source)
```

---

## Team

| Name | Student ID | Feature |
|---|---|---|
| Yarushah | ys321 | TTS, keyboard navigation, error simplification, final merge |
| Goutham | gs100 | Splash screen, keyboard file dialogs, UML diagrams |
| Thabitha | ta106 | Colour blind mode, font size control, icon enlargement |
| Rinku Kumari | rk105 | Assigned settings integration (not delivered) |
