import 'dart:convert';
import 'dart:html' as html;
import 'package:http/http.dart' as http;

import '../constants/api_url_constants.dart';
import '../constants/auth_header_helper.dart';

class OAuthBrokerService {
  // ── TOTP ─────────────────────────────────────────────────────────────────────

  /// Calls GET /profitmart/oauth/generate-totp?totpKey=<key>
  /// Returns { "status": "SUCCESS", "totp": "847291" } on success.
  Future<Map<String, dynamic>> generateProfitmartTotp({
    required String totpKey,
  }) async {
    final uri = Uri.parse(
      '${ApiUrls.baseUrl}/profitmart/oauth/generate-totp?totpKey=${Uri.encodeComponent(totpKey)}',
    );

    try {
      final headers = AuthHeaderHelper.getHeaders();
      headers['Accept'] = 'application/json';
      final response = await http.get(
        uri,
        headers: headers,
      );

      final decoded = jsonDecode(response.body) as Map<String, dynamic>;

      if (response.statusCode == 200 && decoded.containsKey('totp')) {
        return {'status': 'SUCCESS', 'totp': decoded['totp'] as String};
      }

      return {
        'status': 'FAILURE',
        'message': decoded['message'] as String? ?? 'Failed to generate TOTP',
      };
    } on http.ClientException catch (e) {
      return {'status': 'FAILURE', 'message': 'Network error: ${e.message}'};
    } catch (e) {
      return {'status': 'FAILURE', 'message': e.toString()};
    }
  }

  // ── PROFITMART ────────────────────────────────────────────────────────────────

  /// Builds the Profitmart login URL and returns it WITHOUT opening a new tab.
  /// The dialog will load it inside an <iframe> / HtmlElementView.
  Future<Map<String, dynamic>> getProfitmartLoginUrl({
    required String clientId,
  }) async {
    try {
      final loginUrl =
          '${ApiUrls.baseUrl}/profitmart/oauth/getProfitLoginUrl?clientId=${Uri.encodeComponent(clientId)}';

      // Do NOT call html.window.open — the dialog embeds an iframe instead.
      return {'status': 'SUCCESS', 'loginUrl': loginUrl};
    } catch (e) {
      return {'status': 'FAILURE', 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> exchangeProfitmartToken({
    required String userId,
    required String code,
    required String secretKey,
    String? ip,
  }) async {
    const endpoint = '${ApiUrls.baseUrl}/profitmart/oauth/getAckTkn';

    try {
      final body = <String, dynamic>{
        'userId': userId,
        'code': code,
        'seceretKey': secretKey,
      };
      if (ip != null && ip.isNotEmpty) body['ip'] = ip;

      final headers = AuthHeaderHelper.getHeaders();
      headers['Content-Type'] = 'application/json';
      headers['Accept'] = 'application/json';

      final response = await http.post(
        Uri.parse(endpoint),
        headers: headers,
        body: jsonEncode(body),
      );

      final decoded = jsonDecode(response.body) as Map<String, dynamic>;

      if (response.statusCode == 200 && decoded['status'] == 'success') {
        return decoded;
      }

      return {
        'status': 'failed',
        'message': decoded['message'] as String? ??
            'Profitmart token exchange failed (HTTP ${response.statusCode})',
      };
    } on http.ClientException catch (e) {
      return {'status': 'failed', 'message': 'Network error: ${e.message}'};
    } catch (e) {
      return {'status': 'failed', 'message': e.toString()};
    }
  }

  // ── GOODWILL ──────────────────────────────────────────────────────────────────

  /// Builds the GoodWill login URL and returns it WITHOUT opening a new tab.
  Future<Map<String, dynamic>> getGoodwillLoginUrl({
    required String clientId,
  }) async {
    try {
      final loginUrl =
          '${ApiUrls.baseUrl}/goodwill/oauth/getGoodwillLoginUrl?clientId=${Uri.encodeComponent(clientId)}';

      // Do NOT call html.window.open — the dialog embeds an iframe instead.
      return {'status': 'SUCCESS', 'loginUrl': loginUrl};
    } catch (e) {
      return {'status': 'FAILURE', 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> exchangeGoodwillToken({
    required String userId,
    required String code,
    required String secretKey,
    required String apiKey,
    String? ip,
  }) async {
    const endpoint = '${ApiUrls.baseUrl}/goodwill/oauth/login';

    try {
      final body = <String, dynamic>{
        'userId': userId,
        'code': code,
        'seceretKey': secretKey,
        'apiKey': apiKey,
      };
      if (ip != null && ip.isNotEmpty) body['ip'] = ip;

      final headers = AuthHeaderHelper.getHeaders();
      headers['Content-Type'] = 'application/json';
      headers['Accept'] = 'application/json';

      final response = await http.post(
        Uri.parse(endpoint),
        headers: headers,
        body: jsonEncode(body),
      );

      final decoded = jsonDecode(response.body) as Map<String, dynamic>;

      if (response.statusCode == 200 && decoded['status'] == 'success') {
        return decoded;
      }

      return {
        'status': 'failed',
        'message': decoded['message'] as String? ??
            'GoodWill token exchange failed (HTTP ${response.statusCode})',
      };
    } on http.ClientException catch (e) {
      return {'status': 'failed', 'message': 'Network error: ${e.message}'};
    } catch (e) {
      return {'status': 'failed', 'message': e.toString()};
    }
  }
}