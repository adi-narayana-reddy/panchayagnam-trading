part of 'fetch_demat_bloc.dart';

abstract class FetchDematState {}

class FetchDematInitial extends FetchDematState {}

class FetchDematLoading extends FetchDematState {}

class FetchDematLoaded extends FetchDematState {
  final List<DematAccountModel> accounts;

  FetchDematLoaded(this.accounts);
}

class FetchDematError extends FetchDematState {
  final String message;

  FetchDematError(this.message);
}