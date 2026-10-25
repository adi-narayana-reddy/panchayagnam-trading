import '../models/add_demat_account_request.dart';
import '../models/add_demat_account_response.dart';
import '../service/service_class.dart';

abstract class AddDematAccountRepository {
  Future<AddDematAccountResponse> addAngelOneAccount(
      AddDematAccountRequest request,String actionType);
}

class AngelOneRepositoryImpl implements AddDematAccountRepository {
  final ServiceClass _service;

  AngelOneRepositoryImpl({ServiceClass? service})
      : _service = service ?? ServiceClass();

  @override
  Future<AddDematAccountResponse> addAngelOneAccount(
      AddDematAccountRequest request,String actionType) async {
    return await _service.addDematAccount(request,actionType);
  }

  void dispose() => _service.dispose();
}
