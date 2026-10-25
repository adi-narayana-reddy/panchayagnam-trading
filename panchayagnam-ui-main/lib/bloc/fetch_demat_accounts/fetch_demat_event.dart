part of 'fetch_demat_bloc.dart';

abstract class FetchDematEvent {}

class LoadDematAccounts extends FetchDematEvent {
  final bool isAdmin;

  LoadDematAccounts(this.isAdmin);
}