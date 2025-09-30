# 🚀 S-Emulator – Assignment #2

## ✨ Bonus Features
- 🎨 **Style sheet options (skins)**
- ⏪ **Step back** (debugger backward execution)
- ⛔ **Breakpoint support**
- 📝 **UI-based program creator**

## 🖥️ System Overview
- JavaFX desktop application for **S-language** programs (XML validated against schema v2).  
- Structured table view of instructions.  
- Supports **expansion**, **execution**, and **history**.  
- Debugging: step-by-step, forward/backward, variable visualization.  
- Highlighting by variable/label.  
- Save/load programs, switch between UI themes.  

## 🛠️ Design Decisions
- **DTO only** between UI and engine (`ProgramDto`, `InstructionDto`, …).  
- **Program creation (bonus):** `InstructionDraft` + `ProgramDraft` keep UI and logic separate.  
- **File loading:** runs in a JavaFX `Task` on a background thread with progress bar & result dialog.  
- **Instruction hierarchy:**  
  - `SimpleInstruction` → Assignment #1 basics (INCREASE, DECREASE, JUMP_NOT_ZERO, …).  
  - `ComplexInstruction` → Assignment #2 (QUOTE, JUMP_EQUAL_FUNCTION).  

## 📦 Project Structure
- **engine** → core logic: model, validation, expansion, execution, debug.  
- **ui-fx** → JavaFX layer: main app, controllers, theming.  

## ⚡ Execution Modes
- ▶️ **Run**: execute full program, show result, variables, cycles.  
- 🐞 **Debug**: step over/back, breakpoints, live highlights.  

## 📂 Main Classes (highlights)
- `ProgramImpl` – program representation.  
- `ProgramExecutorImpl` – execution engine.  
- `SEmulatorEngineImpl` – manages program, expansion, history.  
- `XmlProgramMapperV2` – JAXB mapping from XML.  
- `DebuggerExecutionController`, `HistoryController`, `AddProgramController`, `TopBarController`.  
- Helpers: `DisplayUtils`, `ProgramUtil`.  

🔗 GitHub: [linoyfurian/S-Emulator](https://github.com/linoyfurian/S-Emulator)
