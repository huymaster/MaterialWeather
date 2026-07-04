# Project: MaterialWeather – Data-Driven UI Engine

## 1. Summary

MaterialWeather is not simply a standard weather application; it is a highly adaptable Data-Driven
UI Engine. It empowers users to define custom data providers, construct parsing scripts, and
automatically bind dynamic user interfaces. This project serves as a comprehensive dashboard engine
tailored for tech enthusiasts who want to build personalized weather or local IoT station monitoring
applications.

## 2. Problem & Solution Argumentation

- The Argument (Pain Points): Traditional weather applications are rigidly tied to a single data
  provider (API) and a static User Interface (UI). When an API's pricing model changes, services
  deprecate, or users need to integrate a local IoT weather station, these applications become
  obsolete.
- The Evidence & Solution: MaterialWeather solves this by completely decoupling three core
  components: the Data Provider, the Parser, and the UI. Users act as "system configurators,"
  defining behaviors using JSON.
- Trade-off: This approach offers absolute flexibility; however, it requires users to understand
  external API documentation and update configurations if the data structure changes.

## 3. Technology Stack & Technical Standards

- Language:
  [![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)]((https://kotlinlang.org/))

- UI Framework: [Jetpack Compose](https://developer.android.com/compose)
  ([Adaptive UI](https://developer.android.com/develop/adaptive-apps/guides/get-started-with-adaptive-apps) & [Material 3](https://m3.material.io/))
- Dependency Injection: [Koin](https://insert-koin.io/)
- Networking: [Ktor](https://ktor.io) Client for processing HTTP requests
- Parsing & Serialization: [Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html)
  combined with JSONPath for dynamic JSON tree traversal
- Asynchronous Programming: [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-guide.html)
- Local Storage: [Room](https://developer.android.com/training/data-storage/room) Database &
  [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

## 4. Architectural Design & Patterns

The system strictly adheres to Clean Architecture, SOLID principles, and official Android
Architecture recommendations to manage the engine's dynamic nature. The core flow operates
seamlessly: `[Provider Node] → [JSON Parser Engine] → [Dynamic UI Engine]`.

Key design patterns implemented include:

- Strategy Pattern: Utilized at the Provider Node layer. It allows seamless switching between
  different data retrieval strategies (e.g., OpenWeatherMap Strategy vs. Local IoT Station Strategy)
  via a common interface without breaking the underlying architecture.
- Factory Method / Builder Pattern: The Dynamic UI Engine relies on a WidgetFactory. When reading
  the UI structure from JSON (e.g., `{"type": "Text", "value": "..."}`), the Factory dynamically
  constructs the corresponding Jetpack Compose components (such as `Box`, `Row`, `Column`, `Text`).
- Observer Pattern (via Flow): Extracted data is pushed into a `StateFlow`. The UI Engine acts as an
  Observer, automatically recomposing the interface whenever new data arrives or the cache is
  updated.
- Repository Pattern: Ensures a complete separation between the data fetching layer (Ktor/Room) and
  the business logic layer, heavily enforcing the Dependency Inversion Principle.

## 5. Operational Flow (How It Works)

1. Provider Node Configuration: Users define a data provider node using a configuration structure
   that includes the URI, Headers, Parameter Holders, and the HTTP Method. The Ktor
   network core executes the request and retrieves the raw JSON string.
2. Parser Engine: Instead of relying on static data classes, the system utilizes JSONPath to
   dynamically traverse the returned JSON tree. It accurately extracts necessary values (e.g.,
   temperature, humidity) based on user-defined rules.
3. Dynamic UI Binding (The Backbone): The UI is defined via a structured JSON tree. A built-in
   Mapper traverses this tree; for instance, encountering a "Box" node dynamically generates a
   Compose `Box`. Text values support dynamic binding (e.g., `{{current.temp}}°C`), allowing the
   engine
   to automatically replace placeholders with the parsed data from the previous step.

## 6. Development Roadmap

### Phase 1: Core Engine

- [x] Establish the foundational architecture using Koin and Jetpack Compose.
- [ ] Complete the Base Provider Node to support HTTP requests yielding JSON.
- [ ] Finalize the Parser utilizing JSONPath.
- [ ] Develop the Dynamic UI Engine to support fundamental widgets (Box, Column,
  Row, Text), allowing users to build upon the Base Provider template.

### Phase 2: Automation & Reliability

- [ ] Caching System: Integrate Room Database to cache raw JSON or
  parsed data, enabling offline functionality or fallback during API failures.
- [ ] Rule-based Notifications: Enable users to set logical conditions (e.g., if (temp > 35) trigger
  Alert). The system will push notifications if the data meets the defined criteria.

### Phase 3: Ecosystem Expansion

- [ ] Import/Export Engine: Package entire configurations (Provider, Parser, and UI Layout) into a
  proprietary archive format, such as ZIP
- [ ] Community Sharing: Allow users to share custom "Themes" and "Dashboards" for local weather
  stations via a quick import mechanism.