import 'dart:html' as html;
import '../app_route/user_session.dart';

class AuthHeaderHelper {
  /// Returns standard headers including Authorization: Bearer <token> if available.
  static Map<String, String> getHeaders({Map<String, String>? extraHeaders}) {
    final headers = <String, String>{
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    String token = UserSession().token;
    if (token.isEmpty) {
      try {
        token = html.window.sessionStorage['auth_token'] ?? '';
      } catch (_) {
        // Fallback for non-web environments or storage unavailability
      }
    }

    if (token.isNotEmpty) {
      headers['Authorization'] = 'Bearer $token';
    }

    if (extraHeaders != null) {
      headers.addAll(extraHeaders);
    }

    return headers;
  }
}
