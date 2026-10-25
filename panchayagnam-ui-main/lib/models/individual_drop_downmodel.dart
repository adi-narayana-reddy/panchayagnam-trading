class BrokerAccount {
  final int brokerId;
  final String brokerClientId;
  final String userName;
  final int pyId;
  final bool isActive;
  final bool isOwn;

  BrokerAccount({
    required this.brokerId,
    required this.brokerClientId,
    required this.userName,
    required this.pyId,
    required this.isActive,
    required this.isOwn,
  });

  factory BrokerAccount.fromJson(Map<String, dynamic> json) {
    return BrokerAccount(
      brokerId: json['brokerId'] ?? 0,
      brokerClientId: json['brokerClientId'] ?? '',
      userName: json['userName'] ?? '',
      pyId: json['pyId'] ?? 0,
      isActive: json['isActive'] ?? false,
      isOwn: json['isOwn'] ?? false,
    );
  }

  String get initials {
    if (userName.isEmpty) return '';
    final parts = userName.split(' ');
    if (parts.length == 1) {
      return parts[0].substring(0, 2).toUpperCase();
    }
    return (parts[0][0] + parts[1][0]).toUpperCase();
  }
}