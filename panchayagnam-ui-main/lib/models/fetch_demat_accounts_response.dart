import 'demat_account_model.dart';

class FetchDematAccountsResponse {
  final int statusCode;
  final String status;
  final String message;
  final List<DematAccountModel> data;

  FetchDematAccountsResponse({
    required this.statusCode,
    required this.status,
    required this.message,
    required this.data,
  });

  factory FetchDematAccountsResponse.fromJson(
      Map<String, dynamic> json) {
    return FetchDematAccountsResponse(
      statusCode: json['statusCode'],
      status: json['status'],
      message: json['message'],
      data: (json['data'] as List)
          .map((e) => DematAccountModel.fromJson(e))
          .toList(),
    );
  }
}