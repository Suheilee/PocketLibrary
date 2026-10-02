# 📚 Pocket Library

Pocket Library is a mobile application designed to provide users with a convenient way to discover, search, categorize, and manage books. The application combines remote book data from the Open Library API with locally persisted user favourites, while using Firebase for cloud-based functionality and authentication.

The application was developed as part of **COMP2008 – Mobile Application Development** using Android Studio and modern Android development technologies.

## ✨ Features

* 🔎 **Book Search** — Search for books using the Open Library API.
* ❤️ **Favourites** — Save and remove books from a locally persisted favourites collection.
* 🏷️ **Categories** — Organize and filter books based on categories.
* 🔥 **Firebase Integration** — Cloud-based functionality with Firebase and Firestore.
* 📱 **Responsive UI** — Dynamic interface that updates according to search results, favourites, filters, and network state.
* 📷 **Custom Book Covers** — Use the device camera to customize book covers.
* 📤 **Book Sharing** — Share books using device contacts.
* 🌐 **Network-Aware State** — Handles changing network conditions alongside API and local data.

## 🛠️ Technology Stack

| Technology                   | Purpose                                               |
| ---------------------------- | ----------------------------------------------------- |
| **Kotlin**                   | Primary programming language                          |
| **Android Studio**           | Development environment                               |
| **Jetpack Compose**          | User interface                                        |
| **ViewModel**                | UI state and business logic                           |
| **Room**                     | Local persistence for favourite books                 |
| **Retrofit**                 | Open Library API integration                          |
| **Open Library API**         | Remote book search and metadata                       |
| **Firebase / Firestore**     | Cloud data and authentication                         |
| **Kotlin Coroutines & Flow** | Asynchronous operations and reactive state management |

## 🏗️ Architecture

The application follows a layered architecture in which the UI communicates with ViewModels, while repositories abstract interactions with local and remote data sources.

```text
┌─────────────────────────┐
│       UI / Compose      │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│       ViewModel         │
│     BooksViewModel      │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│      Repositories       │
├─────────────┬───────────┤
│             │           │
▼             ▼           ▼
Room       Open Library  Firebase
Database      API       / Firestore
```

## ❤️ Favourites

The favourites functionality uses **Room Persistence Library** to maintain a local collection of favourite books.

The local data layer consists of:

* `FavouriteBookEntity` — Represents a persisted favourite book.
* `FavouriteBookDao` — Provides database operations.
* `AppDatabase` — Defines the Room database.
* `FavouriteBookRepository` — Abstracts database operations from the ViewModel.

This separation provides the `BooksViewModel` with a clean API while keeping database implementation details within the data layer.

## 🔎 Search

Book search is implemented using the **Open Library API** through Retrofit.

The search functionality was designed to provide responsive results while managing network requests efficiently. Search results are incorporated into the application's overall UI state alongside locally stored favourites.

## 🔄 State Management

One of the more complex aspects of the application was managing multiple sources of state simultaneously.

The application's state can include:

* Open Library API search results
* Locally persisted favourite books
* Search queries
* Category filters
* Favourite filters
* Network status

These streams are combined within `BooksViewModel` using Kotlin Flow's `combine` functionality to produce a single `BooksUiState`.

This allows the UI to react consistently when any underlying data changes and prevents issues such as favourite statuses becoming outdated when search results change.

### Favourite Synchronization

When a user adds or removes a favourite, the application updates the Room database while also updating the local `allBooks` collection to provide immediate UI feedback.

The resulting flow is approximately:

```text
User toggles favourite
        │
        ├──► Update Room database
        │
        └──► Update local book state
                    │
                    ▼
              BooksUiState
                    │
                    ▼
                    UI refresh
```

## 🏷️ Filtering

Filtering logic is encapsulated through a computed `filteredBooks` property.

This allows multiple filtering criteria to be applied together, including:

* Search queries
* Book categories
* Favourite status

Keeping this logic centralized makes the filtering behaviour easier to maintain and prevents different parts of the UI from implementing inconsistent filtering rules.

## 🔥 Firebase Integration

Firebase was integrated to support cloud-based functionality and authentication.

During development, an initial Firebase configuration error prevented the application from accessing Firestore correctly. Investigation revealed that **anonymous authentication had not been enabled** in the Firebase project.

After enabling the required authentication method and correcting the configuration, Firebase synchronization functioned as expected.

## 🔐 Security & Permissions

The application considers both cloud data security and device-level permissions.

Firebase permissions were configured so that privileged operations are restricted to authenticated users, helping reduce the risk of unauthorized access or exposure of user data.

The application also requests runtime permissions where required for specific functionality:

* **Camera permission** — Used to allow users to customize book covers using their device camera.
* **Contacts permission** — Used when sharing books with contacts.

Permissions are requested explicitly at runtime rather than being assumed by the application.

## 👩‍💻 My Contributions

As part of the development team, I primarily contributed to the following areas:

### Favourites

* Designed and implemented the local Room data layer.
* Created `FavouriteBookEntity`.
* Implemented the `FavouriteBookDao`.
* Configured `AppDatabase`.
* Developed the `FavouriteBookRepository`.
* Implemented favourite toggling and synchronization with the UI.

### Search

* Integrated the Open Library API using Retrofit.
* Implemented the search functionality and associated UI.
* Worked on efficient handling of API search results.

### Categories & Filtering

* Implemented category-based filtering.
* Developed the `filteredBooks` property to combine category, favourite, and search filtering.
* Integrated filtering with the overall UI state.

### Firebase

* Integrated Firebase functionality.
* Debugged Firebase/Firestore configuration issues.
* Identified missing anonymous authentication as the cause of the initial Firestore access problem.
* Verified Firebase synchronization after correcting the configuration.

### State Management

* Worked with `BooksViewModel` to manage multiple asynchronous data sources.
* Used Kotlin `Flow` and `combine` to merge API results, local favourites, and other application state into a unified `BooksUiState`.
* Implemented immediate UI updates when favourites were added or removed.

## 🎓 Project Context

**Module:** COMP2008 – Mobile Application Development
**Project:** Pocket Library
**Platform:** Android
**Development Environment:** Android Studio
**Language:** Kotlin

This was a collaborative university project. The repository represents the team's application, with the contributions listed above describing my primary areas of responsibility.
