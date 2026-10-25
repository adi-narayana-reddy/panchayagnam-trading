import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/api_url_constants.dart';
import '../constants/auth_header_helper.dart';

const _baseUrl = ApiUrls.baseUrl;

class GroupPositionsService {
  // ── Fetch all batches for a group ─────────────────────────────────────────
  Future<List<Map<String, dynamic>>> fetchGroupPositions(int groupId) async {
    final uri = Uri.parse(
      '$_baseUrl/api/v1/group-order/group-positions/$groupId',
    );
    final response = await http.get(uri, headers: AuthHeaderHelper.getHeaders());
    final body = jsonDecode(response.body);
    if (response.statusCode == 200) {
      if (body['status'] == 'SUCCESS') {
        return List<Map<String, dynamic>>.from(body['data'] ?? []);
      }
      throw Exception(body['message'] ?? 'Failed to fetch positions');
    }
    throw Exception('${body['message']}');
  }

  // ── Fetch single batch positions ──────────────────────────────────────────
  Future<List<Map<String, dynamic>>> fetchBatchPositions(
    int groupId,
    String batchId,
  ) async {
    final uri = Uri.parse(
      '$_baseUrl/api/v1/group-order/group-positions/batch/$groupId?batchId=$batchId',
    );
    final response = await http.get(uri, headers: AuthHeaderHelper.getHeaders());
    if (response.statusCode == 200) {
      final body = jsonDecode(response.body);
      if (body['status'] == 'SUCCESS') {
        return List<Map<String, dynamic>>.from(body['data'] ?? []);
      }
      throw Exception(body['message'] ?? 'Failed to fetch batch positions');
    }
    throw Exception('Server error: ${response.statusCode}');
  }

  // ── Full group exit (all OPEN positions) ──────────────────────────────────
  Future<Map<String, dynamic>> fullGroupExit({
    required String groupBatchId,
    required String orderType,
    required String productType,
    required int triggerPrice,
    String variety = 'NORMAL',
  }) async {
    final uri = Uri.parse(
      '$_baseUrl/api/v1/group-order/group-exit/$groupBatchId?orderType=$orderType&productType=$productType&triggerPrice=$triggerPrice&variety=$variety',
    );
    final response = await http.post(uri, headers: AuthHeaderHelper.getHeaders());
    final body = jsonDecode(response.body);
    if (response.statusCode == 200 && body['status'] == 'SUCCESS') {
      return body;
    }
    throw Exception(body['message'] ?? 'Group exit failed');
  }

  // ── Selective group exit ───────────────────────────────────────────────────
  Future<Map<String, dynamic>> selectiveGroupExit({
    required String groupId,
    required List<int> brokerAccountIds,
    required String orderType,
    required String productType,
    required int triggerPrice,
    String variety = 'NORMAL',
  }) async {
    final uri = Uri.parse(
      '$_baseUrl/api/v1/group-order/group-exit/$groupId/selective',
    );
    final response = await http.post(
      uri,
      headers: AuthHeaderHelper.getHeaders(),
      body: jsonEncode({
        'brokerAccountIds': brokerAccountIds,
        'orderType': orderType,
        'productType': productType,
        'variety': variety,
        'triggerPrice':triggerPrice
      }),
    );
    final body = jsonDecode(response.body);
    if (response.statusCode == 200 && body['status'] == 'SUCCESS') {
      return body;
    }
    throw Exception(body['message'] ?? 'Selective exit failed');
  }

  // ── Buy One Sell One  (pass a BUY order for a single broker) ─────────────
  // Uses the existing group-order placement endpoint; caller crafts payload.
  Future<Map<String, dynamic>> placeOrderForBroker(
    Map<String, dynamic> payload,
  ) async {
    final uri = Uri.parse('$_baseUrl/api/v1/group-order/place');
    final response = await http.post(
      uri,
      headers: AuthHeaderHelper.getHeaders(),
      body: jsonEncode(payload),
    );
    final body = jsonDecode(response.body);
    if (response.statusCode == 200 && body['status'] == 'SUCCESS') {
      return body;
    }
    throw Exception(body['message'] ?? 'Order placement failed');
  }
}
