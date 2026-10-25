part of 'fetch_py_users_bloc.dart';

abstract class FetchPyUsersState {}

class FetchPyUsersInitial extends FetchPyUsersState {}

class FetchPyUsersLoading extends FetchPyUsersState {}

class FetchPyUsersLoaded extends FetchPyUsersState {
  final List<PyUser> users;
  FetchPyUsersLoaded(this.users);
}

class FetchPyUsersError extends FetchPyUsersState {
  final String message;
  FetchPyUsersError(this.message);
}