part of 'fetch_user_demat_bloc.dart';

abstract class FetchUserDematState {}

class FetchUserDematInitial extends FetchUserDematState {}

class FetchUserDematLoading extends FetchUserDematState {
  final int pyUserId;
  FetchUserDematLoading(this.pyUserId);
}

class FetchUserDematLoaded extends FetchUserDematState {
  final int pyUserId;
  final List<DematAccount> accounts;
  FetchUserDematLoaded(this.pyUserId, this.accounts);
}

class FetchUserDematError extends FetchUserDematState {
  final int pyUserId;
  final String message;
  FetchUserDematError(this.pyUserId, this.message);
}