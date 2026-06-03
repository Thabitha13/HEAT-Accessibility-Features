# Group 5 — Team Logbook
## COMP7024 Software Engineering — HEAT Accessibility Project

---

## Day 1 — 27 May 2026

### Team Setup
- Project assigned: HEAT IDE accessibility for visually impaired users
- Group repository created: https://git.cs.kent.ac.uk/comp7024/g5
- HEAT v5 source code downloaded from Moodle and pushed to GitLab

### Decisions Made
- Selected TTS, colour blind mode, high contrast, and keyboard navigation as our four features
- Chose Kanban-based development process with daily standups
- Agreed: one feature per team member, all members must have commits

### Customer Interaction
- Rogério de Lemos (client) gave verbal guidance to all groups
- Explicitly recommended implementing a colour blindness feature
- Recommended accommodating at least one specific colour blindness category

### Individual Progress
- ys321: Set up project, installed GHCi, pushed to GitLab, uncommented keyboard mnemonics in MainMenu.java

---

## Day 2 — 30 May 2026

### Standup
- ys321: Working on TTS feature
- Other members: TBC after group meeting

### Decisions Made
- Combined TTS with error simplification — speak plain English errors instead of raw GHCi output
- Rejected Speech-to-Text (STT) — no built-in Java support, Haskell syntax too complex to dictate, no reliable undo mechanism
- TTS enabled by default in settings — blind users should not need to navigate menus to enable accessibility

### Individual Progress
- ys321: Completed TTS feature — TTSManager.java, ErrorSimplifier.java, hooked into WindowManager, InterpreterToConsole, SettingsManager, Main.java

### Features Status
| Feature | Owner | Status |
|---|---|---|
| TTS + Error Simplification | ys321 | ✅ Complete |
| Colour Blind Mode | TBC | ⬜ Not started |
| High Contrast Mode | TBC | ⬜ Not started |
| Keyboard Navigation | TBC | ⬜ Not started |

### Test Results Today
- Launch HEAT — "Welcome to HEAT. Open a Haskell file to begin." ✅
- Compilation successful announced ✅
- Compilation failed announced ✅
- Type error spoken in plain English ✅
- Not in scope error spoken in plain English ✅
- Parse error spoken in plain English ✅
- Correct result spoken e.g. "Result: 16" ✅

---

## Meeting Notes Template
*(Fill in after each team meeting)*

### Date:
### Attendees:
### Decisions:
### Action items:
| Task | Assigned to | Due |
|---|---|---|
| | | |


---

## Meeting 2 — (fill in date)

### Attendees
- Yarusha (ys321)
- Thabitha
- Gautham
- Rinku

### Duration


### Location


### Decisions
-
-
-

### Action Items
| Task | Assigned to | Due |
|---|---|---|
| Implement Colour Blind Mode | TBC | TBC |
| Implement High Contrast Mode | TBC | TBC |
| Implement Keyboard Navigation | TBC | TBC |
| Set up GitLab wiki pages | Yarusha | 31 May |
| Email Rogério for stakeholder meeting | Yarusha | Tonight |
| Remove debug lines from TTSManager | Yarusha | 31 May |

### Feature Assignment (confirm with team)
| Feature | Owner | Branch name |
|---|---|---|
| TTS + Error Simplification | Yarusha | feature/tts |
| Colour Blind Mode | TBC | feature/colour-blind |
| High Contrast Mode | TBC | feature/high-contrast |
| Keyboard Navigation | TBC | feature/keyboard-nav |

