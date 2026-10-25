import 'dart:convert';

import 'package:http/http.dart' as http;

import '../constants/api_url_constants.dart';
import '../models/individual_drop_downmodel.dart';

class BrokerService {
  final String baseUrl;
  BrokerService({this.baseUrl = ApiUrls.baseUrl});

  Future<List<BrokerAccount>> fetchBrokers({
    required int pyId,
    required String broker,
  }) async {
    final uri = Uri.parse('$baseUrl/pyBrokerApi/broker-accounts/grouped/$pyId');

    final response = await http.get(uri, headers: {'accept': '*/*'});

    if (response.statusCode == 200) {
      final body = jsonDecode(response.body);

      if (body['status'] == 'SUCCESS') {
        final data = body['data'] as Map<String, dynamic>;

        final brokerList = data[broker] ?? [];

        return (brokerList as List)
            .map((e) => BrokerAccount.fromJson(e))
            .toList();
      }

      throw Exception(body['message'] ?? 'Failed');
    }

    throw Exception('HTTP ${response.statusCode}');
  }
}
