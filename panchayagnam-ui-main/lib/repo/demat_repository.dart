import '../models/fetch_demat_accounts_response.dart';
import '../service/service_class.dart';

abstract class DematRepository {
  Future<FetchDematAccountsResponse> fetchAccounts(bool isAdmin);
}

class DematRepositoryImpl implements DematRepository {
  final ServiceClass _service;

  DematRepositoryImpl({ServiceClass? service})
      : _service = service ?? ServiceClass();

  @override
  Future<FetchDematAccountsResponse> fetchAccounts(
      bool isAdmin) async {
    return await _service.fetchDematAccounts(isAdmin);
  }
}