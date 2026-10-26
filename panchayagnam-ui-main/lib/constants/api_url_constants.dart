class ApiUrls {
  // Configurable at compile time via --dart-define=API_BASE_URL=https://...
  static const String baseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'https://panchayagnam-api.onrender.com',
  );

  static const String webSocketbaseUrl = String.fromEnvironment(
    'WS_BASE_URL',
    defaultValue: 'wss://panchayagnam-api.onrender.com',
  );
}