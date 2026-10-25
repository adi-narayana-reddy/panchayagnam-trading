import 'dart:html' as html;

class SessionStorageHelper {
  static void setString(String key, String value) {
    html.window.sessionStorage[key] = value;
  }

  static void setInt(String key, int value) {
    html.window.sessionStorage[key] = value.toString();
  }

  static String? getString(String key) {
    return html.window.sessionStorage[key];
  }

  static int? getInt(String key) {
    final val = html.window.sessionStorage[key];
    return val != null ? int.tryParse(val) : null;
  }

  static void clear() {
    html.window.sessionStorage.clear();
  }
}