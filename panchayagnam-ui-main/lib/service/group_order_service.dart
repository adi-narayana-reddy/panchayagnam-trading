import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/api_url_constants.dart';
import '../constants/auth_header_helper.dart';

const _baseUrl = ApiUrls.baseUrl;

class TradeGroupService {
  Future<List<Map<String, dynamic>>> fetchAllGroups(int pyId) async {
    final uri = Uri.parse('$_baseUrl/api/v1/trade-group/all/$pyId');
    final response = await http.get(uri, headers: AuthHeaderHelper.getHeaders());
    if (response.statusCode == 200) {
      final body = jsonDecode(response.body);
      if (body['status'] == 'SUCCESS') {
        return List<Map<String, dynamic>>.from(body['data'] ?? []);
      }
      throw Exception(body['message'] ?? 'Failed to fetch groups');
    }
    throw Exception('Server error: ${response.statusCode}');
  }

  // ── Fetch single group (fresh members + balances) ─────────────────────────
  Future<Map<String, dynamic>> fetchGroupById(int groupId) async {
    final uri = Uri.parse('$_baseUrl/api/v1/trade-group/$groupId');
    final response = await http.get(uri, headers: AuthHeaderHelper.getHeaders());
    if (response.statusCode == 200) {
      final body = jsonDecode(response.body);
      // API returns { statusCode, status: "SUCCESS", message, data: {...} }
      if (body['status'] == 'SUCCESS') {
        return Map<String, dynamic>.from(body['data']);
      }
      throw Exception(body['message'] ?? 'Failed to fetch group');
    }
    throw Exception('Server error: ${response.statusCode}');
  }

  Future<Map<String, dynamic>> groupOperation(
      Map<String, dynamic> payload,
      ) async {
    final uri = Uri.parse('$_baseUrl/api/v1/trade-group/operations');
    final response = await http.post(
      uri,
      headers: AuthHeaderHelper.getHeaders(),
      body: jsonEncode(payload),
    );
    final body = jsonDecode(response.body);
    if (response.statusCode == 200 && body['status'] == 'SUCCESS') {
      return body;
    }
    throw Exception(body['message'] ?? 'Operation failed');
  }

  Future<Map<String, dynamic>> createGroup({
    required int pyId,
    required String groupName,
    String? description,
    List<int>? brokerAccountIds,
  }) {
    return groupOperation({
      'actionType': 'CREATE',
      'pyId': pyId,
      'groupName': groupName,
      'description': description,
      'brokerAccountIds': brokerAccountIds,
    });
  }

  Future<Map<String, dynamic>> updateGroup({
    required int groupId,
    String? groupName,
    String? description,
    List<int>? brokerAccountIds,
  }) {
    return groupOperation({
      'actionType': 'UPDATE',
      'groupId': groupId,
      if (groupName != null) 'groupName': groupName,
      if (description != null) 'description': description,
      'brokerAccountIds': brokerAccountIds,
    });
  }

  Future<Map<String, dynamic>> deleteGroup(int groupId) {
    return groupOperation({'actionType': 'DELETE', 'groupId': groupId});
  }

  Future<Map<String, dynamic>> addMembers({
    required int groupId,
    required List<int> brokerAccountIds,
  }) {
    return groupOperation({
      'actionType': 'ADD_MEMBERS',
      'groupId': groupId,
      'brokerAccountIds': brokerAccountIds,
    });
  }

  Future<Map<String, dynamic>> removeMember({
    required int groupId,
    required int brokerAccountId,
  }) {
    return groupOperation({
      'actionType': 'REMOVE_MEMBER',
      'groupId': groupId,
      'brokerAccountId': brokerAccountId,
    });
  }
}