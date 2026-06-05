# Group 5 — Project Logbook

## Day 1 — 27 May 2026

### Setup completed
- Imported HEAT v5 source into Eclipse
- Installed GHCi 9.6.7 via GHCup
- Configured HEAT to use /Users/user/.ghcup/bin/ghci
- Set up GitLab remote at https://git.cs.kent.ac.uk/comp7024/g5
- Pushed initial source code commit
- Created GitLab wiki home page with corpus structure

### First code change
- Uncommented keyboard mnemonics for Compile, Interrupt and Test in MainMenu.java
- Committed and pushed to GitLab

### HEAT exploration observations
- Compilation errors shown in red text only — no audio feedback
- Status icon is purely visual (colour change only) — no text or sound
- A blind user has no way to know compilation failed without reading the screen
- Error messages contain complex GHC output — line numbers, arrows, technical text
- No keyboard shortcuts for core actions (compile, test, interrupt)
- Console prompt gives no audio cue when ready for input

### Decisions made
- Will implement TTS for status changes and error messages
- Will add keyboard shortcuts for all core actions
- Will add high contrast mode for partial vision users

## Customer Interaction — 27 May 2026

### Source
Rogério de Lemos (module convenor / client) — verbal guidance given to all groups during project week.

### Guidance received
- Explicitly suggested implementing a colour blindness accessibility feature
- Noted there are multiple categories of colour blindness to consider
- Suggested accommodating at least one specific category

### Our response
- Researched colour blindness categories: Deuteranopia (red-green, most common), Protanopia (red-green variant), Tritanopia (blue-yellow)
- Selected Deuteranopia as our target — most common form, affecting ~6% of males
- HEAT's current colour scheme (red errors, green prompts) is specifically broken for Deuteranopia
- Will implement a colour blind mode using the Wong (2011) accessible colour palette
- Will run Coblis colour blindness simulator on HEAT screenshots as evidence

### Evidence
Verbal instruction given in class to all COMP7024 groups on 27 May 2026.

## TTS Feature — Full Implementation Log — 30 May 2026

### What we built
- TTSManager.java — singleton TTS engine using OS built-in speech
- ErrorSimplifier.java — maps GHCi errors to plain English
- Hooked into WindowManager.java for status announcements
- Hooked into InterpreterToConsole.java for live output reading
- Added TTS_ENABLED setting to Settings.java and SettingsManager.java
- Added startup silence fix in Main.java

### What worked first time
- Status announcements — COMPILEDCORRECT, COMPILEDERROR, UNCOMPILED, EVALUATING, NOPROGRAM all worked immediately after hooking into WindowManager.setStatus()
- OS speech engine detection — macOS 'say', Windows PowerShell, Linux espeak
- Settings persistence — TTS_ENABLED stored in heat.settings

### What failed and how it was fixed

#### Problem 1 — TTS not triggering at all
- Cause: ErrorSimplifier.java was never saved to disk
- Fix: Created file via Terminal cat command

#### Problem 2 — TTS silent despite correct code
- Cause: heat.settings file existed from before TTS_ENABLED was added as a default — so getSetting() returned null
- Fix: Added TTS_ENABLED=true to createDefaultProperties() in SettingsManager.java AND manually added to existing heat.settings file

#### Problem 3 — Startup noise
- HEAT was speaking "Program not compiled", "Evaluating", "No program loaded" repeatedly on launch
- Cause: setStartupComplete() was being called before InterpreterManager.startProcess() finished triggering status changes
- Fix: Added startupComplete boolean flag — TTS silenced until setStartupComplete() called after wm.setVisible() in Main.java with 3 second delay

#### Problem 4 — speak() method killing itself
- Cause: stopSpeaking() was called at the start of every speak() call, killing the previous process before it finished
- Fix: Replaced single-process approach with BlockingQueue — messages queued and spoken in order on a separate thread

#### Problem 5 — bufferChar() not being called
- Cause: processQueue() call was accidentally deleted from speak() during an edit
- Fix: Re-added if (!isSpeaking) { processQueue(); } to speak()

#### Problem 6 — TTSManager.java corrupted
- Cause: Multiple partial edits pasted on top of each other without replacing properly — duplicate methods, unclosed braces
- Fix: Rewrote entire file from scratch with clean structure

#### Problem 7 — "Compilation successful" spoken after runtime errors
- Cause: ConsoleWindow detects GHCi prompt return after expression evaluation and calls setStatusCompiledCorrect() — same as after real compilation
- Fix: Added savedStatus check in WindowManager — only speak "Compilation successful" when transitioning FROM a different state, not when restoring

#### Problem 8 — Result not being spoken
- Cause: GHCi outputs result on same line as prompt e.g. "*MyCode>16" — filter was blocking it
- Fix: Strip prompt prefix using line.substring(line.lastIndexOf(">") + 1) before speaking result

#### Problem 9 — Too much noise spoken
- Lines like "In the expression:", "Suggested fix:", "|", "11 | prop_square..." being spoken as results
- Fix: Added comprehensive filter list in bufferChar() — filters detail lines, pipe characters, line number references, GHCi internal messages

#### Problem 10 — Raw GHCi error header spoken before simplified version
- Lines like "<interactive>:7:8: error: [GHC-39999]" spoken before plain English version
- Fix: Added !line.startsWith("<interactive>") and !line.matches(".*\\.hs:\\d+.*") to error condition

