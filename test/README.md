# HEAT Test Suite

```
test/
├── unit/
│   ├── ErrorSimplifierTest.java   — 13 tests for all error pattern mappings
│   ├── ColorThemeManagerTest.java — 9 tests for colour toggle and colour values
│   └── FontSizeManagerTest.java   — 6 tests for font size boundaries and constants
└── functional/
    └── FunctionalTestPlan.md      — 14 manual black-box test scenarios
```

---

## Running unit tests

From the `test/unit/` directory:

```bash
# ErrorSimplifier
javac -cp ../../src ErrorSimplifierTest.java
java -cp .:../../src ErrorSimplifierTest

# ColorThemeManager
javac -cp ../../src ColorThemeManagerTest.java
java -cp .:../../src ColorThemeManagerTest

# FontSizeManager
javac -cp ../../src FontSizeManagerTest.java
java -cp .:../../src FontSizeManagerTest
```

Or run all three in one go:

```bash
cd test/unit
for f in ErrorSimplifierTest ColorThemeManagerTest FontSizeManagerTest; do
  javac -cp ../../src ${f}.java && java -cp .:../../src $f
  echo ""
done
```

---

## Running functional tests

Open `test/functional/FunctionalTestPlan.md` and work through each scenario
against the running application. Mark each as PASS or FAIL.

---

## Why these tests were chosen

**Unit tests** target the three pure logic classes that have no Swing/GUI
dependencies — `ErrorSimplifier`, `ColorThemeManager`, and `FontSizeManager`.
These can be compiled and run without launching the application.
Manager classes like `WindowManager` and `ActionManager` are not unit-tested
here because they require a live Swing environment; those are covered by
the functional tests instead.

**Functional tests** cover every user-visible feature end-to-end: TTS output,
keyboard shortcuts, colour mode, font sizing, file search, and settings
persistence. They are written as manual scripts because HEAT's GUI is
event-driven and not easily automated without a UI testing framework.
