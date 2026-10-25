class ApiUrls {
  // Configurable at compile time via --dart-define=API_BASE_URL=https://...
  static const String baseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://dev.panchayagnam.com:8080',
  );

  static const String webSocketbaseUrl = String.fromEnvironment(
    'WS_BASE_URL',
    defaultValue: 'ws://dev.panchayagnam.com:8080',
  );
}