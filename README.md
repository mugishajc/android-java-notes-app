# Notes (Android, Java)

A small offline notes app for Android written in Java. Notes have a title, a
body and a category, can be pinned to the top of the list, and can be found
again with a search box and category filter chips.

![CI](https://github.com/mugishajc/android-java-notes-app/actions/workflows/ci.yml/badge.svg)

## Features

- Create, edit and delete notes. Changes save when you leave the editor.
- Five categories (Personal, Work, Ideas, To-do, Other) with filter chips.
- Search across titles and bodies as you type.
- Pin important notes. Pinned notes stay on top, the rest sort by last edit.
- Swipe a note to delete it, then undo from the snackbar.
- Light, dark or follow-system theme (Material 3 DayNight).
- Everything is stored locally in a Room database. No account, no network.

## Stack

| Area | Choice |
| --- | --- |
| Language | Java 17 |
| UI | Android Views, ViewBinding, Material Components 3 |
| List | RecyclerView with `ListAdapter` and `DiffUtil` |
| Architecture | MVVM with `ViewModel` and `LiveData` |
| Storage | Room 2.6 (schema exported to `app/schemas`) |
| Build | Gradle 8.7 wrapper, AGP 8.4, version catalog |
| Tests | JUnit 4, `InstantTaskExecutorRule` |
| CI | GitHub Actions |

minSdk 26, targetSdk and compileSdk 35.

## Architecture

```
ui/            MainActivity, EditNoteActivity, NoteAdapter
               NotesViewModel, EditNoteViewModel
domain/        NoteFilter (search + category + sort rules, plain Java)
data/          Note entity, NoteDao, NoteDatabase
               NoteRepository (interface) -> RoomNoteRepository
```

- Activities only render state and forward user actions.
- `NotesViewModel` combines the Room `LiveData` with the current search text
  and category through a `MediatorLiveData`, and runs them through
  `NoteFilter`.
- `EditNoteViewModel` owns the save rules: blank drafts are dropped, a blank
  title is taken from the first line of the body, and an unchanged note is not
  written again.
- ViewModels depend on the `NoteRepository` interface, so tests swap in an
  in-memory fake and run on the JVM without an emulator or Robolectric.
- `RoomNoteRepository` runs writes on a single background executor and posts
  results back to the main thread.

## Running it

Requirements: JDK 17 and the Android SDK (platform 35).

```bash
./gradlew assembleDebug         # build the APK
./gradlew testDebugUnitTest     # run the unit tests
./gradlew installDebug          # install on a connected device or emulator
```

Or open the project in Android Studio and run the `app` configuration.

## Tests

Local unit tests live in `app/src/test`:

- `NoteFilterTest`: search, category filter and display order.
- `NotesViewModelTest`: filtering through the ViewModel, pinning, delete and undo.
- `EditNoteViewModelTest`: new vs existing notes, empty drafts, derived titles,
  timestamps.
- `CategoryTest`, `ThemePrefsTest`: converters and theme mapping.

## Screenshots

No screenshots are checked in yet. Build and run the app locally to try it.

## License

MIT, see [LICENSE](LICENSE).
