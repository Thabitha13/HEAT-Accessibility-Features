# HEAT Functional Test Plan

Each test below is a black-box scenario run against the live application.
Pass/fail is recorded manually by the tester.
Tests cover all three teammates' features: TTS (yours), colour themes (Thabitha), and file accessibility (Goutham).

---

## How to run

1. Build and launch HEAT:
```
cd /path/to/heat_merged
find src -name "*.java" | xargs javac -d bin
cp -r src/icons bin/ && cp -r src/html bin/
java -cp bin Main
```
2. Work through each test in order.
3. Mark each as PASS / FAIL and note any observations.

---

## FT-01: TTS welcome message on startup

**Feature:** TTS startup announcement  
**Steps:**
1. Launch HEAT from a cold start.
2. Wait 3 seconds.

**Expected:** TTS speaks *"Welcome to HEAT. Press Command O to open a Haskell file. Press F1 for all keyboard shortcuts."*  
**Pass criteria:** Message spoken once, no speech before the 2-second delay.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-02: F1 reads all shortcuts

**Feature:** Keyboard guide  
**Steps:**
1. Launch HEAT.
2. Press F1.

**Expected:** TTS reads all 17 shortcuts in order, including *"Command Shift C — toggle colour blind mode"*, *"Command equals — increase font size"*, *"Command minus — decrease font size"*.  
**Pass criteria:** All 17 shortcuts spoken, none missing.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-03: Open file via Cmd+O

**Feature:** Keyboard file open (Goutham)  
**Steps:**
1. Press Cmd+O.
2. Check dialog appears.

**Expected:** Dialog opens. TTS speaks *"Search for a Haskell file. Type the file name and press Enter."*  
**Pass criteria:** Dialog visible, TTS fires.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-04: File search and Cmd+F5 path reading

**Feature:** Keyboard file open — path announcement  
**Steps:**
1. Press Cmd+O.
2. Type a filename (e.g. "Main") and press Enter.
3. Use arrow key to select a result.
4. Press Cmd+F5.

**Expected:** TTS speaks *"Selected file: /full/path/to/Main.hs"*  
**Pass criteria:** Full absolute path spoken.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-05: F5 compiles and TTS announces result

**Feature:** Compile keyboard shortcut + TTS  
**Steps:**
1. Open a valid Haskell file (e.g. one containing `square x = x * x`).
2. Press F5.

**Expected:** TTS speaks *"Evaluating."* then *"Compilation successful. Your program is ready."*  
**Pass criteria:** Both messages spoken in order.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-06: TTS speaks error details on compile failure

**Feature:** Error block TTS  
**Steps:**
1. Open a Haskell file with a type error (e.g. `square x = x + "hello"`).
2. Press F5.

**Expected:** TTS speaks the error header, then the simplified plain-English message, then the raw detail lines including *"In the expression"* lines.  
**Pass criteria:** At least 3 lines spoken for the error, simplified message included.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-07: Colour blind mode toggle via Cmd+Shift+C

**Feature:** Deuteranopia toggle (Thabitha)  
**Steps:**
1. Look at the console — note the prompt colour (green) and error colour (red).
2. Press Cmd+Shift+C.
3. Trigger an error (type `1 + "a"` in console and press Enter).

**Expected:**
- TTS speaks *"Colour blind mode enabled. Console colours adjusted for deuteranopia."*
- Error text turns orange (RGB 230, 159, 0) instead of red.
- Prompt turns blue (RGB 0, 114, 178) instead of green.

**Pass criteria:** TTS fires, colours visually changed.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-08: Colour blind mode untoggle

**Feature:** Deuteranopia toggle off  
**Steps:**
1. Enable colour blind mode (Cmd+Shift+C).
2. Press Cmd+Shift+C again.

**Expected:** TTS speaks *"Colour blind mode disabled. Normal colours restored."* Console prompt returns to green, errors to red.  
**Pass criteria:** TTS fires with correct message, colours restored.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-09: Colour blind mode persists after Options save

**Feature:** Settings persistence  
**Steps:**
1. Enable colour blind mode via Cmd+Shift+C.
2. Open Options (Cmd+D), click Apply.
3. Close and relaunch HEAT.

**Expected:** Deuteranopia mode still active on next launch — console colours are orange/blue.  
**Pass criteria:** Setting survives restart.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-10: Font size increase via Cmd+=

**Feature:** Font size accessibility (Thabitha)  
**Steps:**
1. Note current font size in the accessibility panel (e.g. "Size: 14pt").
2. Press Cmd+= three times.

**Expected:** Font size increases by 2pt each press. Label updates to "Size: 20pt". Text visibly larger in editor and console.  
**Pass criteria:** Label correct, text visibly larger.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-11: Font size decrease via Cmd+-

**Feature:** Font size accessibility (Thabitha)  
**Steps:**
1. Press Cmd+- twice.

**Expected:** Font size decreases by 2pt each press. Label updates accordingly.  
**Pass criteria:** Label correct, text visibly smaller.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-12: Font size does not go below minimum

**Feature:** Font size boundary  
**Steps:**
1. Press Cmd+- repeatedly until the A− button greys out.

**Expected:** Font size stops at 8pt. Button becomes disabled. No crash.  
**Pass criteria:** Minimum enforced, no exception thrown.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-13: F10 moves focus to toolbar

**Feature:** Toolbar keyboard navigation (yours)  
**Steps:**
1. Click in the editor.
2. Press F10.
3. Press Tab several times.

**Expected:** TTS speaks *"Toolbar focused. Press Tab to move between buttons."* Focus moves through toolbar buttons. Each button name and shortcut is announced by TTS.  
**Pass criteria:** Focus moves, TTS announces each button.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## FT-14: High contrast mode via Options

**Feature:** High contrast (Goutham)  
**Steps:**
1. Open Options (Cmd+D).
2. Go to Accessibility tab.
3. Check "High contrast mode" and click Apply.

**Expected:** Editor and console background colours change to high-contrast theme. Setting saved.  
**Pass criteria:** Visual change confirmed, setting persists after Apply.  
**Result:** [ ] PASS  [ ] FAIL  
**Notes:**

---

## Summary

| Test | Feature | Owner | Result |
|------|---------|-------|--------|
| FT-01 | TTS startup | Yours | |
| FT-02 | F1 shortcut list | Yours | |
| FT-03 | Cmd+O dialog | Goutham | |
| FT-04 | Cmd+F5 path read | Goutham | |
| FT-05 | F5 compile TTS | Yours | |
| FT-06 | Error detail TTS | Yours | |
| FT-07 | Colour blind on | Thabitha | |
| FT-08 | Colour blind off | Thabitha | |
| FT-09 | Setting persistence | Thabitha | |
| FT-10 | Font increase | Thabitha | |
| FT-11 | Font decrease | Thabitha | |
| FT-12 | Font minimum | Thabitha | |
| FT-13 | F10 toolbar focus | Yours | |
| FT-14 | High contrast | Goutham | |
