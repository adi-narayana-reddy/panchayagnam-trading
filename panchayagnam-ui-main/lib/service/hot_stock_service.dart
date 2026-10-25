import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/api_url_constants.dart';
import '../constants/auth_header_helper.dart';
import '../models/hot_stock_model.dart';

class ApiResponse<T> {
  final bool success;
  final String message;
  final T? data;

  const ApiResponse({required this.success, required this.message, this.data});
}

class HotStockService {
  static  String _baseUrl = ApiUrls.baseUrl;

  Future<ApiResponse<HotStockModel?>> operations(
    HotStockOperationRequest request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/api/v1/hot-stocks/operations'),
        headers: AuthHeaderHelper.getHeaders(),
        body: jsonEncode(request.toJson()),
      );

      final body = jsonDecode(response.body) as Map<String, dynamic>;

      final isSuccess =
          response.statusCode == 200 && body['status'] == 'SUCCESS';
      final message = (body['message'] ?? '') as String;

      if (!isSuccess) {
        return ApiResponse(success: false, message: message);
      }

      final data = body['data'];
      final model = data != null
          ? HotStockModel.fromJson(data as Map<String, dynamic>)
          : null;

      return ApiResponse(success: true, message: message, data: model);
    } catch (e) {
      return ApiResponse(success: false, message: 'Network error: $e');
    }
  }

  Future<ApiResponse<List<HotStockModel>>> fetchByDateAndCategory({
    required String date,
    required HotStockCategory category,
  }) async {
    try {
      final url =
          '$_baseUrl/api/v1/hot-stocks/date/$date/category/${category.name}';

      final response = await http.get(Uri.parse(url), headers: AuthHeaderHelper.getHeaders());

      final body = jsonDecode(response.body) as Map<String, dynamic>;

      // ✅ FIX: match your actual API shape — statusCode + "status": "SUCCESS"
      final isSuccess =
          response.statusCode == 200 && body['status'] == 'SUCCESS';
      final message = (body['message'] ?? '') as String;

      if (!isSuccess) {
        return ApiResponse(success: false, message: message, data: []);
      }

      final rawList = body['data'] as List<dynamic>? ?? [];
      final stocks = rawList
          .map((e) => HotStockModel.fromJson(e as Map<String, dynamic>))
          .toList();

      return ApiResponse(success: true, message: message, data: stocks);
    } catch (e) {
      return ApiResponse(
        success: false,
        message: 'Network error: $e',
        data: [],
      );
    }
  }
}
