# ABCBankCarousel

Android assignment app for a banking client: image carousel, searchable list, and a statistics bottom sheet. The final submission keeps one polished, runnable implementation on `main` and the alternative UI implementation on a feature branch.

- **JDK 17**
- **minSdk 24**, **targetSdk 35**

## Project structure

- **MVI presentation**
  - **Intent:** user input and screen actions
  - **State:** immutable screen content with `Loading`, `Content`, `Empty`, and `Error`
  - **Effect:** one-off UI events such as load failure snackbar
- **Shared domain layer**
  - `domain/model/`
  - `domain/usecase/`
  - `domain/repository/`
- **Explicit local data layer**
  - `data/source/` reads a local JSON asset
  - `data/repository/` maps source models into domain models
- **Package:** `com.abcbank.carousel`

## Branches

| Branch | UI stack | Launcher activity |
|--------|----------|--------------------|
| `main` | Compose + Material 3 | `ComposeMainActivity` |
| `feature/xml-implementation` | View + ViewPager2 + RecyclerView + ViewBinding | `CarouselXmlActivity` |

Both branches share the same domain/data foundation and differ only in presentation implementation.

## What the app does

- **Image carousel** — Swipe to change page; list and page indicators follow the current page.
- **Scrollable list** — Different item counts per page (25, 30, 20, 15, 28). Each item: thumbnail, title, subtitle (e.g. fruit names).
- **Search** — Pinned at top; filters by title or subtitle (case-insensitive, real-time).
- **FAB** — Opens a bottom sheet with stats for all pages: page label, item count, top 3 character counts.
- **Screen states** — Handles loading, content, empty search results, empty source data, and load errors.

## Implementation notes

- **Compose branch:** `collectAsStateWithLifecycle()`, hoisted callbacks, `HorizontalPager`, and `LazyColumn`.
- **XML branch:** `repeatOnLifecycle`, `ViewPager2`, `RecyclerView`, `DiffUtil`, and ViewBinding.
- **Dependency creation:** a lightweight `CarouselAppContainer` wires the local data source and repository.
- **Data source:** page definitions live in `app/src/main/assets/carousel_pages.json`.

## Test coverage

- `FilterItemsUseCaseTest`
- `CalculateStatisticsUseCaseTest`
- `DefaultCarouselRepositoryTest`
- `CarouselViewModelTest` on `main`
- `CarouselXmlViewModelTest` on `feature/xml-implementation`

Run locally with:

```bash
./gradlew test assembleDebug lintDebug
```
