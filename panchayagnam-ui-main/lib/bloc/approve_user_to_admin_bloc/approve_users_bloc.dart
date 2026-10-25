import 'package:flutter_bloc/flutter_bloc.dart';
import '../../service/service_class.dart';
part 'approve_users_event.dart';
part 'approve_users_state.dart';

class ApproveUsersBloc extends Bloc<ApproveUsersEvent, ApproveUsersState> {
  ApproveUsersBloc() : super(ApproveUsersInitial()) {
    on<FetchPendingRequests>(_onFetchAll);
    on<ReviewRequest>(_onReviewRequest);
  }

  ServiceClass serviceClass = ServiceClass();

  Future<void> _onFetchAll(
    FetchPendingRequests event,
    Emitter<ApproveUsersState> emit,
  ) async {
    emit(ApproveUsersLoading());
    try {
      final data = await serviceClass.fetchAllRequestsApi(event.client ?? "");
      emit(
        ApproveUsersLoaded(
          pending: List<Map<String, dynamic>>.from(data['pending'] ?? []),
          approved: List<Map<String, dynamic>>.from(data['approved'] ?? []),
          rejected: List<Map<String, dynamic>>.from(data['rejected'] ?? []),
          totalCount: data['totalCount'] ?? 0,
          pendingCount: data['pendingCount'] ?? 0,
          approvedCount: data['approvedCount'] ?? 0,
          rejectedCount: data['rejectedCount'] ?? 0,
        ),
      );
    } catch (e) {
      emit(ApproveUsersError('Network error: ${e.toString()}'));
    }
  }

  // Helper to extract lists from current state
  Map<String, dynamic> _extractFromState() {
    if (state is ApproveUsersLoaded) {
      final s = state as ApproveUsersLoaded;
      return {
        'pending': s.pending,
        'approved': s.approved,
        'rejected': s.rejected,
        'totalCount': s.totalCount,
        'pendingCount': s.pendingCount,
        'approvedCount': s.approvedCount,
        'rejectedCount': s.rejectedCount,
      };
    } else if (state is ApproveUsersReviewSuccess) {
      final s = state as ApproveUsersReviewSuccess;
      return {
        'pending': s.pending,
        'approved': s.approved,
        'rejected': s.rejected,
        'totalCount': s.totalCount,
        'pendingCount': s.pendingCount,
        'approvedCount': s.approvedCount,
        'rejectedCount': s.rejectedCount,
      };
    }
    return {
      'pending': <Map<String, dynamic>>[],
      'approved': <Map<String, dynamic>>[],
      'rejected': <Map<String, dynamic>>[],
      'totalCount': 0,
      'pendingCount': 0,
      'approvedCount': 0,
      'rejectedCount': 0,
    };
  }

  Future<void> _onReviewRequest(
    ReviewRequest event,
    Emitter<ApproveUsersState> emit,
  ) async {
    final current = _extractFromState();

    List<Map<String, dynamic>> pendingList = List.from(
      current['pending'] as List,
    );
    List<Map<String, dynamic>> approvedList = List.from(
      current['approved'] as List,
    );
    List<Map<String, dynamic>> rejectedList = List.from(
      current['rejected'] as List,
    );

    emit(
      ApproveUsersReviewing(
        pending: pendingList,
        approved: approvedList,
        rejected: rejectedList,
        totalCount: current['totalCount'] as int,
        pendingCount: current['pendingCount'] as int,
        approvedCount: current['approvedCount'] as int,
        rejectedCount: current['rejectedCount'] as int,
      ),
    );

    try {
      await serviceClass.reviewRequestApi(
        superAdminId: event.superAdminId,
        requestId: event.requestId,
        status: event.status,
        reason: event.reason,
      );

      // Move the request from pending to approved/rejected locally
      final movedItem = pendingList.firstWhere(
        (r) => r['requestId'] == event.requestId,
        orElse: () => {},
      );

      pendingList = pendingList
          .where((r) => r['requestId'] != event.requestId)
          .toList();

      if (movedItem.isNotEmpty) {
        final updatedItem = {
          ...movedItem,
          'status': event.status,
          'reviewedByName': 'Super Admin',
          'reviewedAt': DateTime.now().toIso8601String(),
          if (event.status == 'REJECTED' && event.reason != null)
            'reason': event.reason,
        };
        if (event.status == 'APPROVED') {
          approvedList = [...approvedList, updatedItem];
        } else {
          rejectedList = [...rejectedList, updatedItem];
        }
      }

      final newPendingCount = pendingList.length;
      final newApprovedCount = approvedList.length;
      final newRejectedCount = rejectedList.length;

      emit(
        ApproveUsersReviewSuccess(
          pending: pendingList,
          approved: approvedList,
          rejected: rejectedList,
          totalCount: newPendingCount + newApprovedCount + newRejectedCount,
          pendingCount: newPendingCount,
          approvedCount: newApprovedCount,
          rejectedCount: newRejectedCount,
          message:
              '${event.status == 'APPROVED' ? 'Approved' : 'Rejected'} successfully',
        ),
      );
    } catch (e) {
      emit(
        ApproveUsersError(
          'Network error: ${e.toString()}',
          pending: pendingList,
          approved: approvedList,
          rejected: rejectedList,
        ),
      );
    }
  }
}
