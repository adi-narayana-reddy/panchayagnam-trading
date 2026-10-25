class DematAccountModel {
  final int id;
  final String userName;
  final String clientId;
  final String brokerType;
  final double availableCash;
  final bool active;
  final DateTime createdDate;
  final bool isAdmin;
  final String mpin;
  final String apiKey;
  final String totpKey;
  final String password;
  final String dob;
  final List<String> childIds;

  DematAccountModel({
    required this.id,
    required this.userName,
    required this.clientId,
    required this.brokerType,
    required this.availableCash,
    required this.active,
    required this.createdDate,
    required this.isAdmin,
    required this.mpin,
    required this.apiKey,
    required this.totpKey,
    required this.password,
    required this.dob,
    required this.childIds,
  });

  factory DematAccountModel.fromJson(Map<String, dynamic> json) {
    return DematAccountModel(
      id: json['id'],
      userName: json['userName'] ?? '',
      clientId: json['clientId'] ?? '',
      brokerType: json['brokerType'] ?? '',
      availableCash: (json['availableCash'] as num?)?.toDouble() ?? 0.0,
      active: json['active'] ?? false,
      createdDate: json['createdDate'] != null
          ? DateTime.parse(json['createdDate'])
          : DateTime.now(),
      isAdmin: json['isAdmin'] ?? false,
      mpin: json['mpin'] ?? '',
      apiKey: json['apiKey'] ?? '',
      totpKey: json['totpKey'] ?? '',
      password: json['password'] ?? '',
      dob: json['dob'] ?? '',
      childIds: json['childIds'] != null
          ? List<String>.from(json['childIds'])
          : <String>[],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userName': userName,
      'clientId': clientId,
      'brokerType': brokerType,
      'availableCash': availableCash,
      'active': active,
      'createdDate': createdDate.toIso8601String(),
      'isAdmin': isAdmin,
      'mpin': mpin,
      'apiKey': apiKey,
      'totpKey': totpKey,
      'password': password,
      'dob': dob,
      'childIds': childIds,
    };
  }
}