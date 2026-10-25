class AddDematAccountResponse {
  final bool success;
  final String? message;
  final dynamic data;

  const AddDematAccountResponse({
    required this.success,
    this.message,
    this.data,
  });

  factory AddDematAccountResponse.fromJson(Map<String, dynamic> json) {
    return AddDematAccountResponse(
      success: json['success'] as bool? ?? false,
      message: json['message'] as String?,
      data: json['data'],
    );
  }
}
