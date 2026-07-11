# Go Tour N Travels — TravelRide

A **production-ready native Android app + Node.js backend** built for **Go Tour N Travels**, a vehicle rental and travel agency based in **Mount Abu, Rajasthan**. This app digitises their existing business — scooter/bike/car rentals, vehicles with drivers, airport transfers, sightseeing, and tour packages — into a single mobile experience for customers, with a built-in admin dashboard.

> **Single-vendor app.** This is *not* a multi-provider marketplace. Only Go Tour N Travels operates vehicles and bookings.

---

## 1. Tech Stack

### Android
| Layer | Library |
|------|---------|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (Compose → ViewModel → Repository → Retrofit → API) |
| Navigation | Navigation Compose |
| DI | Hilt |
| Network | Retrofit + OkHttp + Gson |
| Async | Coroutines + Flow |
| Local storage | DataStore (auth/prefs) + Room (optional cache) |
| Maps | Google Maps SDK, Maps Compose, Places API, Fused Location Provider |
| Auth | JWT in DataStore |
| Push | Firebase Cloud Messaging |
| Payments | Razorpay Checkout |
| Images | Coil |
| Permissions | Accompanist Permissions |

### Backend
| Layer | Library |
|------|---------|
| Runtime | Node.js + Express |
| Database | MongoDB + Mongoose |
| Auth | JWT (jsonwebtoken + bcryptjs) |
| Realtime | Socket.IO (live rental tracking) |
| Storage | Cloudinary (with local fallback for dev) |
| Payments | Razorpay Orders API |
| Security | Helmet, CORS, express-rate-limit |
| Logging | Winston + Morgan |

---

## 2. Project Structure

```
TravelRide/
├── app/                                   # Android app
│   ├── src/main/java/com/gotourntravels/
│   │   ├── ui/
│   │   │   ├── screens/{auth,customer,admin}/   # 25+ Compose screens
│   │   │   ├── components/               # Reusable Compose components
│   │   │   ├── navigation/               # NavHost + Dest routes
│   │   │   └── theme/                    # Royal Rajasthan palette
│   │   ├── viewmodel/                    # 9 Hilt ViewModels
│   │   ├── repository/                   # GoTourRepository (single source of truth)
│   │   ├── network/                      # Retrofit API + OkHttp + Hilt module
│   │   ├── models/                       # Domain models
│   │   ├── datastore/                    # DataStore prefs (auth, dark mode, onboarding)
│   │   ├── services/                     # FCM + Foreground TrackingService
│   │   ├── permissions/                  # Accompanist permission helpers
│   │   ├── location/                     # Fused Location wrapper
│   │   ├── GoTourApp.kt                  # @HiltAndroidApp + NotificationChannels
│   │   └── MainActivity.kt
│   └── src/main/res/                     # Theme, colors, strings, drawables
│
├── backend/                               # Node.js Express API
│   ├── controllers/  routes/  services/  models/  middleware/  sockets/  config/
│   ├── seed/                             # Vehicles + places + admin seed
│   ├── app.js  server.js  package.json
│   └── .env.example
│
├── build.gradle.kts  settings.gradle.kts  gradle.properties
└── README.md
```

---

## 3. Branding

**Royal Rajasthan** palette:
- Maroon `#8B1E3F` (primary)
- Gold `#D4A437` (accent)
- Cream `#FFF8E7` (background)
- Ink `#1A0F12` (text)

Dark mode supported. Splash screen uses the gold-on-maroon shield logo.

---

## 4. Getting Started

### 4.1 Backend

```bash
cd TravelRide/backend
cp .env.example .env
# Edit .env: set MONGO_URI, JWT_SECRET, etc.

npm install
npm run seed      # Seeds admin + 8 vehicles + Mount Abu POIs + sample bookings
npm run dev       # http://localhost:5000
```

**Default admin login** (after seeding):
- Email: `admin@gotourntravels.com`
- Password: `Admin@123`

