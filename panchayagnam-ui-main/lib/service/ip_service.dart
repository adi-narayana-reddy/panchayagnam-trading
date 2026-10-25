import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/api_url_constants.dart';
import '../constants/auth_header_helper.dart';
import '../models/ip_model.dart';

class IpRepository {
  String _baseUrl = ApiUrls.baseUrl;

  Future<SaveIpResponse> saveIps(List<String> ips) async {
    final uri = Uri.parse('$_baseUrl/pyconfig/ip/save');
    final response = await http.post(
      uri,
      headers: AuthHeaderHelper.getHeaders(),
      body: jsonEncode(ips),
    );
    if (response.statusCode == 200) {
      return SaveIpResponse.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to save IPs: ${response.statusCode}');
  }

  // ── Fetch All IPs ───────────────────────────────────────────────────────────
  Future<FetchAllIpsResponse> fetchAllIps() async {
    final uri = Uri.parse('$_baseUrl/pyconfig/ip/all');
    final response = await http.get(uri, headers: AuthHeaderHelper.getHeaders());
    if (response.statusCode == 200) {
      return FetchAllIpsResponse.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to fetch IPs: ${response.statusCode}');
  }

  // ── IP Report ───────────────────────────────────────────────────────────────
  Future<IpReportResponse> fetchIpReport() async {
    final uri = Uri.parse('$_baseUrl/pyconfig/ip/report');
    final response = await http.get(uri, headers: AuthHeaderHelper.getHeaders());
    if (response.statusCode == 200) {
      return IpReportResponse.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to fetch IP report: ${response.statusCode}');
  }

  // ── IP Health ───────────────────────────────────────────────────────────────
  Future<IpHealthResponse> checkIpHealth(List<String> ips) async {
    final uri = Uri.parse('$_baseUrl/pyconfig/ip/health');
    final response = await http.post(
      uri,
      headers: AuthHeaderHelper.getHeaders(),
      body: jsonEncode(ips),
    );
    if (response.statusCode == 200) {
      return IpHealthResponse.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to check IP health: ${response.statusCode}');
  }
}
