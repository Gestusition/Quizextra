# 🎓 Quiz Application

A comprehensive Object-Oriented (OOP) console-based quiz management system built with Java, featuring multi-user authentication (Admin/Student), 3-tier layered architecture, diverse question types (MC/TF/FB), fully configurable system environment (where near every aspect is customizable), extensive administrative control, persistent CSV data storage, and extensive JUnit 5 test coverage

---

## 📸 Screenshots
Main Menu

<img width="373" height="228" alt="image" src="https://github.com/user-attachments/assets/857bede8-0190-4190-95b8-5c662d79a3e3" />

Student Menu

<img width="371" height="206" alt="image" src="https://github.com/user-attachments/assets/d93d7a54-f04f-4d38-a053-a99d1c20fd97" />

Admin Menu

<img width="360" height="428" alt="image" src="https://github.com/user-attachments/assets/01d21a31-353b-45e0-8d1a-420fd2c51276" />

Quiz in Progress

<img width="373" height="181" alt="image" src="https://github.com/user-attachments/assets/6d3e14d7-67ce-4e83-93ca-7547f2c69178" />
<img width="667" height="224" alt="image" src="https://github.com/user-attachments/assets/2e6e68a4-ca68-410f-bd69-023dae4d5052" />
<img width="669" height="113" alt="image" src="https://github.com/user-attachments/assets/27f4177c-de93-4913-be00-30d60d9bede8" />

Quiz Review - Detailed Results

<img width="624" height="577" alt="image" src="https://github.com/user-attachments/assets/30d31927-312b-4837-8dc3-8d644e074d3a" />











---

## ✨ Features

### 👨‍🎓 Student Features

| Feature | Description |
| --- | --- |
| **Take Quiz** | Answer questions with configurable time limit |
| **Difficulty Filter** | Choose Easy, Medium, or Hard questions |
| **Multiple Question Types** | Multiple Choice, True/False, Fill-in-the-Blank |
| **View Results** | See score, letter grade, and pass/fail status |
| **Review Answers** | Detailed review of all questions after quiz |
| **Quiz History** | View past quiz results |

### 👨‍💼 Administrator Features

| Feature | Description |
| --- | --- |
| **Student Management** | Add, edit, delete, list, search students |
| **Password Reset** | Reset student passwords to default |
| **Question Management** | Add, delete, list, reorder, search questions |
| **View Question Details** | See full question with correct answer |
| **Quiz Configuration** | Timer (30s-24h), shuffle, passing grade |
| **Grade Scale Editor** | Customize all grade thresholds (AA-FF) |
| **Activity Logs** | Track all admin operations |
| **Data Validation** | View and fix CSV data errors |
| **File Statistics** | View counts for students, questions, quizzes |
| **16-Option Menu** | Comprehensive admin control panel |

### ⚙️ System Features

| Feature | Description |
| --- | --- |
| **CSV Persistence** | All data stored in human-readable CSV files |
| **Input Validation** | Comprehensive validation with helpful error messages |
| **Timer System** | Per-quiz configurable countdown timer |
| **Shuffle Mode** | Randomize question order for each quiz |
| **Grading System** | AA, BA, BB, CB, CC, DC, DD, FD, FF scale |
| **Test Suite** | 41 JUnit 5 test methods |

### 🚀 Advanced Features

This application demonstrates **enterprise-level** software engineering practices:

| Feature | Description |
| --- | --- |
| **Layered Architecture** | Strict separation between UI, business logic, and data layers |
| **Interface-Driven Design** | 6 interfaces enabling polymorphism and loose coupling |
| **Singleton Pattern** | Thread-safe centralized data management |
| **Template Method Pattern** | Abstract `Question` class with extensible subtypes |
| **Comprehensive Validation** | Multi-level validation (Model + Service + Handler) |
| **Error Recovery** | Graceful handling of invalid data with user feedback |
| **Audit Logging** | Complete activity trail for admin operations |
| **Configurable Grading** | Runtime-adjustable grade thresholds and passing criteria |
| **Difficulty Multiplier** | Score weighting based on question difficulty |
| **CSV Escape Handling** | Proper handling of special characters in data |
| **Password Security** | Minimum length enforcement, SHA-256 ready |
| **Record Classes** | Modern Java records for immutable data transfer |

---

## 🏗️ Architecture

The application follows a **3-tier layered architecture**:

```
┌─────────────────────────────────────────────────────────────┐
│                         HANDLER                              │
│                  (Presentation Layer)                        │
│  • Displays menus and messages                               │
│  • Reads user input (Scanner)                                │
│  • No business logic                                         │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                         SERVICE                              │
│                  (Business Logic Layer)                      │
│  • Validation rules                                          │
│  • Calculations and filtering                                │
│  • Coordinates between Handler and Data                      │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                     MODEL + DATA                             │
│                    (Data Layer)                              │
│  • Model: Data structures (Student, Question, Quiz)          │
│  • Data: CSV file I/O (DataManager, ActivityLogger)          │
└─────────────────────────────────────────────────────────────┘
```

### Project Structure

