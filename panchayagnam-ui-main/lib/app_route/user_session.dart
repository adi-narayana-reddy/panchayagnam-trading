class UserSession {
  static final UserSession _instance = UserSession._internal();
  factory UserSession() => _instance;
  UserSession._internal();

  String token = '';
  String role = '';
  String fullName = '';
  String email = '';
  String phoneNumber = '';
  int userId = 0;

  void set({
    required String token,
    required String role,
    required String fullName,
    required String email,
    required String phoneNumber,
    required int userId,
  }) {
    this.token = token;
    this.role = role;
    this.fullName = fullName;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.userId = userId;
  }

  void clear() {
    token = '';
    role = '';
    fullName = '';
    email = '';
    phoneNumber = '';
    userId = 0;
  }

  bool get isLoggedIn => token.isNotEmpty;

  String get initials {
    final parts = fullName.trim().split(' ');
    if (parts.length >= 2) {
      return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
    }
    return fullName.isNotEmpty ? fullName[0].toUpperCase() : '?';
  }
}