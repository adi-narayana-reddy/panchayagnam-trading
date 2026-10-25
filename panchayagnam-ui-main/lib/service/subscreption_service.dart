import 'dart:convert';
import 'package:http/http.dart' as http;

import '../constants/api_url_constants.dart';
import '../constants/auth_header_helper.dart';
import '../models/subscreption_status_model.dart';

class SubscriptionService {
  static const String _baseUrl = ApiUrls.baseUrl;
  Future<List<SubscriptionRequestModel>> fetchUserRequests(int userId,String type) async {
    final uri = Uri.parse('$_baseUrl/api/v1/user/my-requests/$userId?requestType=$type');

    final response = await http.get(uri, headers: AuthHeaderHelper.getHeaders());

    if (response.statusCode == 200) {
      final body = json.decode(response.body) as Map<String, dynamic>;

      if (body['success'] == true) {
        final List<dynamic> data = body['data'] as List<dynamic>;
        return data
            .map(
              (e) =>
                  SubscriptionRequestModel.fromJson(e as Map<String, dynamic>),
            )
            .toList();
      }

      throw SubscriptionServiceException(
        body['message']?.toString() ?? 'Failed to load requests',
      );
    }

    throw SubscriptionServiceException('Server error: ${response.statusCode}');
  }
}

class SubscriptionServiceException implements Exception {
  final String message;
  const SubscriptionServiceException(this.message);

  @override
  String toString() => message;
}
