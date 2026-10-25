import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../service/service_class.dart';

part 'demote_admin_event.dart';
part 'demote_admin_state.dart';

class DemoteAdminBloc extends Bloc<DemoteAdminEvent, DemoteAdminState> {
  DemoteAdminBloc() : super(DemoteAdminInitial()) {
    on<DemoteAdminUser>(_onDemoteAdmin);
  }

  final ServiceClass _serviceClass = ServiceClass();

  Future<void> _onDemoteAdmin(
    DemoteAdminUser event,
    Emitter<DemoteAdminState> emit,
  ) async {
    emit(DemoteAdminLoading(event.targetPyId));
    try {
      final result = await _serviceClass.demoteAdmin(
        superAdminPyId: event.superAdminPyId,
        targetPyId: event.targetPyId,
      );
      emit(
        DemoteAdminSuccess(
          targetPyId: result['pyId'],
          fullName: result['fullName'],
          newRole: result['newRole'],
          groupsAffected: result['groupsAffected'],
          message: result['message'],
        ),
      );
    } catch (e) {
      emit(
        DemoteAdminError(targetPyId: event.targetPyId, message: e.toString()),
      );
    }
  }
}
