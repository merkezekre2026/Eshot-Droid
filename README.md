# İzmir ESHOT Android

Modern, Turkish-first Android app for exploring İzmir ESHOT bus lines, stops, route geometry, scheduled departures, nearby stops, favorites, and official announcements.

## Tech Stack

- Kotlin, Jetpack Compose, Material 3
- MVVM with repository pattern
- Hilt dependency injection
- Retrofit, OkHttp, Kotlinx Serialization
- Room local cache with stale-while-revalidate behavior
- DataStore-ready project setup
- Leaflet map rendered in Android WebView, with OpenStreetMap tiles
- Coroutines and Flow

## Official Data Sources

The app is wired to official İzmir Metropolitan Municipality / İzmir Open Data sources:

- Bus lines: `https://openfiles.izmir.bel.tr/211488/docs/eshot-otobus-hatlari.csv`
- Bus stops: `https://openfiles.izmir.bel.tr/211488/docs/eshot-otobus-duraklari.csv`
- Route geometry: `https://openfiles.izmir.bel.tr/211488/docs/eshot-otobus-hat-guzergahlari.csv`
- Scheduled departure times: `https://openfiles.izmir.bel.tr/211488/docs/eshot-otobus-hareketsaatleri.csv`
- Announcements: `https://openfiles.izmir.bel.tr/211488/docs/eshot-otobus-hat-duyurulari.csv`
- Connection lines: `https://openfiles.izmir.bel.tr/211488/docs/eshot-otobus-baglantili-hatlar.csv`
- GTFS fallback: `https://www.eshot.gov.tr/gtfs/bus-eshot-gtfs.zip`
- Nearby stops API: `https://openapi.izmir.bel.tr/api/ibb/cbs/noktayayakinduraklar`
- Official live bus locations and approaching bus APIs under `https://openapi.izmir.bel.tr/api/iztek/...`

The UI labels scheduled times as planned/static data. Live vehicle or approaching-bus data is shown only when the official API returns usable data; the app does not fabricate ETA predictions.

## Maps

Google Maps is not used. The map feature is isolated in `feature:map` and renders local Leaflet assets through `WebView`.

By default the Leaflet shell uses OpenStreetMap raster tiles:

```text
https://tile.openstreetmap.org/{z}/{x}/{y}.png
```

Review OpenStreetMap tile usage policy before production release. For production scale, configure a compliant tile provider, self-hosted tile server, or offline tile strategy in `app/src/main/assets/map/map.js`.

## Setup

1. Open the project in Android Studio.
2. Use JDK 17 or newer.
3. Build with the Gradle wrapper once `gradle-wrapper.jar` is generated or supplied:

```bash
./gradlew assembleDebug
```

If the wrapper JAR is missing, generate it with a compatible local Gradle installation:

```bash
gradle wrapper --gradle-version 8.12.1
```

No Google Maps API key is required.

## Architecture

- `core:model`: normalized app/domain models.
- `core:network`: Retrofit services and official source constants.
- `core:database`: Room entities, DAOs, and database module.
- `core:data`: repositories, CSV parser, GTFS parser, mapping, sync logic.
- `feature:*`: Compose screens and ViewModels.
- `app`: Hilt app class, navigation, theme, manifest, and Leaflet assets.

The sync layer downloads official CSV files, parses Turkish UTF-8 content robustly, normalizes entities, and upserts them into Room. Screens observe Room first and can keep stale cached data visible when remote refresh fails.

## Notes And Current Limits

- The official CSV files are treated as the static source of truth.
- GTFS parser support is included as infrastructure, but the first UI path primarily uses official CSV/OpenAPI sources.
- Nearby stops use the official nearby-stop API first and fall back to local Haversine sorting over cached stops.
- Route stop ordering is approximated from stop-line membership until an official ordered `hatduraklari` response is integrated into cache.
- Location permission is requested only from the Nearby Stops flow.

## License

Recommended app code license: Apache-2.0.

Data attribution should mention İzmir Metropolitan Municipality / İzmir Open Data. Map attribution must include Leaflet and OpenStreetMap contributors.
