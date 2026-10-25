import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/api_url_constants.dart';
import '../constants/auth_header_helper.dart';
import '../models/dashboard_count_states_model.dart';

class DashboardCountRepository {
  final String baseUrl;

  DashboardCountRepository({this.baseUrl = ApiUrls.baseUrl});

  Future<DashboardCountModel> fetchDashboardStats(int pyId) async {
    final uri = Uri.parse('$baseUrl/api/v1/dashboard/stats/$pyId');

    final response = await http
        .get(uri, headers: AuthHeaderHelper.getHeaders())
        .timeout(
      const Duration(seconds: 30),
      onTimeout: () =>
      throw Exception('Request timed out. Please try again.'),
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> json = jsonDecode(response.body);

      // ✅ Fix: API returns status:"SUCCESS" not success:true
      final status = (json['status'] ?? '').toString().toUpperCase();
      if (status == 'SUCCESS' && json['data'] != null) {
        return DashboardCountModel.fromJson(json['data']);
      } else {
        throw Exception(json['message'] ?? 'Failed to fetch dashboard stats');
      }
    } else if (response.statusCode == 404) {
      throw Exception('Dashboard stats not found.');
    } else if (response.statusCode == 401) {
      throw Exception('Unauthorized. Please login again.');
    } else if (response.statusCode >= 500) {
      throw Exception('Server error. Please try again later.');
    } else {
      throw Exception('Failed to fetch stats (${response.statusCode})');
    }
  }
}