### Final test results
- Launch HEAT — hears "Welcome to HEAT. Open a Haskell file to begin." ✅
- Compile success — hears "Compilation successful. Ready to evaluate." ✅
- Compile failure — hears "Compilation failed. Check the error in the console." ✅
- Type error (square 3.5) — hears "Error: wrong type of value. Check what type your function expects." ✅
- Not in scope (cubbe) — hears "Error: name not found. Check your spelling or missing definition." ✅
- Parse error (broken x = x +) — hears "Error: syntax problem. Check your brackets, indentation, or missing symbols." ✅
- Correct result (square 4) — hears "Result: 16" ✅
- No false "Compilation successful" after runtime errors ✅
- No startup noise ✅

### Known limitations
- TTS toggle not yet in Options UI — enabled via settings file only
- "Evaluating" spoken every time user submits expression — minor noise
- Some complex multi-part GHCi errors only speak the first relevant line
- ErrorSimplifier covers 10 common GHCi errors — more patterns can be added

### Files changed
- src/accessibility/TTSManager.java — new file
- src/accessibility/ErrorSimplifier.java — new file
- src/managers/WindowManager.java — TTS hooks in setStatus()
- src/managers/SettingsManager.java — TTS_ENABLED default
- src/utils/InterpreterToConsole.java — bufferChar hook
- src/utils/Settings.java — TTS_ENABLED constant
- src/Main.java — setStartupComplete() call

---

## 30 May 2026 — User Stories and Acceptance Criteria

### TTS Feature — User Stories

**US-01**
As a blind student, I want HEAT to welcome me when it opens, so that I know the application has loaded successfully without needing to read the screen.

**US-02**
As a blind student, I want HEAT to announce when compilation succeeds, so that I know my program is ready to evaluate without relying on visual status icons.

**US-03**
As a blind student, I want HEAT to announce when compilation fails, so that I know I need to check errors without relying on the red cross icon.

**US-04**
As a blind student, I want compiler errors read aloud in plain English, so that I can understand what went wrong without sighted assistance.

**US-05**
As a blind student, I want evaluation results read aloud, so that I can hear the output of my Haskell expressions without reading the console.

**US-06**
As a blind student, I want TTS enabled by default, so that I can use HEAT immediately without navigating settings menus.

**US-07**
As a sighted student, I want to be able to turn TTS off, so that speech does not distract me during normal use.

### Error Simplification — User Stories

**US-08**
As a novice Haskell student, I want GHCi error messages translated to plain English, so that I can understand what went wrong without knowing Haskell type theory.

**US-09**
As a blind student, I want to hear a plain English explanation instead of raw GHCi output, so that the spoken error is meaningful rather than confusing technical text.

### TTS Acceptance Criteria

| ID | Given | When | Then | Status |
|---|---|---|---|---|
| AC-01 | HEAT launches | App finishes loading | Speaks "Welcome to HEAT. Open a Haskell file to begin." | ✅ Pass |
| AC-02 | TTS enabled, file open | Compilation succeeds | Speaks "Compilation successful. Ready to evaluate." | ✅ Pass |
| AC-03 | TTS enabled, file open | Compilation fails | Speaks "Compilation failed. Check the error in the console." | ✅ Pass |
| AC-04 | TTS enabled | File not yet opened | Speaks "No program loaded. Open a Haskell file to begin." | ✅ Pass |
| AC-05 | TTS enabled | File opened but not compiled | Speaks "Program not compiled. Press compile to load." | ✅ Pass |
| AC-06 | TTS enabled | User types expression | Speaks "Evaluating." | ✅ Pass |
| AC-07 | TTS enabled | Expression returns result | Speaks "Result: " followed by the value | ✅ Pass |
| AC-08 | TTS enabled | Type error occurs | Speaks "Error: wrong type of value. Check what type your function expects." | ✅ Pass |
| AC-09 | TTS enabled | Name not in scope | Speaks "Error: name not found. Check your spelling or missing definition." | ✅ Pass |
| AC-10 | TTS enabled | Parse error occurs | Speaks "Error: syntax problem. Check your brackets, indentation, or missing symbols." | ✅ Pass |
| AC-11 | TTS enabled | Type mismatch occurs | Speaks "Error: type mismatch. Two parts of your code expect different types." | ✅ Pass |
| AC-12 | TTS disabled | Any event occurs | HEAT is completely silent | ⬜ Not yet tested |
| AC-13 | Any state | Multiple messages queued | Messages spoken in order without cutting each other off | ✅ Pass |
| AC-14 | TTS enabled | HEAT launching | No speech during startup sequence | ✅ Pass |
| AC-15 | Any state | TTS unavailable on OS | HEAT continues working silently — no crash | ✅ Pass |

### Error Simplification Acceptance Criteria

| ID | Given | When | Then | Status |
|---|---|---|---|---|
| AC-16 | Any state | "No instance for" error | Speaks "Error: wrong type of value. Check what type your function expects." | ✅ Pass |
| AC-17 | Any state | "Not in scope" error | Speaks "Error: name not found. Check your spelling or missing definition." | ✅ Pass |
| AC-18 | Any state | "parse error" | Speaks "Error: syntax problem. Check your brackets, indentation, or missing symbols." | ✅ Pass |
| AC-19 | Any state | "Couldn't match type" | Speaks "Error: type mismatch. Two parts of your code expect different types." | ✅ Pass |
| AC-20 | Any state | Unknown error | Raw error line spoken as fallback | ✅ Pass |
| AC-21 | Any state | Detail lines e.g. "In the expression:" | Not spoken — filtered out | ✅ Pass |
---

## Repository Housekeeping — 5 June 2026

### Update
- Tightened the project `.gitignore` so Eclipse metadata, compiled output, and macOS `.DS_Store` files stay out of version control.
