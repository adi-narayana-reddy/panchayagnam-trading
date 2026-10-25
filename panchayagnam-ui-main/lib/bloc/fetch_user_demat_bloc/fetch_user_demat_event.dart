part of 'fetch_user_demat_bloc.dart';

abstract class FetchUserDematEvent {}

class LoadUserDematAccounts extends FetchUserDematEvent {
  final int pyUserId;
  LoadUserDematAccounts(this.pyUserId);
}