**Demo customer**: `demo@gotourntravels.com` / `Demo@123`

API surface:
- `POST /api/auth/register | login | admin/login | verify-otp | forgot-password | reset-password`
- `GET|PUT /api/users/me`
- `GET|POST|PUT|DELETE /api/vehicles`
- `GET|POST /api/bookings` (+ `/activate`, `/complete`, `/cancel`, `/location`, `/review`)
- `POST /api/payments/create-order | /verify`
- `GET|POST /api/sos` (+ `/acknowledge`, `/resolve`)
- `GET|PUT /api/business`
- `GET|POST|PUT|DELETE /api/ads`
- `GET /api/places`
- `POST /api/upload`
- `GET /api/admin/dashboard | /analytics/*`

### 4.2 Android App

1. **Open** `TravelRide/` in Android Studio (Hedgehog or newer).
2. Replace `app/google-services.json` with your real Firebase project file (or use the placeholder for local testing — FCM push won't work but the app compiles & runs).
3. Add your Google Maps API key in `AndroidManifest.xml` `<meta-data android:name="com.google.android.geo.API_KEY" android:value="YOUR_KEY" />` (only needed for live map rendering; the rest of the app works without it).
4. **Backend URL** is set in `app/build.gradle.kts` → `BASE_URL`. Default for emulator: `http://10.0.2.2:5000/api/`. For a real device, set your machine's LAN IP.
5. **Build & Run** on any Android 8.0+ (API 26+) device.

```bash
cd TravelRide
./gradlew assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

---

## 5. User Roles & Screens

### Customer
Splash → Onboarding → Login → Register → OTP → **Home** → Search → Vehicle Details → Book Vehicle → Booking Summary → Payment → Active Booking → Booking History → Payments → Tourist Map → Nearby Petrol/Hospitals/Police → Tourist Attractions → SOS → Notifications → Profile → Settings → Help & Support → About

### Business Admin
Login as admin → **Admin Dashboard** → Vehicle Management (add/edit) → Booking Management (activate/complete/cancel) → Customer Management → Earnings Dashboard → Analytics → SOS Requests → Reviews → Advertisement Management → Business Profile

Admin screens are accessible from the **Profile** tab as well (visible only when `user.role == 'admin'`). Both customer and admin live in the **same APK** for fast iteration.

---

## 6. Key Features

- Browse scooters, Activas, bikes, cars, SUVs with real-time availability
- Hourly / daily / weekly rentals with computed GST + security deposit
- Razorpay order creation + signature verification (mock mode accepts any signature for dev)
- JWT auth with OTP phone verification
- Live rental tracking with foreground location service (Android ForegroundServiceType.location)
- SOS to business + direct call to 112
- Nearby POIs (curated Mount Abu dataset — petrol, hospitals, police, attractions)
- Digital invoices and payment history
- Push notifications via FCM (NotificationChannels: tracking, sos, general)
- Dark mode + Material 3 theming
- Admin analytics: bookings trend chart, revenue by vehicle type, top vehicles
- Advertisements module (home banners)
- Cloudinary image uploads (local fallback when not configured)

---

## 7. Architecture

```
Android:
  Compose UI
     ↓  (state hoisting)
  ViewModel (Hilt)
     ↓
  Repository
     ↓
  Retrofit  →  Express API  →  MongoDB

Backend:
  Routes → Controllers → Services → Mongoose Models → MongoDB
                                    ↑
                          Socket.IO (tracking, SOS)
```

- **Single source of truth**: `GoTourRepository` — all ViewModels go through it.
- **StateFlow** for reactive UI state in every ViewModel.
- **DataStore** for auth token + user + dark mode + onboarding flag (survives process death).
- **Hilt** for DI across `ViewModel`, `Repository`, `NetworkModule`, `DataStoreModule`, `LocationProvider`.
- **WorkManager** (HiltWorkerFactory) wired for background sync work (extends as business grows).

---

## 8. Permissions Handled

| Permission | Where used |
|------------|-----------|
| `INTERNET`, `ACCESS_NETWORK_STATE` | Retrofit |
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | Tourist map, active rental tracking, SOS |
| `ACCESS_BACKGROUND_LOCATION` | Foreground tracking service during active rental |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION` | `TrackingService` |
| `CALL_PHONE` | SOS call-112 action |
| `CAMERA`, `READ_MEDIA_IMAGES` | Licence upload (future) |
| `POST_NOTIFICATIONS` | Android 13+ — requested at runtime |

All runtime permissions use Accompanist's `rememberPermissionState` API.

---

## 9. Integrations — Mock vs Real

The app ships in **mock mode** by default so it runs end-to-end without external accounts. Swap in real keys when ready:

| Service | Mock behaviour | How to enable real |
|---------|----------------|--------------------|
| Razorpay | `RAZORPAY_KEY_ID=rzp_test_placeholder` → backend returns `mock: true` orders that auto-verify | Set real test key in `backend/.env` and `app/build.gradle.kts` BuildConfig |
| ImageKit | Files saved to `backend/public/uploads/` and served via `/static` when ImageKit is not configured | Set `IMAGEKIT_PRIVATE_KEY` in `.env` to upload to ImageKit |
| Google Maps | Map placeholder renders; POI list works without key | Add real `GOOGLE_MAPS_API_KEY` in `AndroidManifest.xml` |
| Firebase FCM | App compiles; push doesn't arrive | Replace `app/google-services.json` with real Firebase project |
| MongoDB | Uses local `mongodb://127.0.0.1:27017/gotourntravels` | Set `MONGO_URI` in `.env` to Atlas or production URI |
| OTP SMS | OTPs are logged to backend console (`[DEV] OTP for 91111…: 123456`) | Plug Twilio/MSG91 into `services/otpService.js` |

---

## 10. Production Hardening Checklist

Before going live:
- [ ] Enable ProGuard/R8 (`minifyEnabled true` already set in release buildType)
- [ ] Replace placeholder `google-services.json` with production Firebase project
- [ ] Add real Razorpay live key (after testing on test key)
- [ ] Set `BASE_URL` in `app/build.gradle.kts` `release` buildType to production HTTPS URL
- [ ] Add real Google Maps API key with package-restricted Android credentials
- [ ] Set `usesCleartextTraffic="false"` in `AndroidManifest.xml` (currently true for dev)
- [ ] Configure Twilio / MSG91 in `otpService.js`
- [ ] Move OTP store from in-memory to Redis (`otpService.js`)
- [ ] Configure real Cloudinary credentials
- [ ] Set up a MongoDB Atlas cluster or self-hosted MongoDB in production
- [ ] Add SSL certificate to backend (Nginx reverse proxy + Let's Encrypt)
- [ ] Wire `FCM token` upload in `GoTourMessagingService.onNewToken`
- [ ] Implement `ACTION_DIAL` / `ACTION_CALL` intents for SOS and emergency phone buttons

---

## 11. Extending the App

The architecture is designed for extension:

- **Add a new screen**: Add a `Dest` entry → register a `composable(...)` in `GoTourNavHost` → create a new ViewModel if needed → reuse components from `ui/components/`.
- **Add a new API endpoint**: Add a method to `GoTourApi` → add a passthrough in `GoTourRepository` → call from the relevant ViewModel.
- **Add a new vehicle field**: Extend `Vehicle` in both `models/Vehicle.js` (backend) and `models/Models.kt` (Android). Mongoose + Gson handle the rest.
- **Add a new admin analytics**: Add a route under `routes/admin.js` → expose via `GoTourApi` → render in `AnalyticsScreen`.

---

## 12. License

Proprietary — © Go Tour N Travels, Mount Abu, Rajasthan. Built for the client's exclusive use.