```
src/com/quizapp/
├── data/           - Data management & activity logging
│   ├── DataManager.java        (Singleton, all CRUD operations)
│   └── ActivityLogger.java     (Quiz history & admin logs)
│
├── handler/        - User interface handlers
│   ├── MenuHandler.java        (Main menu loop)
│   ├── AdminHandler.java       (16-option admin menu)
│   ├── StudentHandler.java     (Student login & quiz)
│   ├── QuestionHandler.java    (Question CRUD)
│   ├── QuizHandler.java        (Statistics display)
│   └── StartupHandler.java     (Welcome message)
│
├── interfaces/     - Behavior contracts
│   ├── Displayable.java        (Display formatting)
│   ├── Exportable.java         (CSV export)
│   ├── Gradable.java           (Score calculation)
│   ├── Identifiable.java       (Unique ID)
│   ├── Searchable.java         (Search support)
│   └── Validatable.java        (Validation logic)
│
├── model/          - Data models
│   ├── Student.java            (Student entity)
│   ├── Admin.java              (Admin entity)
│   ├── Question.java           (Abstract base class)
│   ├── MultipleChoiceQuestion.java
│   ├── TrueFalseQuestion.java
│   ├── FillBlankQuestion.java
│   ├── Quiz.java               (Quiz session)
│   ├── GradeScale.java         (Grading configuration)
│   └── Difficulty.java         (EASY, MEDIUM, HARD enum)
│
├── service/        - Business logic
│   ├── StudentService.java     (Student operations)
│   ├── QuestionService.java    (Question operations)
│   └── QuizService.java        (Quiz operations)
│
├── test/           (9 files) - JUnit 5 test classes
│   ├── TestRunner.java         (Run all tests)
│   ├── StudentTest.java        (9 tests)
│   ├── QuestionTest.java       (10 tests)
│   ├── QuizTest.java           (4 tests)
│   ├── DataManagerTest.java    (5 tests)
│   ├── ServiceTest.java        (6 tests)
│   └── ...
│
├── util/           - Utilities
│   ├── CsvUtils.java           (CSV escape/unescape)
│   └── PasswordUtils.java      (Password validation)
│
└── main/
    └── QuizApp.java            (Entry point)
```

**Total: 38 Java files**

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- JUnit 5 (for running tests)

### Compile

```bash
javac -encoding UTF-8 -d out -sourcepath src src/com/quizapp/main/QuizApp.java
```

### Run

```bash
java -cp out com.quizapp.main.QuizApp
```

### Default Credentials

| Role | Username/ID | Password |
| --- | --- | --- |
| Admin | `admin` | `admin` |
| Student | `1` | `12345` |

---

## 📁 Data Files

| File | Format | Description |
| --- | --- | --- |
| `students.csv` | `STUDENT,id,name,password` | Student records |
| `admins.csv` | `ADMIN,username,password` | Admin credentials |
| `questions.csv` | `TYPE,text,answer,difficulty,...` | Question bank |
| `settings.csv` | `KEY,value` | Quiz configuration |
| `quiz_history.csv` | Timestamped results | Student quiz history |
| `activity_log.csv` | Timestamped actions | Admin activity audit |

---

## 🎯 Design Patterns & Principles

| Pattern | Implementation |
| --- | --- |
| **Singleton** | `DataManager.getInstance()` - Single data source |
| **Template Method** | `Question` abstract class with `checkAnswer()` |
| **Strategy** | Different question types implement answer checking |

| Principle | Implementation |
| --- | --- |
| **Single Responsibility** | Each class has one job |
| **Open/Closed** | Add new question types without modifying existing code |
| **Interface Segregation** | 6 focused interfaces instead of one large interface |
| **Dependency Inversion** | Handlers depend on abstractions (interfaces) |

---

## 📝 Grading System

Configurable letter grade scale with pass/conditional/fail status:

| Grade | Default Threshold | Status |
| --- | --- | --- |
| AA  | 90+ | ✅ PASS |
| BA  | 85+ | ✅ PASS |
| BB  | 80+ | ✅ PASS |
| CB  | 75+ | ✅ PASS |
| CC  | 70+ | ✅ PASS |
| DC  | 65+ | ⚠️ CONDITIONAL |
| DD  | 60+ | ⚠️ CONDITIONAL |
| FD  | 50+ | ❌ FAIL |
| FF  | <50 | ❌ FAIL |

All thresholds and the passing grade are configurable via Admin menu.

---

## 🧪 Testing

### Run Test Suite

```bash
java -cp "out;lib/*" com.quizapp.test.TestRunner
```

### Test Coverage

| Test Class | Tests | Coverage |
| --- | --- | --- |
| StudentTest | 9   | Student validation, search, display |
| QuestionTest | 10  | MC, TF, FB answer checking |
| QuizTest | 4   | Scoring, grading, timer |
| DataManagerTest | 5   | CRUD, singleton, settings |
| ServiceTest | 6   | Business logic layer |
| PasswordUtilsTest | 7   | Password validation |
| QuizSystemTest | 4   | Integration tests |

**Total: 41 test methods**

---

## 👤 Author

**Kıraç Polatkan**  
Computer Engineering Student

---

<p align="center">
  Made with ☕ and Java
</p>
